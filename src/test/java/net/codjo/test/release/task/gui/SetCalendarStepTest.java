package net.codjo.test.release.task.gui;
import net.codjo.test.common.LogString;
import net.codjo.test.release.task.gui.metainfo.MyCalendarMock;
import net.codjo.test.release.task.gui.metainfo.MyCalendarMockTestBehavior;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import junit.extensions.jfcunit.JFCTestCase;
/**
 *
 */
public class SetCalendarStepTest extends JFCTestCase {
    private SetCalendarStep step = new SetCalendarStep();
    private JTextField myComponent;


    private void showFrame() {
        JFrame frame = new JFrame();

        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        myComponent = new MyCalendarMock();
        myComponent.setName("fred");
        panel.add(myComponent);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }


    public void test_specificTestBehavior() throws Exception {
        showFrame();
        LogString logString = new LogString();
        MyCalendarMockTestBehavior.LOG = logString;
        step.setName("fred");
        step.setValue("Une autre valeur");
        step.proceed(new TestContext(this));
        logString.assertContent(
              "MyCalendarMockTestBehavior.setCalendarValue(" + myComponent + ", " + step + ")");
        logString.clear();
    }
}
