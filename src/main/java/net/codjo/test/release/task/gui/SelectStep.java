/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Component;
import javax.accessibility.Accessible;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import junit.extensions.jfcunit.eventdata.JMenuMouseEventData;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 * Classe permettant de Selectionner une ligne dans une {@link javax.swing.JList} ou {@link javax.swing.JTable} ou
 * {@link javax.swing.JPopupMenu}.
 */
public class SelectStep extends AbstractGuiStep {
    public static final String MULTIPLE_UNSUPPORTED_MESSAGE =
          "L'attribute 'multiple' n'est pas supporté pour des composants de type JComboBox.";
    private static final int INITIAL_VALUE = -1;
    private String name;
    private int row = INITIAL_VALUE;
    private int column = INITIAL_VALUE;
    private String label;
    private boolean multiple = false;
    private String path;
    private String mode;
    private long popupDelay = 500;
    private long listDelay = 50;


    public boolean isMultiple() {
        return multiple;
    }


    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setRow(int row) {
        this.row = row;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    public String getName() {
        return name;
    }


    public int getRow() {
        return row;
    }


    public String getLabel() {
        return label;
    }


    public int getColumn() {
        return column;
    }


    public void setColumn(int column) {
        this.column = column;
    }


    public void setPath(String path) {
        this.path = path;
    }


    public String getPath() {
        return path;
    }


    public void setMode(String mode) {
        this.mode = mode;
    }


    public String getMode() {
        return mode;
    }


    public long getPopupDelay() {
        return popupDelay;
    }


    public void setPopupDelay(long popupDelay) {
        this.popupDelay = popupDelay;
    }


    public void proceed(TestContext context) {
        NamedComponentFinder finder = new NamedComponentFinder(JComponent.class, name);
        Component comp = findOnlyOne(finder, context, getTimeout());
        if (comp == null) {
            throw new GuiFindException("Le composant '" + getName() + "' est introuvable.");
        }

        if (name == null) {
            name = comp.getName();
        }

        if (!JTree.class.isInstance(comp) && (getMode() != null)) {
            throw new GuiAssertException("Component doesn't support the attribute 'mode'");
        }

        if (!comp.isEnabled()) {
            throw new GuiConfigurationException(computeUneditableComponent(name));
        }
        try {
            if (comp instanceof JTable) {
                proceedTable(context, (JTable)comp);
            }
            else if (comp instanceof JList) {
                proceedList(context, (JList)comp);
            }
            else if (comp instanceof JTree) {
                proceedTree(context, (JTree)comp);
            }
            else if (comp instanceof JComboBox) {
                proceedCombo(context, ((JComboBox)comp));
            }
            else if (comp instanceof JPopupMenu) {
                proceedPopup(context, (JPopupMenu)comp);
            }
            else {
                throw new GuiConfigurationException("Type de composant non supporté : "
                                                    + comp.getClass().getName());
            }
        }
        catch (GuiException e) {
            throw e;
        }
        catch (Exception e) {
            throw new GuiActionException("Impossible de sélectionner le composant.", e);
        }
    }


    private void proceedPopup(TestContext context, final JPopupMenu popupMenu)
          throws Exception {
        if (isMultiple()) {
            throw new GuiConfigurationException(MULTIPLE_UNSUPPORTED_MESSAGE);
        }
        if (label == null && row <= -1) {
            throw new GuiConfigurationException("Le label ou le numéro de ligne doivent être renseignés");
        }
        else if (label != null && row > -1) {
            throw new GuiConfigurationException(computeIllegalUsageOfLabelAndRowMessage(name));
        }

        final int foundItem = findComponentToSelect(popupMenu);
        JMenuMouseEventData eventData =
              new JMenuMouseEventData(context.getTestCase(), popupMenu, new int[]{foundItem});
        eventData.setSleepTime(getPopupDelay());
        context.getHelper().enterClickAndLeave(eventData);
    }


    int findComponentToSelect(JPopupMenu popupMenu) {
        final Component[] items = popupMenu.getComponents();
        int foundItem = -1;
        if (label != null) {
            int count = 0;
            for (int i = 0; i < items.length; i++) {
                final Component itemComponent = items[i];
                if ((itemComponent instanceof JMenuItem) && (label.equals(((JMenuItem)itemComponent).getText()))) {
                    foundItem = i;
                    count++;
                }
            }
            if (count == 0) {
                throw new GuiFindException(computeUnknownLabelMessage(label, name));
            }
            if (count > 1) {
                throw new GuiFindException(computeDoubleLabelMessage(label, name));
            }
        }
        else if (row > -1 && row < items.length) {
            foundItem = row;
        }
        else if (row >= items.length) {
            throw new GuiFindException(computeBadRowMessage(row, name));
        }
        return foundItem;
    }


    private void proceedCombo(TestContext context, final JComboBox comboBox)
          throws Exception {
        if (isMultiple()) {
            throw new GuiConfigurationException(MULTIPLE_UNSUPPORTED_MESSAGE);
        }

        // Get the popup
        Accessible popup = comboBox.getUI().getAccessibleChild(comboBox, 0);
        if (popup != null && popup instanceof javax.swing.plaf.basic.ComboPopup) {
            // get the popup list
            final JList list = ((javax.swing.plaf.basic.ComboPopup)popup).getList();
            final int index = findIndexToSelect(list);
            runAwtCode(context,
                       new Runnable() {
                           public void run() {
                               comboBox.setSelectedIndex(index);
                           }
                       });
        }
    }


    private void proceedTable(TestContext context, final JTable table)
          throws Exception {
        if (row == INITIAL_VALUE && label != null && !"".equals(label.trim())) {
            row = findTableRowByLabel(table, label);
            if (row == -1) {
                throw new GuiFindException("Le label '" + label + "' n'existe pas dans la table " + name);
            }
        }
        if (row < 0 || row >= table.getRowCount()) {
            throw new GuiFindException("La ligne " + row + " n'existe pas dans la table " + name);
        }
        if (isMultiple()) {
            if (column < 0) {
                runAwtCode(context,
                           new Runnable() {
                               public void run() {
                                   toggleRows(table, row, isMultiple());
                               }
                           });
            }
            else {
                throw new GuiActionException("L'option multiple=\"true\" n'est pas supportée lorsque les "
                                             + "attributs row et column sont spécifiés.");
            }
        }
        else {
            if (column < 0) {
                runAwtCode(context,
                           new Runnable() {
                               public void run() {
                                   toggleRows(table, row, isMultiple());
                               }
                           });
            }
            else {
                runAwtCode(context,
                           new Runnable() {
                               public void run() {
                                   table.changeSelection(row, getColumn(), false, false);
                               }
                           });
            }
        }
    }


    private int findTableRowByLabel(JTable table, String theLabel) {
        int rows = table.getRowCount();
        int cols = table.getColumnCount();
        int found = -1;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (theLabel.equals(getTableCellValue(table, i, j))) {
                    if (found == -1) {
                        found = i;
                    }
                    else {
                        throw new GuiFindException(
                              "Le label '" + theLabel + "' est présent plusieurs fois dans la table " + name);
                    }
                }
            }
        }
        return found;
    }


    private String getTableCellValue(JTable table, int rowIndex, int columnIndex) {
        Object value = table.getModel().getValueAt(rowIndex, columnIndex);
        Component component = table.getCellRenderer(rowIndex, columnIndex)
              .getTableCellRendererComponent(table, value, false, false, rowIndex, columnIndex);
        if (JLabel.class.isInstance(component)) {
            return ((JLabel)component).getText();
        }
        return value.toString();
    }


    private void toggleRows(final JTable table, int rowClicked, boolean multipleActivated) {
        int[] rowsAlreadySelected = table.getSelectedRows();
        table.clearSelection();

        if (!multipleActivated) {
            table.setRowSelectionInterval(rowClicked, rowClicked);
            return;
        }

        boolean wasAlreadyActivated = false;
        for (int aRowsAlreadySelected1 : rowsAlreadySelected) {
            if (aRowsAlreadySelected1 == rowClicked) {
                wasAlreadyActivated = true;
                break;
            }
        }
        if (!wasAlreadyActivated) {
            table.setRowSelectionInterval(rowClicked, rowClicked);
        }

        for (int aRowsAlreadySelected : rowsAlreadySelected) {
            if (aRowsAlreadySelected != rowClicked) {
                table.addRowSelectionInterval(aRowsAlreadySelected, aRowsAlreadySelected);
            }
        }
    }


    private void proceedList(TestContext context, final JList list) throws Exception {
        final int computedRow = findIndexToSelect(list);
        if (isMultiple()) {
            runAwtCode(context,
                       new Runnable() {
                           public void run() {
                               list.addSelectionInterval(computedRow, computedRow);
                           }
                       });
        }
        else {
            runAwtCode(context,
                       new Runnable() {
                           public void run() {
                               list.setSelectedIndex(computedRow);
                           }
                       });
        }
    }


    private int findIndexToSelect(JList list) {
        if (row != INITIAL_VALUE && label != null) {
            throw new GuiConfigurationException(computeIllegalUsageOfLabelAndRowMessage(name));
        }
        final int computedRow;
        if (label != null) {
            computedRow = convertLabelToRow(list);
        }
        else {
            computedRow = row;
        }
        if (computedRow < 0 || computedRow >= list.getModel().getSize()) {
            throw new GuiFindException(computeBadRowMessage(computedRow, getName()));
        }
        return computedRow;
    }


    private int convertLabelToRow(JList convList) {
        int convertedRow = INITIAL_VALUE;
        for (int index = 0; index < convList.getModel().getSize(); index++) {
            String listValue = getListValue(convList, index);
            if (label.equals(listValue)) {
                if (convertedRow != INITIAL_VALUE) {
                    throw new GuiFindException(computeDoubleLabelMessage(label, name));
                }
                convertedRow = index;
            }
        }
        if (convertedRow == INITIAL_VALUE) {
            throw new GuiFindException(computeUnknownLabelMessage(label, name));
        }
        return convertedRow;
    }


    static String computeBadRowMessage(int computedRow, String component) {
        return "La ligne '" + computedRow + "' n'existe pas dans le composant '" + component + "'";
    }


    static String computeUnknownLabelMessage(String label, String component) {
        return "Le composant '" + component + "' ne contient pas le label '" + label + "'";
    }


    static String computeDoubleLabelMessage(String label, String component) {
        return "Le composant '" + component + "' contient plusieurs fois le label '" + label + "'";
    }


    static String computeUnexpectedRendererMessage(String rendererClass, String component) {
        return "Le renderer associé à la liste '" + component + "' est de type '" + rendererClass + "'. "
               + "Seuls les renderers de type JLabel sont supportés.";
    }


    static String computeIllegalUsageOfLabelAndRowMessage(String componentName) {
        return "Les attributs 'row' et 'label' du composant '" + componentName
               + "' ne peuvent pas être utilisés en même temps.";
    }


    static String computeUneditableComponent(String componentName) {
        return "Le composant '" + componentName + "' n'est pas éditable.";
    }


    private String getListValue(JList convList, int index) {
        try {
            Thread.sleep(getListDelay());
        }
        catch (InterruptedException e) {
            ;
        }
        Object at = convList.getModel().getElementAt(index);

        Component renderedComponent =
              convList.getCellRenderer()
                    .getListCellRendererComponent(convList,
                                                  at,
                                                  index,
                                                  convList.isSelectedIndex(index),
                                                  false);

        if (JLabel.class.isInstance(renderedComponent)) {
            return ((JLabel)renderedComponent).getText();
        }
        throw new GuiConfigurationException(computeUnexpectedRendererMessage(renderedComponent.getClass().getName(),
                                                                             name));
    }


    private void proceedTree(TestContext context, final JTree tree) throws Exception {
        if (getRow() != INITIAL_VALUE) {
            throw new GuiConfigurationException("L'attribut row n'est pas supporté.");
        }
        if (getColumn() != INITIAL_VALUE) {
            throw new GuiConfigurationException("L'attribut column n'est pas supporté.");
        }
        if (getPath() == null) {
            throw new GuiConfigurationException("Le path n'a pas été renseigné.");
        }

        final TreePath treePath = TreeUtils
              .convertIntoTreePath(tree, getPath(), TreeStepUtils.getConverter(getMode()));

        runAwtCode(context,
                   new Runnable() {
                       public void run() {
                           if (multiple) {
                               tree.addSelectionPath(treePath);
                           }
                           else {
                               tree.setSelectionPath(treePath);
                           }
                       }
                   });
    }


    public long getListDelay() {
        return listDelay;
    }


    public void setListDelay(long listDelay) {
        this.listDelay = listDelay;
    }
}
