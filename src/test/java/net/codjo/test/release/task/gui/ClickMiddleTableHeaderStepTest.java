package net.codjo.test.release.task.gui;

import javax.swing.table.JTableHeader;
import javax.swing.*;
import java.awt.event.MouseEvent;

public class ClickMiddleTableHeaderStepTest extends AbstractClickButtonStepTestCase {
    public void test_click() {
        createTable();
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.addMouseListener(popupHelper);
        showFrame(new JScrollPane(table));

        assertEquals(AbstractClickButtonStepTestCase.BUTTON_NOT_CLICKED, buttonClicked);
        proceedStep("titreA");
        assertEquals(MouseEvent.BUTTON2, buttonClicked);

    }

    private void proceedStep(String columnName) {
        ClickButtonTableHeaderStep step = new ClickMiddleTableHeaderStep();
        step.setName(table.getName());
        step.setColumn(columnName);
        step.proceed(new TestContext(this));
    }
}
