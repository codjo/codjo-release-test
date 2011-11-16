/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import net.codjo.test.release.task.gui.finder.GlassPaneComponentFinder;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.RootPaneContainer;
import junit.extensions.jfcunit.finder.Finder;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 * Classe permettant de vérifier qu'un composant est visible ou non.
 */
public class AssertVisibleStep extends AbstractAssertStep {
    private static final String VISIBLE_COMPONENT_MESSAGE = "Le composant '%s' est visible.";
    private static final String NOT_VISIBLE_COMPONENT_MESSAGE = "Le composant '%s' n'est pas visible.";
    private String name;
    private boolean expected = true;


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public boolean isExpected() {
        return expected;
    }


    public void setExpected(boolean expected) {
        this.expected = expected;
    }


    @Override
    protected void proceedOnce(TestContext context) {
        Component comp = find(context, new GlassPaneComponentFinder(name));
        if (comp != null) {
            proceedRootPaneContainer((RootPaneContainer)comp);
            return;
        }

        comp = find(context, new NamedComponentFinder(JComponent.class, name));
        if (comp != null) {
            proceedComponent(comp);
            return;
        }

        if (expected) {
            throw new GuiConfigurationException(computeNotVisibleComponentMessage(name));
        }
    }


    private void proceedRootPaneContainer(RootPaneContainer rootPaneContainer) {
        boolean internalFrameCondition
              = !JInternalFrame.class.isInstance(rootPaneContainer)
                || ((JInternalFrame)rootPaneContainer).isSelected();
        if (!expected && rootPaneContainer.getGlassPane().isVisible() && internalFrameCondition) {
            throw new GuiConfigurationException(computeVisibleComponentMessage(name));
        }
    }


    private void proceedComponent(Component comp) {
        if (!expected && comp.isVisible()) {
            throw new GuiConfigurationException(computeVisibleComponentMessage(name));
        }
    }


    private Component find(TestContext context, Finder finder) {
        return findOnlyOne(finder, context, (!expected ? 0 : getTimeout() / 1000));
    }


    static String computeVisibleComponentMessage(String componentName) {
        return String.format(VISIBLE_COMPONENT_MESSAGE, componentName);
    }


    static String computeNotVisibleComponentMessage(String componentName) {
        return String.format(NOT_VISIBLE_COMPONENT_MESSAGE, componentName);
    }
}
