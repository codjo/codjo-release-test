/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import net.codjo.test.release.task.Util;
import static net.codjo.test.release.task.gui.TreeUtils.convertIntoTreePath;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 * Permet de simuler des éditions dans des cellules de JTable.
 *
 * @version $Revision: 1.8 $
 */
public class EditCellStep extends StepList {
    public static final String NO_NAME_HAS_BEEN_SET = "Aucun nom de composant n'a été spécifié.";
    private static final int INITIAL_ROW_VALUE = -1;

    private int row = INITIAL_ROW_VALUE;
    private String column;
    private String path;
    private String mode;
    private AbstractTableEditionStep editionStep;


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


    public String getPath() {
        return path;
    }


    public void setPath(String path) {
        this.path = path;
    }


    public String getMode() {
        return mode;
    }


    public void setMode(String mode) {
        this.mode = mode;
    }


    public void addAssertEnabled(AssertEnabledStep step) {
        checkOrderAndAddStep(step);
    }


    public void addAssertFrame(AssertFrameStep step) {
        checkOrderAndAddStep(step);
    }


    public void addAssertList(AssertListStep step) {
        checkOrderAndAddStep(step);
    }


    public void addAssertListSize(AssertListSizeStep step) {
        checkOrderAndAddStep(step);
    }


    public void addAssertMenu(AssertMenuStep step) {
        checkOrderAndAddStep(step);
    }


    public void addAssertSelected(AssertSelectedStep step) {
        checkOrderAndAddStep(step);
    }


    public void addAssertTable(AssertTableStep step) {
        checkOrderAndAddStep(step);
    }


    public void addAssertTableExcel(AssertTableExcelStep step) {
        checkOrderAndAddStep(step);
    }


    public void addAssertTree(AssertTreeStep step) {
        checkOrderAndAddStep(step);
    }


    public void addAssertValue(AssertValueStep step) {
        checkOrderAndAddStep(step);
    }


    public void addAssertVisible(AssertVisibleStep step) {
        checkOrderAndAddStep(step);
    }


    public void addClick(ClickStep step) {
        checkOrderAndAddStep(step);
    }


    public void addClickRight(ClickRightStep step) {
        checkOrderAndAddStep(step);
    }


    public void addCloseFrame(CloseFrameStep step) {
        checkOrderAndAddStep(step);
    }


    public void addPause(PauseStep step) {
        checkOrderAndAddStep(step);
    }


    public void addSelect(SelectStep step) {
        checkOrderAndAddStep(step);
    }


    public void addSelectTab(SelectTabStep step) {
        checkOrderAndAddStep(step);
    }


    public void addSetValue(SetValueStep step) {
        checkOrderAndAddStep(step);
    }


    public void addSleep(SleepStep step) {
        checkOrderAndAddStep(step);
    }


    public void addCancel(CancelTableEditionStep step) {
        addTableEditionStep(step);
    }


    public void addValidate(ValidateTableEditionStep step) {
        addTableEditionStep(step);
    }


    public void addEditCell(EditCellStep step) {
        checkOrderAndAddStep(step);
    }


    public void addPressKey(PressKeyStep step) {
        addStep(step);
    }


    @Override
    public void proceed(TestContext context) {
        Component comp = findComponent(context, JTable.class);
        if (comp == null) {
            comp = findComponent(context, JTree.class);
        }

        if (comp == null) {
            throw new GuiFindException(computeComponentNotFoundMessage());
        }

        if (!comp.isEnabled()) {
            throw new GuiConfigurationException("Le composant '" + getName() + "' n'est pas actif.");
        }

        Component editorComponent;
        try {
            if (comp instanceof JTable) {
                editorComponent = proceedTable((JTable)comp, context);
            }
            else if (comp instanceof JTree) {
                editorComponent = proceedTree((JTree)comp);
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
            throw new GuiActionException("Impossible d'éditer le composant.", e);
        }

        context.setCurrentComponent(editorComponent);
        if (editionStep != null) {
            editionStep.setName(getName());
        }
        super.proceed(context);
        context.setCurrentComponent(null);
    }


    private String computeComponentNotFoundMessage() {
        if (getName() == null) {
            return NO_NAME_HAS_BEEN_SET;
        }
        else {
            return "Le composant '" + getName() + "' est introuvable.";
        }
    }


    private Component findComponent(TestContext context, Class aClass) {
        NamedComponentFinder finder = new NamedComponentFinder(aClass, getName());
        return findOnlyOne(finder, context);
    }


    private Component proceedTree(JTree tree) throws InterruptedException {
        if (getRow() != INITIAL_ROW_VALUE) {
            throw new GuiConfigurationException("L'attribut row n'est pas supporté sur les arbres.");
        }
        if (getColumn() != null) {
            throw new GuiConfigurationException("L'attribut column n'est pas supporté sur les arbres.");
        }
        if (getPath() == null) {
            throw new GuiConfigurationException("Le path n'a pas été renseigné.");
        }

        if (!tree.isEditable()) {
            throw new GuiConfigurationException("L'arbre n'est pas éditable.");
        }
        final TreePath treePath = convertIntoTreePath(tree, getPath(), TreeStepUtils.getConverter(getMode()));
        tree.startEditingAtPath(treePath);

        Object node = treePath.getLastPathComponent();

        boolean isLeaf = tree.getModel().isLeaf(node);
        boolean expanded = tree.isExpanded(treePath);
        int pathRow = tree.getRowForPath(treePath);

        return tree.getCellEditor().getTreeCellEditorComponent(tree, node, true, expanded, isLeaf, pathRow);
    }


    private Component proceedTable(final JTable table, TestContext context) {
        if (getPath() != null) {
            throw new GuiConfigurationException("L'attribut path n'est pas supporté sur les tables.");
        }
        if (getMode() != null) {
            throw new GuiConfigurationException("L'attribut mode n'est pas supporté sur les tables.");
        }

        final int realColumn = TableTools.searchColumn(table, column);
        TableTools.checkTableCellExists(table, row, realColumn);
        if (!table.isCellEditable(row, realColumn)) {
            throw new GuiConfigurationException(computeNotEditableCellMessage(column));
        }

        final boolean[] editionIsPossible = new boolean[]{false};
        try {
            runAwtCode(context, new Runnable() {
                public void run() {
                    editionIsPossible[0] = table.editCellAt(row, realColumn);
                    if (!editionIsPossible[0]) {
                        throw new GuiConfigurationException(computeNotEditableCellMessage(column));
                    }
                }
            });
        }
        catch (Exception error) {
            throw new GuiException("Impossible d'editer la table '" + table.getName() + "'", error);
        }

        return table.getEditorComponent();
    }


    static String computeNotEditableCellMessage(String columnName) {
        return "La colonne '" + columnName + "' n'est pas éditable.";
    }


    static String computeStepAlreadyDefinedMessage(AbstractTableEditionStep step) {
        return "Une step '" + Util.computeClassName(step.getClass()) + "' est déjà définie.";
    }


    static String computeBadTagOrderMessage(AbstractTableEditionStep step) {
        return "La step '" + Util.computeClassName(step.getClass())
               + "' doit être la dernière balise du bloc.";
    }


    private void checkOrderAndAddStep(GuiStep step) {
        checkCancelOrValidateIsTheLast();
        addStep(step);
    }


    private void checkCancelOrValidateIsTheLast() {
        if (editionStep != null) {
            throw new GuiConfigurationException(computeBadTagOrderMessage(editionStep));
        }
    }


    private void addTableEditionStep(AbstractTableEditionStep step) {
        if (editionStep != null) {
            throw new GuiConfigurationException(computeStepAlreadyDefinedMessage(editionStep));
        }
        editionStep = step;
        addStep(step);
    }
}
