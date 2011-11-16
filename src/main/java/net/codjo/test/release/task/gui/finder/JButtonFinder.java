/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui.finder;
import java.awt.Component;
import javax.swing.AbstractButton;
import junit.extensions.jfcunit.finder.ComponentFinder;
/**
 * Cette classe est un remplacement de {@link junit.extensions.jfcunit.finder.LabeledComponentFinder} qui ne
 * marche que pour les JLabel.
 */
class JButtonFinder extends ComponentFinder {
    private String title;


    JButtonFinder(String title) {
        super(AbstractButton.class);
        this.title = title;
        createPatternMatcher(title, false);
    }


    @Override
    public boolean testComponent(Component comp) {
        if (!super.testComponent(comp)) {
            return false;
        }

        AbstractButton button = (AbstractButton)comp;
        return evaluate(button.getText(), title);
    }
}
