package net.codjo.test.release.task.gui;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 * Classe permettant d'asserter le titre d'une bordure.
 */
public class AssertTitleBorderStep extends AbstractMatchingStep {
    private String name;


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    @Override
    protected void proceedOnce(TestContext context) {
        NamedComponentFinder finder = new NamedComponentFinder(JComponent.class, name);
        Component component = findOnlyOne(finder, context, 0);
        if (component == null) {
            throw new GuiFindException("Le composant '" + getName() + "' est introuvable.");
        }

        if (component instanceof JComponent) {
            JComponent jComponent = (JComponent)component;
            Border border = jComponent.getBorder();
            if (border instanceof TitledBorder) {
                proceed((TitledBorder)border);
            }
            else {
                throw new GuiAssertException(
                      "Le composant " + component.getName()
                      + " doit posséder une bordure de type TitleBorder.");
            }
        }
        else {
            throw new GuiAssertException(
                  "Le composant " + component.getName() + " doit être un JComponent "
                  + "et doit posséder une bordure de type TitleBorder.");
        }
    }


    @Override
    protected String getComponentName() {
        return "Component '" + getName() + "' TitleBorder";
    }


    private void proceed(TitledBorder titledBorder) {
        String actualValue = titledBorder.getTitle();
        assertExpected(actualValue);
    }
}
