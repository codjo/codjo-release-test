/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import javax.swing.JTable;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 *
 */
public abstract class AbstractTableEditionStep extends AbstractGuiStep {
    private String name;


    protected abstract void finishEditing(JTable table);


    void setName(String name) {
        this.name = name;
    }


    public void proceed(TestContext context) {
        if (name == null) {
            throw new GuiFindException(EditCellStep.NO_NAME_HAS_BEEN_SET);
        }
        NamedComponentFinder finder = new NamedComponentFinder(JTable.class, name);
        final JTable table = (JTable)findOnlyOne(finder, context);
        if (!table.isEditing()) {
            throw new GuiConfigurationException("La table '" + name + "' n'est plus en édition.");
        }
        try {
            runAwtCodeLater(context, new Runnable() {
                public void run() {
                    finishEditing(table);
                }
            });
        }
        catch (Exception e) {
            throw new GuiException("Impossible de " + getClass().getSimpleName() + " sur '" + name + "'", e);
        }
    }
}
