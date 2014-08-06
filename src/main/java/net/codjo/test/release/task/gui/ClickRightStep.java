/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Component;
import java.awt.event.InputEvent;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import junit.extensions.jfcunit.eventdata.AbstractMouseEventData;
import junit.extensions.jfcunit.eventdata.JListMouseEventData;
import junit.extensions.jfcunit.eventdata.JTableHeaderMouseEventData;
import junit.extensions.jfcunit.eventdata.JTableMouseEventData;
import junit.extensions.jfcunit.eventdata.JTreeMouseEventData;
import junit.extensions.jfcunit.eventdata.MouseEventData;
/**
 *
 */
public class ClickRightStep extends AbstractClickButtonStep {
    private int row = -1;


    public ClickRightStep() {
        setTimeout(500);
    }


    public int getRow() {
        return row;
    }


    public void setRow(int row) {
        this.row = row;
    }


    @Override
    protected boolean acceptAndProceed(TestContext context, Component comp) throws Exception {
        if (comp instanceof JTable) {
            proceedTable(context, (JTable)comp);
        }
        else if (comp instanceof JTree) {
            proceedTree(context, (JTree)comp);
        }
        else if (comp instanceof JComboBox) {
            proceedCombo(context, (JComboBox)comp);
        }
        else if (comp instanceof JList) {
            proceedList(context, (JList)comp);
        }
        else if (comp instanceof JTextField) {
            proceedTextField(context, (JTextField)comp);
        }
        else {
            return false;
        }
        return true;
    }


    private void proceedCombo(TestContext context, JComboBox combo) {
        if (row >= combo.getModel().getSize()) {
            throw new GuiFindException("La ligne '" + getRow() + "' est introuvable dans la combo '"
                                       + getName() + "'");
        }

        combo.setSelectedIndex(row);
        MouseEventData eventData = new ClickMouseEventData(context.getTestCase(),
                                                           combo, 1,
                                                           InputEvent.BUTTON3_MASK,
                                                           true,
                                                           JTableHeaderMouseEventData.DEFAULT_SLEEPTIME);
        showPopupAndSelectItem(combo, context, eventData);
    }


    protected void proceedTable(TestContext context, JTable table) {
        if (getRow() >= table.getRowCount()) {
            throw new GuiFindException("La ligne '" + getRow() + "' est introuvable dans la table '"
                                       + getName() + "'");
        }
        int realColumn = 0;
        if (!"".equals(getColumn())) {
            realColumn = TableTools.searchColumn(table, getColumn());
        }
        if (realColumn >= table.getColumnCount()) {
            throw new GuiFindException("La colonne '" + getColumn() + "' est introuvable dans la table '"
                                       + getName() + "'");
        }

        table.setRowSelectionInterval(getRow(), getRow());
        table.setColumnSelectionInterval(realColumn, realColumn);

        showPopupAndSelectItem(table, context, getMouseEventData(context, table, realColumn));
    }


    protected void proceedTextField(TestContext context, JTextField jTextField) {
        MouseEventData eventData = new ClickMouseEventData(context.getTestCase(),
                                                           jTextField, 1,
                                                           InputEvent.BUTTON3_MASK,
                                                           true,
                                                           JTableHeaderMouseEventData.DEFAULT_SLEEPTIME);

        context.getHelper().enterClickAndLeave(eventData);
    }


    @Override
    protected AbstractMouseEventData getMouseEventData(TestContext context, JTable table, int realColumn) {
        return new JTableMouseEventData(context.getTestCase(), table, getRow(), realColumn,
                                        1, true, getTimeout());
    }


    private void proceedList(TestContext context, JList list) {
        if (row >= list.getModel().getSize()) {
            throw new GuiFindException("La ligne '" + getRow() + "' est introuvable dans la liste '"
                                       + getName() + "'");
        }

        list.setSelectionInterval(row, row);

        JListMouseEventData eventData = new JListMouseEventData(context.getTestCase(), list, row, 1, 1, true,
                                                                getTimeout());
        showPopupAndSelectItem(list, context, eventData);
    }


    private void proceedTree(TestContext context, final JTree tree)
          throws Exception {
        if (getRow() != INITIAL_ROW_VALUE) {
            throw new GuiConfigurationException("L'attribut row n'est pas supporté.");
        }

        if (getPath() == null) {
            throw new GuiConfigurationException("Le path n'a pas été renseigné.");
        }

        final TreePath treePath =
              TreeUtils.convertIntoTreePath(tree, getPath(), TreeStepUtils.getConverter(getMode()));

        JTreeMouseEventData eventData =
              new JTreeMouseEventData(context.getTestCase(), tree, treePath, 1, true, getTimeout());
        showPopupAndSelectItem(tree, context, eventData);
    }
}
