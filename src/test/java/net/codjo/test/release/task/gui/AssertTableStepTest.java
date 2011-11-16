/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import net.codjo.test.common.LogString;
import static net.codjo.test.release.task.gui.AbstractGuiStep.AUTO_MODE;
import static net.codjo.test.release.task.gui.AbstractGuiStep.DISPLAY_MODE;
import static net.codjo.test.release.task.gui.AbstractGuiStep.MODEL_MODE;
import net.codjo.test.release.task.gui.matcher.MatcherFactory;
import net.codjo.test.release.task.gui.metainfo.MyTableCellRenderer;
import net.codjo.test.release.task.gui.metainfo.MyTableCellRendererTestBehavior;
import net.codjo.test.release.task.gui.metainfo.MyTableMock;
import net.codjo.test.release.task.gui.metainfo.MyTableMockTestBehavior;
import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;

public class AssertTableStepTest extends JFCTestCase {
    private AssertTableStep step;


    @Override
    protected void setUp() throws Exception {
        step = new AssertTableStep();
        step.setTimeout(1);
        step.setDelay(10);
        step.setWaitingNumber(100);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    public void test_defaults() throws Exception {
        assertEquals(null, step.getName());
        assertEquals(null, step.getExpected());
        assertEquals(-1, step.getRow());
        assertEquals("-1", step.getColumn());
    }


    public void test_ok_dynamicProperties() throws Exception {
        showFrame();
        TestContext context = new TestContext(this);
        context.setProperty("dynamic", "l1c2");
        step.setName("TableTest");
        step.setRow(1);
        step.setColumn("2");
        step.setExpected("${dynamic}");
        step.proceed(context);
    }


    public void test_ok() throws Exception {
        showFrame();

        step.setName("TableTest");
        step.setRow(1);
        step.setColumn("2");
        step.setExpected("l1c2");
        step.proceed(new TestContext(this));

        step.setName("TableTest");
        step.setRow(1);
        step.setColumn("Col 2");
        step.setExpected("l1c2");
        step.proceed(new TestContext(this));

        step.setName("TableTest");
        step.setRow(0);
        step.setColumn("'144'");
        step.setExpected("a");
        step.proceed(new TestContext(this));

        step.setName("TableTest");
        step.setRow(0);
        step.setColumn("'BigDecimal'");
        step.setExpected("3");
        step.proceed(new TestContext(this));
    }


    public void testInvalidBackGroundColor() {
        try {
            step.setBackground("vert");
            fail();
        }
        catch (GuiException e) {
            assertEquals(e.getMessage(), "Invalid rgb format : vert");
        }
    }


    public void testNoAssertedColorOnNoColoredRenderer() {
        showFrame(new JTable(), 4, new GenericRenderer());

        step.setName("TableTest");
        step.setRow(1);
        step.setColumn("2");
        step.setExpected("l1c2");
        step.proceed(new TestContext(this));
    }


    public void testAssertedColorOnColoredRenderer() {
        showFrame(new JTable(), 4, new GenericRenderer(new Color(50, 50, 20)));

        step.setName("TableTest");
        step.setRow(1);
        step.setColumn("2");
        step.setExpected("l1c2");
        step.setBackground("50,50,20");
        step.proceed(new TestContext(this));

        step.setBackground("10,10,200");
        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (GuiAssertException e) {
        }
    }


    public void testAssertedColorOnNoColoredRenderer() {
        showFrame(new JTable(), 4, new GenericRenderer());

        step.setName("TableTest");
        step.setRow(1);
        step.setColumn("2");
        step.setExpected("l1c2");
        step.setBackground("50,50,20");
        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (GuiAssertException e) {
        }
    }


    public void testNoAssertedColorOnColoredRenderer() {
        showFrame(new JTable(), 4, new GenericRenderer(new Color(50, 50, 20)));

        step.setName("TableTest");
        step.setRow(1);
        step.setColumn("2");
        step.setExpected("l1c2");
        step.proceed(new TestContext(this));
    }


    public void test_nok_tableNotFound() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("UneTableInconnue");
        step.setRow(1);
        step.setColumn("2");
        step.setExpected("expval");

        try {
            step.proceed(context);
            fail("Pas d'erreur alors que la table n'existe pas.");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
    }


    public void test_nok_badCell() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("TableTest");
        step.setRow(100);
        step.setColumn("200");
        step.setExpected("l1c2");

        try {
            step.proceed(context);
            fail("Pas d'erreur alors que la valeur n'est pas celle attendue.");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }

        step.setName("TableTest");
        step.setRow(1);
        step.setColumn("Col 5");
        step.setExpected("l1c2");

        try {
            step.proceed(context);
            fail("Pas d'erreur alors que la valeur n'est pas celle attendue.");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
    }


    public void test_nok_badValue() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("TableTest");
        step.setRow(1);
        step.setColumn("2");
        step.setExpected("expval");

        try {
            step.proceed(context);
            fail("Pas d'erreur alors que row et column sont invalides.");
        }
        catch (GuiAssertException ex) {
            ; // Cas normal
        }
    }


    public void test_defaultMode() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("TableTest");
        step.setRow(0);
        step.setColumn("4");

        step.setExpected("One");
        step.proceed(context);

        step.setExpected("1");
        step.proceed(context);
    }


    public void test_displayMode() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("TableTest");
        step.setRow(1);
        step.setColumn("4");
        step.setMode(DISPLAY_MODE);

        step.setExpected("Valeur nulle");
        step.proceed(context);

        step.setExpected("2");
        try {
            step.proceed(context);
            fail("Pas d'erreur alors que l'expected devrait etre 'Two'.");
        }
        catch (GuiAssertException ex) {
            ; // Cas normal
        }
    }


    public void test_modelMode() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("TableTest");
        step.setRow(1);
        step.setColumn("4");
        step.setMode(MODEL_MODE);

        step.setExpected("null");
        step.proceed(context);

        step.setExpected("Two");
        try {
            step.proceed(context);
            fail("Pas d'erreur alors que l'expected devrait etre '2'.");
        }
        catch (GuiAssertException ex) {
            ; // Cas normal
        }
    }


    public void test_badMode() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("TableTest");
        step.setRow(1);
        step.setColumn("4");
        step.setMode("noMode");

        step.setExpected("Two");
        try {
            step.proceed(context);
            fail("Pas d'erreur alors que le model n'est pas valide.");
        }
        catch (GuiAssertException ex) {
            assertEquals("Invalid value of 'mode' attribute : must be in {\"auto\", \"display\", \"model\"}.",
                         ex.getMessage());
        }
    }


    public void test_customRenderer() throws Exception {
        JFrame frame = new JFrame("Test JTable with custom renderer");

        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        JTable table =
              new JTable(new Object[][]{
                    {"a", "true"},
                    {"b", "false"}
              }, new Object[]{"Col 0", "ColWithCustomRenderer"});
        table.setName("TableTest");
        table.setDefaultRenderer(table.getColumnClass(1), new CustomRenderer());
        panel.add(new JScrollPane(table));

        frame.pack();
        frame.setVisible(true);
        flushAWT();

        TestContext context = new TestContext(this);
        step.setName("TableTest");
        step.setRow(0);
        step.setColumn("1");
        step.setMode(AUTO_MODE);
        step.setExpected("true");
        step.proceed(context);

        step.setRow(1);
        step.setColumn("1");
        step.setMode(AUTO_MODE);
        step.setExpected("false");
        step.proceed(context);
    }


    public void test_expectedRenderer() throws Exception {
        JFrame frame = new JFrame("Test JTable with custom renderer");

        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        JTable table =
              new JTable(new Object[][]{
                    {"a", true},
                    {"b", false}
              }, new Object[]{"Col 0", "ColWithCustomRenderer"});

        table.setName("TableTest");
        table.getColumnModel().getColumn(1).setCellRenderer(new CustomRenderer());
        panel.add(new JScrollPane(table));

        frame.pack();
        frame.setVisible(true);
        flushAWT();

        TestContext context = new TestContext(this);
        step.setName("TableTest");
        step.setRow(0);
        step.setColumn("1");
        step.setMode(AUTO_MODE);
        step.setExpected("true");
        step.setExpectedCellRenderer("net.codjo.test.release.task.gui.AssertTableStepTest$CustomRenderer");
        step.proceed(context);
    }


    public void test_expectedRendererFailure() throws Exception {
        JFrame frame = new JFrame("Test JTable with custom renderer");

        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        JTable table =
              new JTable(new Object[][]{
                    {"a", true},
                    {"b", false}
              }, new Object[]{"Col 0", "ColWithCustomRenderer"});

        table.setName("TableTest");
        table.getColumnModel().getColumn(1).setCellRenderer(new CustomRenderer());
        panel.add(new JScrollPane(table));

        frame.pack();
        frame.setVisible(true);
        flushAWT();

        TestContext context = new TestContext(this);
        step.setName("TableTest");
        step.setRow(0);
        step.setColumn("0");
        step.setMode(AUTO_MODE);
        step.setExpected("a");
        step.setExpectedCellRenderer("net.codjo.test.release.task.gui.AssertTableStepTest.CustomRenderer");

        try {
            step.proceed(context);
            fail();
        }
        catch (GuiAssertException e) {
            assertEquals(
                  "Composant Table 'TableTest' [ligne 0, colonne 0, mode auto] : "
                  + "renderer attendu='net.codjo.test.release.task.gui.AssertTableStepTest.CustomRenderer' obtenu='javax.swing.table.DefaultTableCellRenderer$UIResource'",
                  e.getMessage());
        }
    }


    public void test_specificTestBehaviorOnTable() throws Exception {
        JTable myComponent = new MyTableMock();
        showFrame(myComponent, 0, new MyTableCellRenderer());
        LogString logString = new LogString();
        MyTableMockTestBehavior.LOG = logString;
        step.setName(myComponent.getName());
        step.setColumn("0");
        step.setRow(0);
        step.setExpected("Hello !!!");
        step.proceed(new TestContext(this));
        logString.assertContent(MyTableMockTestBehavior.class.getSimpleName() + ".assertTable(" + myComponent
                                + ", " + step + ")");
        logString.clear();
    }


    public void test_specificTestBehaviorOnRenderer() throws Exception {
        JTable myComponent = new JTable();
        showFrame(myComponent, 0, new MyTableCellRenderer());
        LogString logString = new LogString();
        MyTableCellRendererTestBehavior.LOG = logString;
        step.setName(myComponent.getName());
        step.setColumn("0");
        step.setRow(0);
        step.setExpected("Hello !!!");
        step.proceed(new TestContext(this));
        logString.assertContent(
              MyTableCellRendererTestBehavior.class.getSimpleName() + ".assertTable(" + myComponent
              + ", " + step + ")");
        logString.clear();
    }


    public void test_matching() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("TableTest");
        step.setRow(0);
        step.setColumn("0");

        step.setExpected("c0");
        try {
            step.proceed(context);
            fail("Pas d'erreur alors que le matching ne correspond pas.");
        }
        catch (GuiAssertException ex) {
            assertEquals(
                  "Composant Table 'TableTest' [ligne 0, colonne 0, mode auto] : attendu='c0' obtenu='l0c0'",
                  ex.getMessage());
        }

        step.setMatching(MatcherFactory.STARTS_WITH_MATCHING);
        try {
            step.proceed(context);
            fail("Pas d'erreur alors que le matching ne correspond pas.");
        }
        catch (GuiAssertException ex) {
            assertEquals(
                  "Composant Table 'TableTest' [ligne 0, colonne 0, mode auto] : attendu='c0' obtenu='l0c0'",
                  ex.getMessage());
        }

        step.setMatching(MatcherFactory.CONTAINS_MATCHING);
        step.proceed(context);

        step.setMatching(MatcherFactory.ENDS_WITH_MATCHING);
        step.proceed(context);
    }


    private void showFrame() {
        showFrame(new JTable(), 4, new GenericRenderer());
    }


    private void showFrame(JTable table, int column, TableCellRenderer renderer) {
        JFrame frame = new JFrame("Test JTable");

        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        table.setModel(new DefaultTableModel(new Object[][]{
              {"l0c0", "l0c1", "l0c2", "a", "1", new BigDecimal("3")},
              {"l1c0", "l1c1", "l1c2", "b", null, new BigDecimal("5")}
        }, new Object[]{"Col 0", "Col 1", "Col 2", "144", "ColWithRenderer", "BigDecimal"}));
        table.setName("TableTest");
        table.setDefaultRenderer(table.getColumnClass(column), renderer);
        panel.add(new JScrollPane(table));

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }


    class GenericRenderer implements TableCellRenderer {

        private Color background;


        GenericRenderer() {
        }


        GenericRenderer(Color background) {
            this.background = background;
        }


        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {

            JLabel label = new JLabel();
            if ("1".equals(value)) {
                label.setText("One");
            }
            else if ("2".equals(value)) {
                label.setText("Two");
            }
            else if (value == null) {
                label.setText("Valeur nulle");
            }
            else {
                label.setText("Default");
            }

            if (background != null) {
                label.setBackground(background);
            }
            return label;
        }
    }

    private static class CustomRenderer extends JCheckBox implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            try {
                this.setSelected(Boolean.valueOf(value.toString()));
            }
            catch (Exception e) {
                this.setSelected(false);
            }
            return this;
        }
    }
}
