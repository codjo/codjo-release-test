package net.codjo.test.release.task.gui;
import net.codjo.test.common.LogString;
import net.codjo.test.release.task.gui.metainfo.ClickTableHeaderDescriptor;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
import junit.extensions.jfcunit.eventdata.MouseEventData;
import org.jdesktop.swingx.JXTable;
/**
 * Classe de test de {@link net.codjo.test.release.task.gui.ClickStep}.
 */
public class ClickTableHeaderStepTest extends JFCTestCase {
    private ClickTableHeaderStep step;
    private JTable table;
    private static final LogString log = new LogString();


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        step = new ClickTableHeaderStep();
        step.setTimeout(2);
        log.clear();
    }


    @Override
    protected void tearDown() throws Exception {
        TestHelper.cleanUp(this);
        log.clear();
        super.tearDown();
    }


    public void test_doubleClickOnTableHeaderSortColumn() throws Exception {
        showFrame(null);

        MyMouseAdapter myMouseAdapter = new MyMouseAdapter();
        table.getTableHeader().addMouseListener(myMouseAdapter);

        clickOnColumn(2, 2, false);

        assertEquals(2, myMouseAdapter.getClickedColumn());
    }


    public void test_clickOnTableHeader_withCTRL() throws Exception {
        showFrame(null);

        final MyMouseAdapter mouseListener = new MyMouseAdapter();
        table.getTableHeader().addMouseListener(mouseListener);

        clickOnColumn("2", 1, false, "ctrl");

        assertEquals(-1, mouseListener.getClickedColumn());
        assertEquals(MouseEvent.CTRL_MASK | MouseEvent.BUTTON1_MASK, mouseListener.getModifier());
    }


    public void test_clickOnTableHeaderComponent() throws Exception {
        MyButtonRenderer button = new MyButtonRenderer();

        showFrame(button);

        clickOnColumn(2, 1, true);

        log.assertContent(
              "MyButtonRendererTestBehavior.getComponentToClick(), MyButtonRendererTestBehavior.getComponentToClick()");
    }


    public void test_jXTable_rowsSorted() throws Exception {
        table = new JXTable(new Object[][]{
              {"A", "B", "C"},
              {"J", "B", "A"},
              {"D", "H", "Q"},
              {"J", "F", "H"},
              {"B", "D", "Y"}
        }, new Object[]{"COL0", "COL1", "COL2"});
        table.setName("table");
        JFrame frame = new JFrame();
        frame.add(new JScrollPane(table));
        frame.pack();
        frame.setVisible(true);
        frame.setSize(100, 100);
        flushAWT();
        assertColumnValues(Arrays.asList("A", "J", "D", "J", "B"), 0);
        assertColumnValues(Arrays.asList("B", "B", "H", "F", "D"), 1);
        assertColumnValues(Arrays.asList("C", "A", "Q", "H", "Y"), 2);

        clickOnColumn(0, 1, true);
        assertColumnValues(Arrays.asList("A", "B", "D", "J", "J"), 0);

        clickOnColumn(1, 1, true);
        assertColumnValues(Arrays.asList("B", "B", "D", "F", "H"), 1);

        clickOnColumn("COL2", 1, true, null);
        assertColumnValues(Arrays.asList("A", "C", "H", "Q", "Y"), 2);
    }


    private void clickOnColumn(int columnIndex, int clickCount, boolean component) {
        clickOnColumn(String.valueOf(columnIndex), clickCount, component, null);
    }


    private void clickOnColumn(final String column, final int clickCount, final boolean component, final String modifier) {
        step.setName(table.getName());
        step.setColumn(column);
        step.setCount(clickCount);
        step.setComponent(component);
        step.setModifier(modifier);
        step.proceed(new TestContext(this));
    }


    private void assertColumnValues(List<String> expectedValues,
                                    int column) {

        Collection<Object> values = new ArrayList<Object>();
        for (int i = 0; i < table.getRowCount(); i++) {
            values.add(table.getValueAt(i, column));
        }
        assertEquals(expectedValues, values);
    }


    private void showFrame(final MyButtonRenderer buttonRenderer) {
        JFrame frame = new JFrame("Frame de test pour ClickStep");

        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        table = new JTable(new Object[][]{
              {"toto", "titi", "tutu"},
              {"pim", "pam", "poum"}
        }, new Object[]{"A", "B", "C"});
        table.setName("maTable");

        if (buttonRenderer != null) {
            for (Enumeration<TableColumn> en = table.getColumnModel().getColumns(); en.hasMoreElements();) {
                TableColumn column = en.nextElement();
                column.setHeaderRenderer(buttonRenderer);
            }
        }

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }


    private class MyButtonRenderer extends JButton implements TableCellRenderer {

        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            setText(String.valueOf(value));
            return this;
        }
    }

    private class MyMouseAdapter extends MouseAdapter {
        private int clickedColumn = -1;
        private int modifier = -1;


        public int getClickedColumn() {
            return clickedColumn;
        }


        public int getModifier() {
            return modifier;
        }


        @Override
        public void mousePressed(MouseEvent event) {
            if (event.getClickCount() > 1) {
                JTableHeader tableHeader = (JTableHeader)event.getSource();
                clickedColumn = tableHeader.getColumnModel().getColumnIndexAtX(event.getX());
            }
            this.modifier = event.getModifiers();
        }
    }

    public static class MyButtonRendererTestBehavior implements ClickTableHeaderDescriptor {

        public Component getComponentToClick(Component comp, AbstractClickStep step) {
            log.info("MyButtonRendererTestBehavior.getComponentToClick()");
            return comp;
        }


        public void setReferencePointIfNeeded(MouseEventData eventData,
                                              Component component,
                                              AbstractClickStep step) {
            if (JTable.class.isInstance(component)) {
                JTable table = (JTable)component;
                int columnNumber = TableTools.searchColumn(table, step.getColumn());

                Rectangle cellRect = table.getTableHeader().getHeaderRect(columnNumber);
                cellRect.x += cellRect.width / 2;
                cellRect.y -= cellRect.height / 2;

                eventData.setPosition(MouseEventData.CUSTOM);
                eventData.setReferencePoint(cellRect.getLocation());
            }
        }
    }
}
