package net.codjo.test.release.task.gui.finder;
import junit.extensions.jfcunit.finder.Finder;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
import java.awt.Component;
/**
 *
 */
public class LabeledAndNamedFinder extends Finder {
    LabeledJComponentFinder labelFinder;
    NamedComponentFinder nameFinder;


    public LabeledAndNamedFinder(String name, String label) {
        this.labelFinder = new LabeledJComponentFinder(label);
        this.nameFinder = new NamedComponentFinder(Component.class, name);
    }


    @Override
    public boolean testComponent(Component comp) {
        return nameFinder.testComponent(comp) && labelFinder.testComponent(comp);
    }
}
