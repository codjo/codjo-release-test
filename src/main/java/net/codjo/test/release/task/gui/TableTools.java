/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
/**
 * Class utilitaire pour les JTable.
 */
public final class TableTools {
    private TableTools() {
    }


    public static int searchColumn(final JTable table, String colName) {
        String myColumn = colName;
        int realColumn = -1;
        try {
            realColumn = Integer.parseInt(myColumn);
        }
        catch (NumberFormatException nfe) {
            if (myColumn.startsWith("'") && myColumn.endsWith("'")) {
                myColumn = myColumn.substring(1, myColumn.length() - 1);
            }
            TableColumnModel tableColumnModel = table.getColumnModel();
            for (int index = 0; index < tableColumnModel.getColumnCount(); index++) {
                Object headerValue = tableColumnModel.getColumn(index).getHeaderValue();
                if (headerValue != null) {
                    if (headerValue.toString().equals(myColumn)) {
                        realColumn = index;
                        break;
                    }
                }
            }
        }
        if (realColumn == -1) {
            throw new GuiFindException(computeUnknownColumnMessage(table, colName));
        }
        return realColumn;
    }


    public static String computeUnknownColumnMessage(JTable table, String colName) {
        return "La colonne '" + colName + "' de la table '" + table.getName() + "' est inconnue.";
    }


    public static void checkTableCellExists(JTable table, int row, int column) {
        if (row < 0 || row >= table.getRowCount() || column < 0 || column >= table.getColumnCount()) {
            throw new GuiFindException(computeBadTableCellMessage(table, row, column));
        }
    }


    public static void checkTableHeaderExists(JTable table, int column) {
        if (table.getTableHeader() == null || column < 0 || column >= table.getColumnCount()) {
            throw new GuiFindException(computeBadTableHeaderMessage(table, column));
        }
    }


    public static String computeBadTableCellMessage(JTable table, int row, int column) {
        return "La cellule [row=" + row + ", col=" + column + "] n'existe pas dans la table "
               + table.getName();
    }


    public static String computeBadTableHeaderMessage(JTable table, int column) {
        return "Le header [col=" + column + "] n'existe pas dans la table "
               + table.getName();
    }
}
