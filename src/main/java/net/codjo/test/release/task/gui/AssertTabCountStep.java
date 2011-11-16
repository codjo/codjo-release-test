package net.codjo.test.release.task.gui;
import java.awt.Component;
import javax.swing.JTabbedPane;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 *
 */
public class AssertTabCountStep extends AbstractAssertStep {

    private String name;
    private int tabCount;


    @Override
    protected void proceedOnce(TestContext context) {
        NamedComponentFinder finder = new NamedComponentFinder(JTabbedPane.class, name);
        Component comp = findOnlyOne(finder, context, 0);

        if (comp == null) {
            throw new GuiFindException(
                  "Le conteneur d'onglets (JTabbedPane) portant le nom " + name + " est introuvable");
        }

        JTabbedPane tabbedPane = (JTabbedPane)comp;

        if (tabbedPane.getTabCount() != tabCount) {

            throw new GuiFindException(new StringBuilder()
                  .append("Le conteneur d'onglets ").append(name).append(" contient ").append(tabbedPane.getTabCount())
                  .append(" onglet(s) au lieu de ").append(tabCount).toString());
        }
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

   public int getTabCount() {
        return tabCount;
    }


    public void setTabCount(int tabCount) {
        this.tabCount = tabCount;
    }
}
