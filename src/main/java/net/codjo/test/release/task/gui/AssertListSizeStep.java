/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 * Classe de base pour l'assertSize d'une : {@link javax.swing.JTable} ou une {@link javax.swing.JList} ou une
 * {@link javax.swing.JComboBox}
 *
 * @version $Revision: 1.12 $
 */
public class AssertListSizeStep extends AbstractAssertStep {
    private String name;
    private int expected = -1;


    public int getExpected() {
        return expected;
    }


    public void setExpected(int expected) {
        this.expected = expected;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    @Override
    protected void proceedOnce(TestContext context) {
        NamedComponentFinder finder = new NamedComponentFinder(JComponent.class, name);
        Component comp = findOnlyOne(finder, context, 0);
        if (comp == null) {
            throw new GuiFindException("Le composant '" + getName() + "' est introuvable.");
        }

        if (comp instanceof JTable) {
            proceed(((JTable)comp).getModel().getRowCount());
        }
        else if (comp instanceof JComboBox) {
            proceed(((JComboBox)comp).getModel().getSize());
        }
        else if (comp instanceof JList) {
            proceed(((JList)comp).getModel().getSize());
        }
        else if (comp instanceof JPopupMenu) {
            proceed(((JPopupMenu)comp).getSubElements().length);
        }
        else if (comp instanceof JTree) {
            proceed(((JTree)comp).getRowCount());
        }
        else {
            throw new GuiConfigurationException("Type de composant non supporté : "
                                                + comp.getClass().getName());
        }
    }


    private void proceed(int actualSize) {
        if (expected != actualSize) {
            throw new GuiAssertException("Composant '" + getName() + "' : Nombre de lignes attendu='"
                                         + expected + "' obtenu='" + actualSize + "'");
        }
    }
}
