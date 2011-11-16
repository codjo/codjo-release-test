/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 * Classe permettant de Selectionner un onglet dans une {@link javax.swing.JTabbedPane}
 */
public class SelectTabStep extends AbstractGuiStep {
    private static final int INITIAL_INDEX_VALUE = -1;
    private String name;
    private String tabLabel;
    private int tabIndex = INITIAL_INDEX_VALUE;

    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }


    public String getTabLabel() {
        return tabLabel;
    }


    public void setTabLabel(String tabLabel) {
        this.tabLabel = tabLabel;
    }


    public int getTabIndex() {
        return tabIndex;
    }


    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }


    public void proceed(TestContext context) {
        NamedComponentFinder finder = new NamedComponentFinder(JComponent.class, name);
        Component comp = findOnlyOne(finder, context);
        if (comp == null) {
            throw new GuiFindException("Le composant '" + getName() + "' est introuvable.");
        }
        if (comp instanceof JTabbedPane) {
            checkAttributesUsage();
            if (tabIndex != INITIAL_INDEX_VALUE) {
                tabLabel = String.valueOf(tabIndex);
            }
            final JTabbedPane tabbedPane = (JTabbedPane)comp;
            final int index = findTabIndex(tabbedPane);
            if (index == -1 || index >= tabbedPane.getTabCount()) {
                throw new GuiFindException("L'onglet '" + tabLabel + "' est introuvable dans le composant '"
                    + getName() + "'");
            }

            try {
                runAwtCodeLater(context,
                    new Runnable() {
                        public void run() {
                            tabbedPane.setSelectedIndex(index);
                        }
                    });
            }
            catch (Exception e) {
                throw new GuiActionException("Impossible de sélectionner l'onglet.", e);
            }
        }
        else {
            throw new GuiConfigurationException("Type de composant non supporté : "
                + comp.getClass().getName());
        }
    }


    static String computeIllegalUsageOfAttributes(String name) {
        return "Les attributs 'tabIndex' et 'tabLabel' du composant '" + name
        + "' ne peuvent pas être utilisés en même temps.";
    }


    private void checkAttributesUsage() {
        if (tabIndex != INITIAL_INDEX_VALUE && tabLabel != null) {
            throw new GuiConfigurationException(computeIllegalUsageOfAttributes(getName()));
        }
    }


    private int findTabIndex(JTabbedPane tabbedPane) {
        if (tabLabel == null) {
            return -1;
        }
        try {
            return Integer.parseInt(tabLabel);
        }
        catch (NumberFormatException nfe) {
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                if (tabLabel.equals(tabbedPane.getTitleAt(i))) {
                    return i;
                }
            }
        }
        return -1;
    }
}
