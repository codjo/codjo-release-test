package net.codjo.test.release.task.gui;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import junit.extensions.jfcunit.finder.NamedComponentFinder;

public class ScrollToVisibleStep extends AbstractGuiStep {
    private String name;
    private String scrollPane;


    public void proceed(TestContext context) {
        NamedComponentFinder finder = new NamedComponentFinder(JScrollPane.class, scrollPane);
        final JScrollPane container = (JScrollPane)findOnlyOne(finder, context, getTimeout());
        if (container == null) {
            throw new GuiFindException("Le JScrollPane '" + getScrollPane() + "' est introuvable.");
        }

        JComponent comp = getComponentByName(container, name);
        if (comp == null) {
            throw new GuiFindException("Le composant '" + getName() + "' est introuvable.");
        }

        Rectangle bounds = comp.getBounds();
        if (container != comp.getParent()) {
            bounds = SwingUtilities.convertRectangle(comp.getParent(), bounds, container);
        }

        final Rectangle finalBounds = bounds;
        try {
            runAwtCode(context,
                       new Runnable() {
                           public void run() {
                               container.getViewport().scrollRectToVisible(finalBounds);
                               container.repaint();
                           }
                       });
        }
        catch (Exception e) {
            throw new GuiActionException("Impossible de scroller sur le composant.", e);
        }
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getScrollPane() {
        return scrollPane;
    }


    public void setScrollPane(String scrollPane) {
        this.scrollPane = scrollPane;
    }


    public static List<JComponent> getAllJComponents(Container container) {
        List<JComponent> components = new ArrayList<JComponent>();
        getAllJComponents(container, components);
        return components;
    }


    private static void getAllJComponents(Container container, Collection<JComponent> collection) {
        if (container instanceof JComponent) {
            JComponent component = (JComponent)container;
            collection.add(component);
        }

        Component[] children = container.getComponents();
        if (children != null) {
            for (Component childComponent : children) {
                if (childComponent instanceof Container) {
                    getAllJComponents((Container)childComponent, collection);
                }
            }
        }
    }


    public static JComponent getComponentByName(Container parentContainer, String componentName) {
        for (JComponent component : getAllJComponents(parentContainer)) {
            if (componentName.equals(component.getName())) {
                return component;
            }
        }
        return null;
    }
}
