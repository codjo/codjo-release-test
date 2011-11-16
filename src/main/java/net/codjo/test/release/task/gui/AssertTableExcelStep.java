/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;

import net.codjo.test.common.excel.ExcelUtil;
import net.codjo.test.common.excel.matchers.CellValueStringifier;
import net.codjo.test.release.task.Util;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellRenderer;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Classe permettant de vérifier le contenu d'une {@link javax.swing.JTable} via un fichier Excel.
 */
public class AssertTableExcelStep extends AbstractAssertStep {
    private String name;
    private String file;
    private String sheetName;
    private int expectedRowCount;
    private int expectedColumnCount = -1;
    private String excludedColumns;
    private CellValueStringifier cellValueStringifier = new CellValueStringifier();
    private boolean checkColumnOrder;


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getFile() {
        return file;
    }


    public void setFile(String file) {
        this.file = file;
    }


    public String getSheetName() {
        return sheetName;
    }


    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }


    public void setExcludedColumns(String excludedColumns) {
        this.excludedColumns = excludedColumns;
    }


    public void setExpectedRowCount(int expectedRowCount) {
        this.expectedRowCount = expectedRowCount;
    }


    public void setCheckColumnOrder(boolean checkColumnOrder) {
        this.checkColumnOrder = checkColumnOrder;
    }


    public void setExpectedColumnCount(int expectedColumnCount) {
        this.expectedColumnCount = expectedColumnCount;
    }


    @Override
    protected void proceedOnce(TestContext context) {
        NamedComponentFinder finder = new NamedComponentFinder(JTable.class, name);
        JTable table = (JTable)findOnlyOne(finder, context, 0);
        if (table == null) {
            throw new GuiFindException("La table '" + getName() + "' est introuvable.");
        }

        if (table.getRowCount() != expectedRowCount) {
            throw new GuiAssertException(computeRowCountErrorMessage(expectedRowCount, table.getRowCount()));
        }

        if (expectedColumnCount != -1 && table.getColumnCount() != expectedColumnCount) {
            throw new GuiAssertException(computeColumnCountErrorMessage(expectedColumnCount,
                                                                        table.getColumnCount()));
        }

        HSSFSheet currentSheet = getExpectedSheet(context);
        HSSFRow headRow = currentSheet.getRow(0);

        StringBuilder assertStringBuilder = new StringBuilder();
        int previousColumnIndex = -1;

        for (int i = 0; i <= headRow.getLastCellNum(); i++) {
            HSSFCell cell = headRow.getCell(i);
            if (cell == null) {
                continue;
            }
            String columnName = cell.getStringCellValue().trim();
            if (getExcludedColumns().contains(columnName)) {
                continue;
            }

            int columnIndex = TableTools.searchColumn(table, columnName);

            if (checkColumnOrder && columnIndex <= previousColumnIndex) {
                throw new ColumnOrderException(
                      "La colonne \""
                      + table.getColumnName(previousColumnIndex) + "\" de la table \"" + name
                      + "\" devrait être avant la colonne \"" + columnName + "\"");
            }

            previousColumnIndex = columnIndex;

            checkTableColumn(table, columnIndex, currentSheet, i, columnName, assertStringBuilder);
        }
        if (assertStringBuilder.length() > 0) {
            throw new GuiAssertException(assertStringBuilder.toString());
        }
    }


    public static String computeColumnCountErrorMessage(int expectedColumnCount, int actualColumnCount) {
        return "Le nombre de colonnes de la table diffère du nombre de colonnes attendu : (attendu '"
               + expectedColumnCount + "', obtenu '" + actualColumnCount + "')";
    }


    static String computeRowCountErrorMessage(int expectedRowCount, int actualRowCount) {
        return "Le nombre de lignes de la table diffère du nombre de lignes attendu : (attendu '"
               + expectedRowCount + "', obtenu '" + actualRowCount + "')";
    }


    private List<String> getExcludedColumns() {
        List<String> excludedColumnList = new ArrayList<String>();
        if (Util.isNotEmpty(excludedColumns)) {
            StringTokenizer st = new StringTokenizer(excludedColumns, ",");
            while (st.hasMoreTokens()) {
                excludedColumnList.add(st.nextToken().trim());
            }
        }
        return excludedColumnList;
    }


    private void checkTableColumn(JTable table, int columnIndex, HSSFSheet sheet, int sheetColumn,
                                  String columnName, StringBuilder assertStringBuilder) {
        for (short i = 1; i <= expectedRowCount; i++) {
            try {
                HSSFRow sheetRow = sheet.getRow(i);
                String cellValue = "";
                if (sheetRow != null) {
                    HSSFCell expectedCell = sheetRow.getCell(sheetColumn);
                    if (expectedCell != null) {
                        cellValue = cellValueStringifier.toString(expectedCell);
                    }
                }

                String tableValue = getTableValue(table, i - 1, columnIndex);
                assertExpectedValue(cellValue, tableValue, i, columnName);
            }
            catch (GuiAssertException gae) {
                assertStringBuilder.append(gae.getMessage());
            }
            catch (GuiConfigurationException e) {
                throw new GuiAssertException("Composant table '" + table + "' : cellule [" + i + ", "
                                             + sheetColumn + "] incompréhensible.");
            }
        }
    }


    private String getTableValue(JTable table, int row, int realColumn) {
        String value;
        Object modelValue = table.getValueAt(row, realColumn);
        TableCellRenderer renderer = table.getCellRenderer(row, realColumn);
        if (renderer == null) {
            value = modelValue.toString();
        }
        else {
            final Component rendererComponent =
                  renderer.getTableCellRendererComponent(table, modelValue, false, false, row, realColumn);
            if (rendererComponent instanceof JLabel) {
                value = ((JLabel)rendererComponent).getText();
            }
            else if (rendererComponent instanceof JCheckBox) {
                value = String.valueOf(((JCheckBox)rendererComponent).isSelected());
            }
            else if (rendererComponent instanceof JTree) {
                value = ((JTree)rendererComponent).getPathForRow(row).getLastPathComponent().toString();
            }
            else {
                throw new GuiAssertException("Unexpected renderer type for Table");
            }
        }
        return value.trim();
    }


    private void assertExpectedValue(String expectedValue, String tableValue, int row, String column) {
        if (replaceWhiteSpace(expectedValue).compareTo(replaceWhiteSpace(tableValue)) != 0) {
            throw new GuiAssertException(computeUnexpectedValueErrorMessage(getName(), row, column,
                                                                            expectedValue, tableValue));
        }
    }


    private String replaceWhiteSpace(String spaced) {
        return spaced.replace(Character.toChars(160)[0], Character.toChars(32)[0]);
    }


    static String computeUnexpectedValueErrorMessage(String tableName, int row, String column,
                                                     String expectedValue, String actualValue) {
        return "Composant Table '" + tableName + "' [ligne '" + row + "', colonne '" + column
               + "'] : attendu='" + expectedValue + "' obtenu='" + actualValue + "'\n";
    }


    private HSSFSheet getExpectedSheet(TestContext context) {
        String testDirectory = context.getProperty(StepPlayer.TEST_DIRECTORY);
        HSSFSheet sheet;
        try {
            HSSFWorkbook workbook = ExcelUtil.loadWorkbook(new File(testDirectory, getFile()));

            if (Util.isNotEmpty(sheetName)) {
                sheet = workbook.getSheet(sheetName);
            }
            else {
                sheet = workbook.getSheetAt(0);
            }
        }
        catch (Exception ex) {
            throw new GuiConfigurationException("impossible de charger le fichier Excel !", ex);
        }
        return sheet;
    }
}
