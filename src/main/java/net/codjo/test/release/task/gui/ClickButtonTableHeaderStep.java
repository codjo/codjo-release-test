package net.codjo.test.release.task.gui;

import java.awt.Component;
import java.awt.event.InputEvent;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

import junit.extensions.jfcunit.eventdata.AbstractMouseEventData;
import junit.extensions.jfcunit.eventdata.JTableHeaderMouseEventData;

public abstract class ClickButtonTableHeaderStep extends AbstractClickButtonStep {
    private static final int NUMBER_OF_CLICKS = 1;


    @Override
    protected boolean acceptAndProceed(TestContext context, Component comp) throws Exception {
        if (comp instanceof JTable) {
            proceedTableHeader(context, (JTable) comp);
            return true;
        }
        return false;
    }


    protected void proceedTableHeader(TestContext context, JTable table) {
        int realColumn = 0;

        if (!"".equals(getColumn())) {
            realColumn = TableTools.searchColumn(table, getColumn());
        }
        if (realColumn >= table.getColumnCount()) {
            throw new GuiFindException("La colonne '" + getColumn() + "' est introuvable dans la table '"
                    + getName() + "'");
        }

        showPopupAndSelectItem(table.getTableHeader(), context,
                getMouseEventData(context, table, realColumn));
    }


    @Override
    protected AbstractMouseEventData getMouseEventData(TestContext context, JTable table, int realColumn) {
        JTableHeader tableHeader = table.getTableHeader();

        return new JTableHeaderMouseEventData(context.getTestCase(),
                tableHeader,
                realColumn,
                NUMBER_OF_CLICKS,
                getButtonMask(),
                true,
                JTableHeaderMouseEventData.DEFAULT_SLEEPTIME);
    }

    protected abstract int getButtonMask();
}
