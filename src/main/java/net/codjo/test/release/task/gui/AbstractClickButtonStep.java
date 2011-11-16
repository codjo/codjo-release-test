package net.codjo.test.release.task.gui;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JTable;
import junit.extensions.jfcunit.eventdata.AbstractMouseEventData;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 *
 */
public abstract class AbstractClickButtonStep extends AbstractClickPopupMenuStep {
    private String path;
    private String mode;


    public String getPath() {
        return path;
    }


    public void setPath(String path) {
        this.path = path;
    }


    public String getMode() {
        return mode;
    }


    public void setMode(String mode) {
        this.mode = mode;
    }


    @Override
    public void proceed(TestContext context) {
        NamedComponentFinder finder = new NamedComponentFinder(JComponent.class, getName());
        Component comp = findOnlyOne(finder, context, finderTimeout);
        if (comp == null) {
            throw new GuiFindException("Le composant '" + getName() + "' est introuvable.");
        }

        try {
            if (!acceptAndProceed(context, comp)) {
                throw new GuiConfigurationException("Type de composant non supporté : "
                                                    + comp.getClass().getName());
            }
        }
        catch (GuiException e) {
            throw e;
        }
        catch (Exception e) {
            throw new GuiActionException("Impossible de sélectionner le composant.", e);
        }
    }


    protected abstract AbstractMouseEventData getMouseEventData(TestContext context,
                                                                JTable table,
                                                                int realColumn);


    protected abstract boolean acceptAndProceed(TestContext context, Component comp) throws Exception;
}
