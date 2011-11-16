package net.codjo.test.release.task.gui;

import javax.swing.*;
import java.awt.event.InputEvent;

public class ClickMiddleTableHeaderStep extends ClickButtonTableHeaderStep {
    @Override
    protected int getButtonMask() {
        return InputEvent.BUTTON2_MASK;
    }


    @Override
    protected void proceedTableHeader(TestContext context, JTable table) {
        int realColumn = 0;

        if (!"".equals(getColumn())) {
            realColumn = TableTools.searchColumn(table, getColumn());
        }
        if (realColumn >= table.getColumnCount()) {
            throw new GuiFindException("La colonne '" + getColumn() + "' est introuvable dans la table '"
                    + getName() + "'");
        }

        context.getHelper().enterClickAndLeave(getMouseEventData(context, table, realColumn));
    }

}
