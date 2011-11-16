/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import net.codjo.test.release.task.gui.finder.FrameFinder;
import java.awt.Component;
import java.awt.Window;
import javax.swing.JInternalFrame;
/**
 * Step permettant de fermer une frame.
 */
public class CloseFrameStep extends AbstractGuiStep {
    private String title;


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public void proceed(final TestContext context) {
        FrameFinder finder = new FrameFinder(getTitle());
        final Component frame = findOnlyOne(finder, context);

        if (frame == null) {
            throw new GuiFindException("La fenêtre '" + getTitle() + "' n'est pas ouverte");
        }

        try {
            runAwtCode(context,
                       new Runnable() {
                           public void run() {
                               dispose(frame);
                           }
                       });
        }
        catch (Exception error) {
            throw new GuiException("Impossible de fermer '" + getTitle() + "'", error);
        }
    }


    private void dispose(Component frame) {
        if (frame instanceof JInternalFrame) {
            ((JInternalFrame)frame).doDefaultCloseAction();
        }
        else if (frame instanceof Window) {
            Window window = (Window)frame;
            window.setVisible(false);
            window.dispose();
        }
        else {
            throw new GuiException("Type de composant non géré par la balise CloseFrameStep");
        }
    }
}
