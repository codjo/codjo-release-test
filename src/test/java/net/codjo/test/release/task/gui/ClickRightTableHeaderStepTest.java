package net.codjo.test.release.task.gui;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JScrollPane;
import javax.swing.table.JTableHeader;
/**
 *
 */
public class ClickRightTableHeaderStepTest extends AbstractClickButtonStepTestCase {

    public void test_assertMenuPosition() {
        createTable();
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.addMouseListener(popupHelper);
        showFrame(new JScrollPane(table));

        assertMenuPosition(0);
        assertMenuPosition("titreA");
        assertMenuPosition("titreB");
    }


    private void assertMenuPosition(String columnName) {
        proceedStep(columnName);

        Point clicked = popupHelper.getLastPointClicked();
        int column = table.getTableHeader().columnAtPoint(clicked);

        String clickedColumnName = table.getColumnName(column);
        assertEquals(columnName, clickedColumnName);
    }


    private void proceedStep(String columnName) {
        ClickButtonTableHeaderStep step = new ClickRightTableHeaderStep();
        step.setName(table.getName());
        step.setColumn(columnName);
        step.proceed(new TestContext(this));
    }


    private void assertMenuPosition(int columnIndex) {
        proceedStep(Integer.toString(columnIndex));

        Point clicked = popupHelper.getLastPointClicked();
        assertEquals(MouseEvent.BUTTON3, buttonClicked);
        int column = table.getTableHeader().columnAtPoint(clicked);

        assertEquals(columnIndex, column);
        JTableHeader tableHeader = table.getTableHeader();
        Rectangle rectangle = tableHeader.getHeaderRect(column);
        assertTrue(rectangle.contains(clicked));
    }
}
