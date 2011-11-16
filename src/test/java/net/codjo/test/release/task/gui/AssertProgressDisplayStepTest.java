package net.codjo.test.release.task.gui;
import net.codjo.test.release.WaitingPanelMock;
import java.awt.Window;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.RootPaneContainer;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
/**
 *
 */
public class AssertProgressDisplayStepTest extends JFCTestCase {

    private AssertProgressDisplayStep step;
    private WaitingPanelMock waitingPanel;
    private TestContext testContext;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        waitingPanel = new WaitingPanelMock();
        step = new AssertProgressDisplayStep();
        step.setName(WaitingPanelMock.WAITING_PANEL_NAME);
        step.setTimeout(1);
        testContext = new TestContext(this);
    }


    @Override
    protected void tearDown() throws Exception {
        TestHelper.cleanUp(this);
        super.tearDown();
    }


    public void test_jDialog() throws Exception {
        runWindow(new JDialog());
    }


    public void test_jFrame() throws Exception {
        runWindow(new JFrame());
    }


    public void test_jWindow() throws Exception {
        runWindow(new JWindow());
    }


    public void test_jDesktopPaneInContentPane() throws Exception {
        runJDesktopPane(false);
    }


    public void test_jDesktopPaneAsContentPane() throws Exception {
        runJDesktopPane(true);
    }


    private void runWindow(Window window) {
        showWindow(window);
        runStep();
        window.dispose();
        assertEquals(0, waitingPanel.getComponentListeners().length);
    }


    private void runJDesktopPane(boolean asContentPane) {
        JFrame frame = new JFrame();
        JDesktopPane desktop = new JDesktopPane();
        if (asContentPane) {
            frame.setContentPane(desktop);
        }
        else {
            frame.add(desktop);
        }
        showJInternalFrame(frame, desktop);
        runStep();
        frame.dispose();
        assertEquals(0, waitingPanel.getComponentListeners().length);
    }


    private void runStep() {
        step.getPreparationStep().proceed(testContext);
        waitingPanel.exec();
        step.proceed(testContext);
    }


    private void showWindow(Window window) {
        window.add(new JLabel(window.getClass().getName()));
        ((RootPaneContainer)window).setGlassPane(waitingPanel);
        window.pack();
        window.setVisible(true);
        flushAWT();
    }


    private JInternalFrame showJInternalFrame(JFrame frame, JDesktopPane desktop) {
        JInternalFrame jInternalFrame = new JInternalFrame();
        jInternalFrame.add(new JLabel("JInternalFrame"));
        jInternalFrame.setGlassPane(waitingPanel);
        waitingPanel.setVisible(false);
        desktop.add(jInternalFrame);

        jInternalFrame.pack();
        jInternalFrame.setVisible(true);

        frame.setSize(200, 200);
        frame.setVisible(true);

        flushAWT();
        return jInternalFrame;
    }
}
