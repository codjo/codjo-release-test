/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui.finder;
import java.awt.Component;
import javax.swing.RootPaneContainer;
import junit.extensions.jfcunit.finder.ComponentFinder;
/**
 * Classe permettant de rechercher des composants positionnés comme glassPane de composants de type
 * RootPaneContainer (JDialog, JFrame, and so on).
 */
public class GlassPaneComponentFinder extends ComponentFinder {
    protected String name;


    public GlassPaneComponentFinder(String name) {
        this(name, true);
    }


    public GlassPaneComponentFinder(String name, boolean mustBeVisible) {
        super(Component.class);
        this.name = name;
        setIgnoreVisibility(!mustBeVisible);
        setOperation(OP_EQUALS);
        setWait(0);
    }


    @Override
    public boolean testComponent(Component comp) {
        if (RootPaneContainer.class.isInstance(comp)) {
            Component glassPane = ((RootPaneContainer)comp).getGlassPane();
            return evaluate(glassPane.getName(), name) && glassPane.isVisible();
        }
        return false;
    }
}
