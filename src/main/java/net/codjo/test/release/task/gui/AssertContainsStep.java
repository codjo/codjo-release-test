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
import javax.swing.ListCellRenderer;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 * 
 */
public class AssertContainsStep extends AbstractAssertStep {
    private String name;
    private String expected;
    private String mode;

    @Override
    protected void proceedOnce(TestContext context) {
        if (mode == null) {
            setMode(AUTO_MODE);
        }
        NamedComponentFinder finder = new NamedComponentFinder(JComponent.class, name);
        Component comp = findOnlyOne(finder, context);
        if (comp == null) {
            throw new GuiFindException("Le composant '" + name + "' est introuvable.");
        }

        try {
            if (comp instanceof JComboBox) {
                proceed((JComboBox)comp);
            }
            else if (comp instanceof JList) {
                proceed((JList)comp);
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
            throw new GuiActionException("Impossible de fixer la valeur.", e);
        }
    }


    private void proceed(final JComboBox comboBox)
            throws Exception {
        if (!AUTO_MODE.equals(mode) && !DISPLAY_MODE.equals(mode) && !MODEL_MODE.equals(mode)) {
            throw new GuiAssertException("Invalid value of 'mode' attribute : must be in {\"" + AUTO_MODE
                + "\", \"" + DISPLAY_MODE + "\", \"" + MODEL_MODE + "\"}.");
        }

        final ListCellRenderer renderer = comboBox.getRenderer();
        if (renderer != null && !MODEL_MODE.equals(mode)) {
            for (int i = 0; i < comboBox.getItemCount(); i++) {
                final Component rendererComponent =
                    renderer.getListCellRendererComponent(new JList(), comboBox.getItemAt(i), i, false, false);
                if (rendererComponent instanceof JLabel) {
                    if (((JLabel)rendererComponent).getText().equals(expected)) {
                        return;
                    }
                }
                else {
                    throw new GuiAssertException("Unexpected renderer type for ComboBox");
                }
            }
        }
        if (!DISPLAY_MODE.equals(mode) || MODEL_MODE.equals(mode)) {
            for (int i = 0; i < comboBox.getItemCount(); i++) {
                Object item = comboBox.getItemAt(i);
                if (item.toString().equals(expected)) {
                    return;
                }
            }
        }

        throw new GuiAssertException("Le composant " + name + " ne contient pas la valeur " + expected);
    }


    private void proceed(final JList list) throws Exception {
        if (!AUTO_MODE.equals(mode) && !MODEL_MODE.equals(mode)) {
            throw new GuiAssertException("Invalid value of 'mode' attribute : must be in {\"" + AUTO_MODE
                + "\", \"" + MODEL_MODE + "\"}.");
        }

        for (int i = 0; i < list.getModel().getSize(); i++) {
            Object item = list.getModel().getElementAt(i);
            if (item.toString().equals(expected)) {
                return;
            }
        }
        throw new GuiAssertException("Le composant " + name + " ne contient pas la valeur " + expected);
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setExpected(String expected) {
        this.expected = expected;
    }


    public void setMode(String mode) {
        this.mode = mode;
    }
}
