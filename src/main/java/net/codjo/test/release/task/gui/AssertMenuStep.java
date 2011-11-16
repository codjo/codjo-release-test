/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Component;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JRadioButtonMenuItem;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 * Classe permettant de verifier le contenu d'un menu (radio check ) et la taille d'un menu
 */
public class AssertMenuStep extends AbstractAssertStep {
    private boolean checked;
    private String name;
    private String label;

    public String getLabel() {
        return label;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    public boolean isChecked() {
        return checked;
    }


    public void setChecked(boolean checked) {
        this.checked = checked;
    }


    @Override
    protected void proceedOnce(TestContext context) {
        NamedComponentFinder finder = new NamedComponentFinder(JComponent.class, name);
        Component comp = findOnlyOne(finder, context, 0);
        if (comp == null) {
            throw new GuiFindException("Le composant '" + getName() + "' est introuvable.");
        }

        if (comp instanceof JCheckBoxMenuItem) {
            proceed((JCheckBoxMenuItem)comp);
        }
        else if (comp instanceof JRadioButtonMenuItem) {
            proceed((JRadioButtonMenuItem)comp);
        }
        else {
            throw new GuiConfigurationException("Type de composant non supporté : "
                + comp.getClass().getName());
        }
    }


    private void proceed(JCheckBoxMenuItem checkBox) {
        assertChecked(checkBox.isSelected());
        if (label != null) {
            assertLabel(checkBox.getText());
        }
    }


    private void assertLabel(String text) {
        if (!label.equals(text)) {
            throw new GuiAssertException("Composant '" + getName() + "' : label  attendu='" + label
                + "' obtenu='" + text + "'");
        }
    }


    private void assertChecked(boolean selected) {
        if (checked != selected) {
            throw new GuiAssertException("Composant '" + getName() + "' :  attendu='" + checked
                + "' obtenu='" + selected + "'");
        }
    }


    private void proceed(JRadioButtonMenuItem radio) {
        assertChecked(radio.isSelected());
        if (label != null) {
            assertLabel(radio.getText());
        }
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }
}
