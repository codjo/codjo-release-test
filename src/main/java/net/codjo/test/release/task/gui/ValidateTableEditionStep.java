/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import javax.swing.JTable;
/**
 * 
 */
public class ValidateTableEditionStep extends AbstractTableEditionStep {
    @Override
    protected void finishEditing(JTable table) {
        table.getCellEditor().stopCellEditing();
    }
}
