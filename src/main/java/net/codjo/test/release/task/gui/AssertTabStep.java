package net.codjo.test.release.task.gui;
import java.awt.Component;
import javax.swing.JTabbedPane;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 *
 */
public class AssertTabStep extends AbstractAssertStep {
    private String name;
    private String tabLabel;
    private int tabIndex = -1;
    private boolean selected = false;
    private boolean selectedAttributeIsSet = false;
    private Boolean enabled;


    @Override
    protected void proceedOnce(TestContext context) {
        NamedComponentFinder finder = new NamedComponentFinder(JTabbedPane.class, name);
        Component comp = findOnlyOne(finder, context, 0);

        if (comp == null) {
            throw new GuiFindException(
                  "Le conteneur d'onglets (JTabbedPane) portant le nom " + name + " est introuvable");
        }

        JTabbedPane tabbedPane = (JTabbedPane)comp;

        int indexFound = -1;

        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            String title = tabbedPane.getTitleAt(i);
            if (title.equals(tabLabel)) {
                indexFound = i;
                break;
            }
        }
        if (indexFound == -1) {
            throw new GuiFindException("L'onglet portant le nom " + tabLabel + " est introuvable");
        }
        else if (tabIndex != -1 && indexFound != tabIndex) {
            throw new GuiFindException("L'onglet " + tabLabel + " en position " + indexFound
                                       + " ne se trouve pas en position " + tabIndex);
        }

        if (selectedAttributeIsSet) {
            boolean isSelected = (indexFound == tabbedPane.getSelectedIndex());
            if (selected && !isSelected) {
                throw new GuiAssertException("L'onglet '" + tabLabel + "' n'est pas sélectionné.");
            }
            if (!selected && isSelected) {
                throw new GuiAssertException("L'onglet '" + tabLabel + "' est sélectionné.");
            }
        }

        if (enabled != null && enabled != tabbedPane.isEnabledAt(indexFound)) {
            throw new GuiAssertException(
                  "L'onglet '" + tabLabel + "' est ou n'est pas actif contrairement à ce qui a été spécifié");
        }
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
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


    public void setSelected(boolean selected) {
        this.selected = selected;
        selectedAttributeIsSet = true;
    }


    public void setEnabled(boolean isEnabled) {
        this.enabled = isEnabled;
    }


    public boolean isEnabled() {
        return enabled;
    }
}
