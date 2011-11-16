package net.codjo.test.release.task.gui;
import net.codjo.test.release.task.gui.metainfo.Introspector;
import net.codjo.test.release.task.gui.metainfo.SetCalendarDescriptor;
import java.awt.Component;
import javax.swing.JComponent;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 *
 */
public class SetCalendarStep extends AbstractGuiStep {
    private String name;
    private String value;


    public void proceed(TestContext context) {
        value = context.replaceProperties(value);
        NamedComponentFinder finder = new NamedComponentFinder(JComponent.class, name);
        final Component component = findOnlyOne(finder, context, getTimeout());
        if (component == null) {
            throw new GuiFindException("Le composant '" + getName() + "' est introuvable.");
        }

        if (!component.isEnabled()) {
            throw new GuiConfigurationException(computeUneditableComponent(name));
        }

        final SetCalendarDescriptor descriptor =
              Introspector.getTestBehavior(component.getClass(), SetCalendarDescriptor.class);
        if (descriptor != null) {
            try {
                runAwtCode(context,
                           new Runnable() {
                               public void run() {
                                   descriptor.setCalendar(component, SetCalendarStep.this);
                               }
                           });
            }
            catch (Exception e) {
                throw new GuiException("" + e.getMessage());
            }
        }
    }


    static String computeUneditableComponent(String componentName) {
        return "Le composant '" + componentName + "' n'est pas éditable.";
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }


    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }
}
