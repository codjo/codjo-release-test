/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Component;
import java.awt.Container;
import java.util.List;
import javax.swing.SwingUtilities;
import junit.extensions.jfcunit.finder.Finder;
import junit.extensions.jfcunit.finder.JPopupMenuFinder;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 * Classe de base des steps de test IHM.
 */
public abstract class AbstractGuiStep implements GuiStep {
    private int timeout = 15;
    public static final String MODEL_MODE = "model";
    public static final String DISPLAY_MODE = "display";
    public static final String AUTO_MODE = "auto";


    public int getTimeout() {
        return timeout;
    }


    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }


    protected Component findOnlyOne(Finder finder, TestContext context) {
        return findOnlyOne(finder, context, getTimeout());
    }


    /**
     * Cherche un composant donné. Dans le cas des NamedComponentFinder, renvoie une exception lorsque
     * plusieurs composants sont trouvés avec le même nom.
     *
     * @param finder le finder à utiliser
     *
     * @return le composant trouvé, ou bien <code>null</code>.
     */
    protected Component findOnlyOne(Finder finder, TestContext context, int finderTimeoutInSeconds) {
        finder.setWait(finderTimeoutInSeconds);
        Component currentComponent = context.getCurrentComponent();
        finder.setOperation(getFinderOperation());
        if (finder instanceof NamedComponentFinder) {
            String name = ((NamedComponentFinder)finder).getName();
            if (name == null && currentComponent != null) {
                return currentComponent;
            }
            return findOnlyOneNamedComponent((NamedComponentFinder)finder, currentComponent);
        }
        else if (finder instanceof JPopupMenuFinder) {
            return finder.find();
        }
        else if (currentComponent != null) {
            return currentComponent;
        }
        else {
            int index = 0;
            Component componentFound;
            do {
                componentFound = finder.find(index++);
                if (componentFound != null &&
                    (componentFound.isShowing() ||
                     SwingUtilities.windowForComponent(componentFound).isShowing())) {
                    return componentFound;
                }
            }
            while (componentFound != null);
        }
        return null;
    }


    protected int getFinderOperation() {
        return Finder.OP_EQUALS;
    }


    private Component findOnlyOneNamedComponent(NamedComponentFinder finder, Component currentComponent) {
        List components;
        if (currentComponent != null && currentComponent instanceof Container) {
            // cas EditCell et ClickRight:
            // plusieurs composants portent le meme nom mais dans des containers differents de l'IHM.
            // On fait alors la recherche a partir du composant courant issu du TestContext.
            components = finder.findAll((Container)currentComponent);
        }
        else {
            components = finder.findAll();
        }
        if (components.isEmpty()) {
            // Patch : il arrive que la méthode findAll ne renvoie rien
            // alors que find renvoie bien un composant. Allez savoir pourquoi.
            return finder.find();
        }
        else if (components.size() == 1) {
            return (Component)components.get(0);
        }
        else {
            StringBuilder message = new StringBuilder();
            message.append("Il existe ");
            message.append(components.size());
            message.append(" composants portant le nom ");
            message.append(finder.getName());
            message.append(" : ");
            for (Object componentAsObject : components) {
                Component component = (Component)componentAsObject;
                message.append("[");
                message.append(buildComponentHierarchy(component));
                message.append(" <");
                message.append(component.getClass().getName());
                message.append(">]");
            }

            throw new GuiFindException(message.toString());
        }
    }


    private String buildComponentHierarchy(Component component) {
        Component parent = component.getParent();
        if (parent != null) {
            return buildComponentHierarchy(parent) + "/" + component.getName();
        }
        else {
            return component.getName();
        }
    }


    public void runAwtCode(TestContext context, Runnable runnable)
          throws Exception {
        context.getTestCase().flushAWT();
        try {
            SwingUtilities.invokeAndWait(runnable);
        }
        finally {
            context.getTestCase().flushAWT();
        }
    }


    public void runAwtCodeLater(TestContext context, Runnable runnable)
          throws Exception {
        context.getTestCase().flushAWT();
        try {
            if (SwingUtilities.isEventDispatchThread()) {
                runnable.run();
            }
            else {
                SwingUtilities.invokeLater(runnable);
            }
        }
        finally {
            context.getTestCase().flushAWT();
        }
    }
}
