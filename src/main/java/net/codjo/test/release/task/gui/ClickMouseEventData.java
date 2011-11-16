package net.codjo.test.release.task.gui;
import java.awt.Component;
import java.awt.Point;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.eventdata.MouseEventData;
/**
 *
 */
public class ClickMouseEventData extends MouseEventData {
    public ClickMouseEventData(TestContext context, Component comp) {
        super(context.getTestCase(), comp);
    }


    public ClickMouseEventData(JFCTestCase testCase,
                               Component component,
                               int numberOfClicks,
                               int modifiers,
                               boolean isPopupTrigger, long sleeptime) {
        super(testCase, component, numberOfClicks, modifiers, isPopupTrigger, sleeptime);
    }


    @Override
    public void insureVisibleLocation(final Point point) {
        ;
    }
}
