/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 * Classe permettant de vérifier le contenu d'une {@link javax.swing.JTable}.
 */
public class AssertListStep extends AbstractAssertStep {
    private String name;
    private String expected;
    private int row = -1;
    private String mode;


    public String getName() {
        return name;
    }


    public void setMode(String mode) {
        this.mode = mode;
    }


    public String getMode() {
        return mode;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getExpected() {
        return expected;
    }


    public void setExpected(String expected) {
        this.expected = expected;
    }


    public int getRow() {
        return row;
    }


    public void setRow(int row) {
        this.row = row;
    }


    @Override
    protected void proceedOnce(TestContext context) {
        expected = context.replaceProperties(expected);

        if (getMode() == null) {
            setMode(AUTO_MODE);
        }
        NamedComponentFinder finder = new NamedComponentFinder(JComponent.class, name);

        Component comp = findOnlyOne(finder, context, 0);
        if (comp == null) {
            throw new GuiFindException("Le composant '" + getName() + "' est introuvable.");
        }

        else if (comp instanceof JComboBox) {
            int listSize = ((JComboBox)comp).getModel().getSize();
            verifyRow(listSize);
            proceed((JComboBox)comp);
        }
        else if (comp instanceof JList) {
            int listSize = ((JList)comp).getModel().getSize();
            verifyRow(listSize);

            proceed((JList)comp);
        }
        else if (comp instanceof JPopupMenu) {
            int listSize = ((JPopupMenu)comp).getSubElements().length;
            verifyRow(listSize);

            proceed((JPopupMenu)comp);
        }
        else {
            throw new GuiConfigurationException("Type de composant non supporté : "
                                                + comp.getClass().getName());
        }
    }


    private void proceed(JComboBox comboBox) {
        isValidMode();

        String actualValue = "";
        final ListCellRenderer renderer = comboBox.getRenderer();
        if (renderer != null) {
            final Component rendererComponent =
                  renderer.getListCellRendererComponent(new JList(),
                                                        comboBox.getItemAt(row),
                                                        row,
                                                        false,
                                                        false);
            if (rendererComponent instanceof JLabel) {
                actualValue = ((JLabel)rendererComponent).getText();
            }
            else {
                throw new GuiAssertException("Unexpected renderer type for ComboBox");
            }
        }

        if (!expected.equals(actualValue)
            && !DISPLAY_MODE.equals(getMode())
            || MODEL_MODE.equals(getMode())
            || "".equals(actualValue)) {
            actualValue = (String)comboBox.getModel().getElementAt(row);
        }

        assertExpected(actualValue);
    }


    private void proceed(JList list) {
        isValidMode();
        String actualValue = "";
        final ListCellRenderer renderer = list.getCellRenderer();
        if (renderer != null) {
            final Component rendererComponent =
                  renderer.getListCellRendererComponent(new JList(), list.getModel().getElementAt(row), row,
                                                        false, false);
            if (rendererComponent instanceof JLabel) {
                actualValue = ((JLabel)rendererComponent).getText();
            }
            else {
                throw new GuiAssertException("Unexpected renderer type for JList");
            }
        }

        if (!expected.equals(actualValue)
            && !DISPLAY_MODE.equals(getMode())
            || MODEL_MODE.equals(getMode())
            || "".equals(actualValue)) {
            actualValue = (String)list.getModel().getElementAt(row);
        }

        assertExpected(actualValue);
    }


    private void isValidMode() {
        if (!AUTO_MODE.equals(getMode()) && !DISPLAY_MODE.equals(getMode())
            && !MODEL_MODE.equals(getMode())) {
            throw new GuiAssertException("Invalid value of 'mode' attribute : must be in {\"" + AUTO_MODE
                                         + "\", \"" + DISPLAY_MODE + "\", \"" + MODEL_MODE + "\"}.");
        }
    }


    private void proceed(JPopupMenu popupMenu) {
        String actualValue = ((JMenuItem)popupMenu.getSubElements()[row].getComponent()).getText();

        assertExpected(actualValue);
    }


    private void assertExpected(String actualValue) {
        if (!actualValue.equals(expected)) {
            throw new GuiAssertException("Composant '" + getName() + "' : attendu='" + expected
                                         + "' obtenu='" + actualValue + "'");
        }
    }


    private void verifyRow(int listSize) {
        if (row < 0 || row >= listSize) {
            throw new GuiFindException("La cellule [row=" + row + "] n'existe pas dans la liste " + name);
        }
    }
}
