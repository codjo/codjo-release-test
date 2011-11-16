package net.codjo.test.release.task.gui.finder;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
import java.awt.Component;
import java.awt.Window;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.RootPaneContainer;
import junit.extensions.jfcunit.JFCTestCase;

public class FastGlassPaneComponentFinderTest extends JFCTestCase {
    private static final String TARGET_NAME = "target";
    private FastGlassPaneComponentFinder finder;


    @Override
    public void setUp() throws Exception {
        super.setUp();
        finder = new FastGlassPaneComponentFinder(TARGET_NAME);
    }


    public void testNotFindGlassPaneJDialog() {
        JDialog dialog = new JDialog();
        dialog.setName(TARGET_NAME);
        dialog.setVisible(true);
        assertThat(finder.findOnlyOne(), nullValue());
    }


    public void testFindGlassPaneJDialog() throws InterruptedException {
        JDialog dialog = new JDialog();
        assertGlassPaneFound(dialog);
    }


    public void testFindGlassPaneJFrame() {
        JFrame frame = new JFrame();
        assertGlassPaneFound(frame);
    }


    public void testFindGlassPaneJWindow() {
        JWindow window = new JWindow();
        assertGlassPaneFound(window);
    }


    public void testFindGlassPaneJInternalFrame() {
        JFrame frame = new JFrame();
        JDesktopPane desktop = new JDesktopPane();
        frame.add(desktop);
        JInternalFrame internalFrame = new JInternalFrame();
        desktop.add(internalFrame);
        internalFrame.setVisible(true);

        Component target = buildGlassPane(internalFrame);
        Component found = findGlassPane(frame);
        internalFrame.dispose();
        assertThat(found, is(target));
    }


    public void testFindGlassPaneJInternalFrameInDeepHierarchy() {
        JFrame frame = new JFrame();
        JDesktopPane desktop = new JDesktopPane();

        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();

        panel1.add(panel2);
        panel2.add(panel3);
        panel3.add(desktop);

        frame.add(panel1);

        JInternalFrame internalFrame = new JInternalFrame();
        desktop.add(internalFrame);
        internalFrame.setVisible(true);

        Component target = buildGlassPane(internalFrame);
        Component found = findGlassPane(frame);
        internalFrame.dispose();
        assertThat(found, equalTo(target));
    }


    public void testFindGlassPaneTwoInternalFrames() throws Exception {
        JInternalFrame internalFrame1 = new JInternalFrame();
        JInternalFrame internalFrame2 = new JInternalFrame();
        showInternalFrames(internalFrame1, internalFrame2);

        assertNull(finder.findOnlyOne());

        internalFrame1.setVisible(true);
        internalFrame1.setSelected(true);

        assertSame(internalFrame1.getGlassPane(), finder.findOnlyOne());

        internalFrame2.setVisible(true);
        internalFrame1.setSelected(true);

        assertSame(internalFrame1.getGlassPane(), finder.findOnlyOne());

        internalFrame2.setSelected(true);

        assertSame(internalFrame2.getGlassPane(), finder.findOnlyOne());

        internalFrame2.setVisible(false);

        assertSame(internalFrame1.getGlassPane(), finder.findOnlyOne());

        internalFrame1.setVisible(false);

        assertNull(finder.findOnlyOne());
    }


    private <T extends Window & RootPaneContainer> void assertGlassPaneFound(T window) {
        Component target = buildGlassPane(window);
        Component found = findGlassPane(window);
        assertThat(found, is(target));
    }


    private Component buildGlassPane(RootPaneContainer container) {
        Component panel = new JPanel();
        panel.setName(TARGET_NAME);
        container.setGlassPane(panel);
        return panel;
    }


    private Component findGlassPane(Window window) {
        window.add(new JLabel("le window"));
        window.pack();
        window.setVisible(true);
        flushAWT();
        Component found = finder.findOnlyOne();
        window.dispose();
        return found;
    }


    private void showInternalFrames(JInternalFrame... internalFrames) {
        JDesktopPane desktop = new JDesktopPane();
        for (JInternalFrame internalFrame : internalFrames) {
            Component glassPane = buildGlassPane(internalFrame);
            glassPane.setVisible(false);
            desktop.add(internalFrame);
            internalFrame.setVisible(false);
        }

        JFrame frame = new JFrame();
        frame.add(desktop);
        frame.pack();
        frame.setVisible(true);
    }
}
