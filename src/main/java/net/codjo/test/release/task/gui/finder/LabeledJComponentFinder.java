package net.codjo.test.release.task.gui.finder;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JLabel;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 *
 */
public class LabeledJComponentFinder extends NamedComponentFinder {
    private String label;


    public LabeledJComponentFinder(String label) {
        super(Component.class, label);
        this.label = label;
        createPatternMatcher(label, false);
    }


    @Override
    public boolean testComponent(final Component comp) {
        if (comp == null) {
            return false;
        }
        String componentLabel = "";
        if (isValidForProcessing(comp, JButton.class)) {
            JButton button = (JButton)comp;
            componentLabel = button.getText();
        }
        else if (isValidForProcessing(comp, JLabel.class)) {
            JLabel jlabel = (JLabel)comp;
            componentLabel = jlabel.getText();
        }
        else {
            return false;
        }

        return evaluate(componentLabel, label);
    }
}
