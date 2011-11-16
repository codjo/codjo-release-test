/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;

import net.codjo.test.common.excel.matchers.CellValueStringifier;
import static net.codjo.test.release.task.gui.AssertTableExcelStep.computeColumnCountErrorMessage;
import static net.codjo.test.release.task.gui.AssertTableExcelStep.computeRowCountErrorMessage;
import static net.codjo.test.release.task.gui.AssertTableExcelStep.computeUnexpectedValueErrorMessage;
import java.awt.Component;
import java.awt.Graphics;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;

/**
 *
 */
public class AssertTableExcelStepTest extends JFCTestCase {
    private AssertTableExcelStep step;
    private TestContext context;
    private static final String EXCEL_TEST_FILE_NAME = "AssertTableExcelStepTest.xls";


    @Override
    protected void setUp() throws Exception {
        step = new AssertTableExcelStep();
        step.setTimeout(1);
        step.setDelay(10);
        step.setWaitingNumber(100);

        context = new TestContext(this);
        context.setProperty(StepPlayer.TEST_DIRECTORY,
                            new File(getClass().getResource(EXCEL_TEST_FILE_NAME).getPath()).getParent());
        step.setName("TableTest");
        step.setFile(EXCEL_TEST_FILE_NAME);
        step.setExpectedRowCount(2);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    public void test_assertSimpleTest() throws Exception {
        showFrame(createTable(
              new Object[][]{
                    {"val00", new BigDecimal(45)},
                    {"val10", new BigDecimal(12)}
              },
              new Object[]{"Col 0", "Col 1"}));

        step.setSheetName("test_simple");
        step.proceed(context);
    }


    public void test_assertWithColumnOrder() throws Exception {
        showFrame(createTable(
              new Object[][]{
                    {new BigDecimal(45), "val00"},
                    {new BigDecimal(12), "val10"}
              },
              new Object[]{"Col 1", "Col 0"}));

        step.setSheetName("test_simple");
        step.setCheckColumnOrder(true);

        try {
            step.proceed(context);
            fail("Column orders don't match");
        }
        catch (ColumnOrderException e) {
        }
    }


    public void test_assertWithoutColumnOrder() throws Exception {
        showFrame(createTable(
              new Object[][]{
                    {new BigDecimal(45), "val00"},
                    {new BigDecimal(12), "val10"}
              },
              new Object[]{"Col 1", "Col 0"}));

        step.setSheetName("test_simple");
        step.setCheckColumnOrder(false);

        step.proceed(context);
    }


    public void test_assertColumnOrderWithExclusions() throws Exception {
        showFrame(createTable(
              new Object[][]{
                    {new BigDecimal(45), new BigDecimal(75), "val00"},
                    {new BigDecimal(12), new BigDecimal(65), "val10"}
              },
              new Object[]{"Col 1", "Col 2", "Col 0"}));

        step.setSheetName("test_simple");
        step.setExcludedColumns("Col 2");
        step.setCheckColumnOrder(true);

        try {
            step.proceed(context);
            fail("Column orders don't match");
        }
        catch (ColumnOrderException e) {
        }
    }


    public void test_assertColumnInversionTest() throws Exception {
        showFrame(createTable(
              new Object[][]{
                    {"val00", new BigDecimal(45)},
                    {"val10", new BigDecimal(12)}
              },
              new Object[]{"Col 0", "Col 1"}));

        step.setSheetName("test_inversion_colonne");
        step.proceed(context);
    }


    public void test_assertEmptyCellTest() throws Exception {
        step.setExpectedRowCount(8);
        showFrame(createTable(new Object[][]{
              {null, ""},
              {"val00", null},
              {"val01", new BigDecimal(45)},
              {"", new BigDecimal(12)},
              {null, null},
              {"", ""},
              {"val03", null},
              {"val04", new BigDecimal(88)},
        }, new Object[]{"Col 0", "Col 1"}));

        step.setSheetName("test_cellule_vide");
        step.proceed(context);
    }


    public void test_assertFormulaTest() throws Exception {
        showFrame(createTable(
              new Object[][]{
                    {"val00", new BigDecimal(45)},
                    {"val10", new BigDecimal(12)}
              },
              new Object[]{"Col 0", "Col 1"}));

        step.setSheetName("test_formule");
        step.proceed(context);
    }


    public void test_assertKo() throws Exception {
        showFrame(createTable(
              new Object[][]{
                    {"val00", new BigDecimal(45)},
                    {"val10", new BigDecimal(12)}
              },
              new Object[]{"Col 0", "Col 1"}));

        step.setSheetName("test_ko");
        try {
            step.proceed(context);
            fail();
        }
        catch (GuiAssertException e) {
            assertEquals(computeUnexpectedValueErrorMessage("TableTest", 2, "Col 1",
                                                            "78", "12"), e.getMessage());
        }
    }


    public void test_assertKoTwoTimes() throws Exception {
        showFrame(createTable(
              new Object[][]{
                    {"val00", new BigDecimal(45 + 1)},
                    {"val10", new BigDecimal(78 + 1)}
              },
              new Object[]{"Col 0", "Col 1"}));

        step.setSheetName("test_ko");
        try {
            step.proceed(context);
            fail();
        }
        catch (GuiAssertException e) {
            assertEquals(
                  computeUnexpectedValueErrorMessage("TableTest", 1, "Col 1", "45", "46") +
                  computeUnexpectedValueErrorMessage("TableTest", 2, "Col 1", "78", "79"), e.getMessage());
        }
    }


    public void test_assertDate() throws Exception {
        showFrame(createTable(new Object[][]{
              {"val00", "25/12/2006"},
              {"val10", "10/01/2068"}
        }, new Object[]{"Col 0", "Col 1"}));

        step.setSheetName("test_date");
        step.proceed(context);
    }


    public void test_assertDateWhithFormat() throws Exception {
        step.setExpectedRowCount(3);

        showFrame(createTable(new Object[][]{
              {"val00", "10/01/2068"},
              {"val10", "25/12/06"},
              {"val20", "Wed Dec 24 00:00:00 CET 2008"}
        }, new Object[]{"Col 0", "Col 1"}));

        step.setSheetName("test_date_format");
        step.proceed(context);
    }


    public void test_assertNumberWhithFormat() throws Exception {
        step.setExpectedRowCount(5);

        Object[][] objects = new Object[][]{
              {"val00", "1369"},
              {"val10", "1 256"},
              {"val20", "1456,36"},
              {"val30", "1 222,36"},
              {"val40", "1 333,000"}
        };
        showFrame(createTable(objects, new Object[]{"Col 0", "Col 1"}));

        step.setSheetName("test_number_format");
        step.proceed(context);
    }


    public void test_assertFormula() throws Exception {
        step.setExpectedRowCount(5);

        showFrame(createTable(new Object[][]{
              {"val00", "2"},
              {"val10", "4"},
              {"val20", "3,28"},
              {"val30", "3"},
              {"val40", "327 978 496,7"}
        }, new Object[]{"Col 0", "Col 1"}));
        step.setSheetName("test_formula");
        step.proceed(context);
    }


    public void test_assertCurrentDate() {
        Date date = new Date();
        String currentDate = new SimpleDateFormat(CellValueStringifier.DATE_FORMAT).format(date);

        String curentDatePlus1Day = getRelativeDate(date, GregorianCalendar.DATE, 1);

        String curentDatePlus10Month = getRelativeDate(date, GregorianCalendar.MONTH, 10);

        String curentDatePlus100Year = getRelativeDate(date, GregorianCalendar.YEAR, 100);

        showFrame(createTable(new Object[][]{
              {"val00", currentDate, "24/01/2006", curentDatePlus10Month, curentDatePlus100Year},
              {"val10", "10/01/2068", curentDatePlus1Day, "24/01/2006", "24/01/2006"}
        }, new Object[]{"Col 0", "Col 1", "date + 1 jour", "date + 10 mois", "date + 100 ans"}));

        step.setSheetName("test_relative_date");
        step.proceed(context);
    }


    private String getRelativeDate(Date date, int dateField, int amount) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(dateField, amount);
        return new SimpleDateFormat(CellValueStringifier.DATE_FORMAT).format(calendar.getTime());
    }


    public void test_assertBoolean() throws Exception {
        showFrame(createTable(
              new Object[][]{
                    {"val00", new BigDecimal(45), Boolean.TRUE},
                    {"val10", new BigDecimal(12), Boolean.FALSE}
              },
              new Object[]{"Col 0", "Col 1", "Col 2"}));

        step.setSheetName("test_booleen");
        step.proceed(context);
    }


    public void test_assertRowCount() throws Exception {
        showFrame(createTable(
              new Object[][]{
                    {"val00", new BigDecimal(45)},
                    {"val10", new BigDecimal(12)}
              },
              new Object[]{"Col 0", "Col 1"}));

        step.setSheetName("test_simple");
        step.setExpectedRowCount(15);
        try {
            step.proceed(context);
            fail();
        }
        catch (GuiAssertException e) {
            assertEquals(computeRowCountErrorMessage(15, 2), e.getMessage());
        }
    }


    public void test_assertColumnCount() throws Exception {
        showFrame(createTable(
              new Object[][]{
                    {"val00", new BigDecimal(45)},
                    {"val10", new BigDecimal(12)}
              },
              new Object[]{"Col 0", "Col 1"}));

        step.setSheetName("test_simple");
        step.setExpectedColumnCount(3);
        try {
            step.proceed(context);
            fail();
        }
        catch (GuiAssertException e) {
            assertEquals(computeColumnCountErrorMessage(3, 2), e.getMessage());
        }

        step.setExpectedColumnCount(2);
        step.proceed(context);
    }


    public void test_assertColumnCountDefault() throws Exception {
        showFrame(createTable(
              new Object[][]{
                    {"val00", new BigDecimal(45)},
                    {"val10", new BigDecimal(12)}
              },
              new Object[]{"Col 0", "Col 1"}));

        step.setSheetName("test_simple");
        step.setExpectedRowCount(2);
        // Don't set the column count
        step.proceed(context);
    }


    public void test_assertColumnExclusionTest() throws Exception {
        showFrame(createTable(
              new Object[][]{
                    {"val00", new BigDecimal(45)},
                    {"val10", new BigDecimal(12)}
              },
              new Object[]{"Col 0", "Col 1"}));

        step.setSheetName("test_column_exclusion");
        step.setExcludedColumns("Col 2, Col 4, Col 1");
        step.proceed(context);
    }


    public void test_assertUncompleteColumnExclusionTest() throws Exception {
        showFrame(createTable(
              new Object[][]{
                    {"val00", new BigDecimal(45)},
                    {"val10", new BigDecimal(12)}
              },
              new Object[]{"Col 0", "Col 1"}));

        step.setSheetName("test_column_exclusion");
        step.setExcludedColumns("Col 2, Col 1");
        try {
            step.proceed(context);
            fail();
        }
        catch (GuiFindException e) {

        }
    }


    public void test_assertTrimedCellValues() {
        showFrame(createTable(
              new Object[][]{
                    {"  val00", new BigDecimal(45)},
                    {"val10  ", new BigDecimal(12)}
              },
              new Object[]{"Col 0", "Col 1"}));

        step.setSheetName("test_trim");
        step.proceed(context);
    }


    public void test_assertJTreeCellValue() {
        showFrame(createJTreeTable());
        step.setSheetName("test_tree_table");
        step.setExpectedRowCount(2);
        step.proceed(context);
    }


    private void showFrame(JTable table) {
        JFrame frame = new JFrame("Test JTable");

        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        panel.add(new JScrollPane(table));

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }


    private JTable createTable(Object[][] data, Object[] columnNames) {
        JTable table = new JTable(data, columnNames);
        table.setName("TableTest");
        return table;
    }


    private JTable createJTreeTable() {
        JTable table = new JTable(new DefaultTableModel(2, 1));
        table.setName("TableTest");

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        root.add(new DefaultMutableTreeNode("Node1"));
        root.add(new DefaultMutableTreeNode("Node2"));

        TreeModel treeModel = new DefaultTreeModel(root);

        TableCellRenderer cellRenderer = new TreeTableCellRenderer(treeModel, table);
        table.setDefaultRenderer(Object.class, cellRenderer);
        return table;
    }


    class TreeTableCellRenderer extends JTree implements TableCellRenderer {
        private int visibleRow;
        private final JTable treetable;


        TreeTableCellRenderer(TreeModel treeModel, JTable treetable) {
            super(treeModel);
            this.treetable = treetable;
            this.setRootVisible(false);
        }


        @Override
        public void setRowHeight(int rowHeight) {
            if (rowHeight > 0) {
                super.setRowHeight(rowHeight);
                treetable.setRowHeight(rowHeight);
            }
        }


        @Override
        public void paint(Graphics graphics) {
            graphics.translate(0, -visibleRow * getRowHeight());
            super.paint(graphics);
        }


        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            visibleRow = row;
            return this;
        }
    }
}
