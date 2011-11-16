/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import net.codjo.test.release.task.gui.finder.FrameFinder;
import net.codjo.test.release.task.gui.matcher.MatcherFactory;
import java.awt.Component;
import junit.extensions.jfcunit.finder.Finder;
/**
 * Classe permettant de vérifier l'état d'une JInternalFrame.
 */
public class AssertFrameStep extends AbstractAssertStep {
    private String title;
    private boolean closed = false;
    private String matching = MatcherFactory.EQUALS_MATCHING;


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public boolean getClosed() {
        return closed;
    }


    public void setClosed(boolean closed) {
        this.closed = closed;
    }


    public void setMatching(String matching) {
        this.matching = matching;
    }


    @Override
    protected int getFinderOperation() {
        return MatcherFactory.getFinderOperationFromMatching(matching);
    }


    @Override
    protected void proceedOnce(TestContext testContext) {
        String parsedTitle = testContext.replaceProperties(getTitle());
        Finder finder = new FrameFinder(parsedTitle);
        Component frame = findOnlyOne(finder, testContext, 0);
        if (closed) {
            if (frame != null) {
                throw new GuiAssertException("La fenêtre '" + parsedTitle + "' n'est pas fermée");
            }
        }
        else {
            if (frame == null) {
                throw new GuiAssertException("La fenêtre '" + parsedTitle + "' n'est pas ouverte");
            }
        }
    }
}
