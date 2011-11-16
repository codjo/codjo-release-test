/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import net.codjo.test.release.task.gui.metainfo.AssertTableDescriptor;
import static net.codjo.test.release.task.gui.metainfo.Introspector.getTestBehavior;
import java.awt.Color;
import java.awt.Component;
import static java.lang.Integer.valueOf;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 * Classe permettant de vérifier le contenu d'une {@link javax.swing.JTable}.
 */
public class AssertTableStep extends AbstractMatchingStep {
    private String name;
    private int row = -1;
    private String column = "-1";
    private Color background;
    private String mode = AUTO_MODE;
    private String expectedCellRenderer;


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public int getRow() {
        return row;
    }


    public void setRow(int row) {
        this.row = row;
    }


    public String getColumn() {
        return column;
    }


    public void setColumn(String column) {
        this.column = column;
    }


    public String getMode() {
        return mode;
    }


    public void setMode(String mode) {
        this.mode = mode;
    }


    public Color getBackground() {
        return background;
    }


    public void setBackground(String rgb) {
        String[] rgbArray = rgb.split(",");
        try {
            background = new Color(valueOf(rgbArray[0]), valueOf(rgbArray[1]), valueOf(rgbArray[2]));
        }
        catch (NumberFormatException e) {
            throw new GuiException("Invalid rgb format : " + rgb, e);
        }
    }


    @Override
    protected void proceedOnce(TestContext context) {
        expected = context.replaceProperties(expected);
        if (getMode() == null) {
            setMode(AUTO_MODE);
        }
        if (!AUTO_MODE.equals(getMode()) && !DISPLAY_MODE.equals(getMode()) && !MODEL_MODE
              .equals(getMode())) {
            throw new GuiAssertException("Invalid value of 'mode' attribute : must be in {\"" + AUTO_MODE
                                         + "\", \"" + DISPLAY_MODE + "\", \"" + MODEL_MODE + "\"}.");
        }
        NamedComponentFinder finder = new NamedComponentFinder(JTable.class, name);

        JTable table = (JTable)findOnlyOne(finder, context, 0);
        if (table == null) {
            throw new GuiFindException("La table '" + getName() + "' est introuvable.");
        }

        if (!proceedTableBehavior(table)) {
            if (!proceedRendererBehavior(table)) {
                proceedDefault(table);
            }
        }
    }


    private boolean proceedTableBehavior(JTable table) {
        final AssertTableDescriptor descriptor =
              getTestBehavior(table.getClass(), AssertTableDescriptor.class);
        if (descriptor != null) {
            descriptor.assertTable(table, this);
            return true;
        }
        return false;
    }


    private boolean proceedRendererBehavior(JTable table) {
        if (!MODEL_MODE.equals(mode)) {
            final AssertTableDescriptor descriptor =
                  getTestBehavior(table.getCellRenderer(row, getRealColumnIndex(table)).getClass(),
                                  AssertTableDescriptor.class);
            if (descriptor != null) {
                descriptor.assertTable(table, this);
                return true;
            }
        }
        return false;
    }


    private void proceedDefault(JTable table) {
        int realColumn = getRealColumnIndex(table);
        TableCellRenderer renderer = table.getCellRenderer(row, realColumn);
        assertExpected(getActualValue(table, realColumn, renderer));
        assertBackground(getActualBackground(table, renderer, realColumn));
        assertCellRenderer(renderer);
    }


    private Color getActualBackground(JTable table, TableCellRenderer renderer, int realColumn) {
        Object actualValue = table.getValueAt(row, realColumn);
        Component rendererComponent =
              renderer.getTableCellRendererComponent(table, actualValue, false, false, row, realColumn);

        return rendererComponent.getBackground();
    }


    private void assertBackground(Color actualBackground) {
        if (background == null) {
            return;
        }
        boolean equals = actualBackground.getRed() == background.getRed()
                         && actualBackground.getGreen() == background.getGreen()
                         && actualBackground.getBlue() == background.getBlue();

        if (!equals) {
            throw new GuiAssertException("Couleur de fond du composant '" + getName() + "' : attendu='"
                                         + background + "' obtenu='" + actualBackground + "'");
        }
    }


    private void assertCellRenderer(TableCellRenderer renderer) {
        if (expectedCellRenderer == null) {
            return;
        }

        String actualRendererName = renderer.getClass().getName();
        boolean equals = expectedCellRenderer.equals(actualRendererName);

        if (!equals) {
            throw new GuiAssertException("Composant " + getComponentName()
                                         + " : renderer attendu='" + expectedCellRenderer
                                         + "' obtenu='" + actualRendererName + "'");
        }
    }


    private int getRealColumnIndex(JTable table) {
        int realColumn = TableTools.searchColumn(table, column);
        TableTools.checkTableCellExists(table, row, realColumn);
        return realColumn;
    }


    private String getActualValue(JTable table, int realColumn, TableCellRenderer renderer) {
        Object actualValue = table.getValueAt(row, realColumn);
        if (!MODEL_MODE.equals(mode)) {
            if (renderer != null) {
                final Component rendererComponent =
                      renderer
                            .getTableCellRendererComponent(table, actualValue, false, false, row, realColumn);

                final AssertTableDescriptor descriptor =
                      getTestBehavior(table.getClass(), AssertTableDescriptor.class);

                if (descriptor != null) {
                }
                else if (rendererComponent instanceof JLabel) {
                    actualValue = ((JLabel)rendererComponent).getText();
                }
                else if (rendererComponent instanceof JCheckBox) {
                    actualValue = String.valueOf(((JCheckBox)rendererComponent).isSelected());
                }
                else {
                    throw new GuiAssertException("Unexpected renderer type for Table");
                }

                if ((!DISPLAY_MODE.equals(mode)) && (!compareWithExpectedValue(actualValue.toString()))) {
                    actualValue = table.getValueAt(row, realColumn);
                }
            }
        }
        return String.valueOf(actualValue);
    }


    @Override
    protected String getComponentName() {
        return "Table '" + getName() + "' [ligne " + row + ", colonne " + column + ", mode " + getMode()
               + "]";
    }


    public String getExpectedCellRenderer() {
        return expectedCellRenderer;
    }


    public void setExpectedCellRenderer(String expectedCellRenderer) {
        this.expectedCellRenderer = expectedCellRenderer;
    }
}
