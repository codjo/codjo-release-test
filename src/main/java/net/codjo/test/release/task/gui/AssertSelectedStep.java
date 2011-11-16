/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 * Classe permettant de vérifier la sélection d'une ligne dans une {@link javax.swing.JList}, une {@link
 * javax.swing.JTable} ou un {@link javax.swing.JTree}.
 */
public class AssertSelectedStep extends AbstractAssertStep {
    private String name;
    private int row = -1;
    private int column = -1;
    private boolean expected = true;
    private String path;
    private String mode = DISPLAY_MODE;


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


    public int getColumn() {
        return column;
    }


    public void setColumn(int column) {
        this.column = column;
    }


    public boolean isExpected() {
        return expected;
    }


    public void setExpected(boolean expected) {
        this.expected = expected;
    }


    public void setPath(String path) {
        this.path = path;
    }


    public void setMode(String mode) {
        this.mode = mode;
    }


    public String getPath() {
        return path;
    }


    public String getMode() {
        return mode;
    }


    @Override
    protected void proceedOnce(TestContext context) {
        NamedComponentFinder finder = new NamedComponentFinder(JComponent.class, name);
        Component comp = findOnlyOne(finder, context, 0);
        if (comp == null) {
            throw new GuiFindException("Le composant '" + getName() + "' est introuvable.");
        }

        if (comp instanceof JTable) {
            proceedTable((JTable)comp);
        }
        else if (comp instanceof JList) {
            proceedList((JList)comp);
        }
        else if (comp instanceof JTree) {
            proceedTree((JTree)comp);
        }
        else {
            throw new GuiConfigurationException("Type de composant non supporté : "
                                                + comp.getClass().getName());
        }
    }


    private void proceedTree(JTree tree) {

        TreePath selectionPath = tree.getSelectionPath();
        if (path == null) {

            if (expected) {
                if (selectionPath == null) {
                    throw new GuiAssertException("Au moins une sélection dans l'arbre est attendue.");
                }
                return;
            } else {
                if (selectionPath != null) {
                    throw new GuiAssertException("Aucune sélection dans l'arbre est attendue.");
                }
                return;
            }
        }

        if ((selectionPath == null) && expected) {
            if (!"".equals(path)) {
                throw new GuiAssertException("Aucun noeud de l'arbre n'est sélectionné.");
            }
            return;
        }

        if ((selectionPath != null) && !expected) {
            if ("".equals(path)) {
                return;
            }
        }

        TreePath expectedPath = TreeUtils
              .convertIntoTreePath(tree, path, TreeStepUtils.getConverter(getMode()));

        if (!expectedPath.equals(selectionPath) && expected) {
            throw new GuiAssertException(
                  "Le noeud sélectionné ne correspond pas : attendu = '" + expectedPath.toString()
                  + "' obtenu = '" + selectionPath.toString() + "'");
        }

        if (expectedPath.equals(selectionPath) && !expected) {
            throw new GuiAssertException(
                  "Noeud '" + expectedPath.toString()
                  + "' : attendu = 'non sélectionné' obtenu = 'sélectionné'");
        }
    }


    private void proceedTable(JTable table) {
        if (row < 0 || row >= table.getRowCount()) {
            throw new GuiFindException("La ligne " + row + " n'existe pas dans la table " + name);
        }

        boolean actual;
        if (column < 0) {
            actual = table.isRowSelected(getRow());
        }
        else {
            actual = table.isCellSelected(getRow(), getColumn());
        }
        assertExpected(actual);
    }


    private void proceedList(JList list) {
        if (row < 0 || row >= list.getModel().getSize()) {
            throw new GuiFindException("La ligne " + row + " n'existe pas dans la liste " + name);
        }
        boolean actual = list.isSelectedIndex(getRow());
        assertExpected(actual);
    }


    private void assertExpected(boolean actual) {
        if (expected != actual) {
            throw new GuiAssertException("Composant '" + getName() + "' : attendu='" + expected
                                         + "' obtenu='" + actual + "'");
        }
    }
}
