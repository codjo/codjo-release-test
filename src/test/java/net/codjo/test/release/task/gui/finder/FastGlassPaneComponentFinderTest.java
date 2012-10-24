package net.codjo.test.release.task.gui.finder;

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

import static net.codjo.test.common.matcher.JUnitMatchers.*;

public class FastGlassPaneComponentFinderTest extends JFCTestCase {
    private static final String TARGET_NAME = "target";
    private FastGlassPaneComponentFinder finder;


    @Override
    public void setUp() throws Exception {
        super.setUp();
        finder = new FastGlassPaneComponentFinder(TARGET_NAME);
    }


    public void testNotFindGlassPaneJDialog() {
        assertGlassPaneNotFound(new JDialog());
    }


    public void testFindGlassPaneJDialog() {
        assertGlassPaneFound(new JDialog());
    }


    public void testNotFindGlassPaneJFrame() {
        assertGlassPaneNotFound(new JFrame());
    }


    public void testFindGlassPaneJFrame() {
        assertGlassPaneFound(new JFrame());
    }


    public void testNotFindGlassPaneJWindow() {
        assertGlassPaneNotFound(new JWindow());
    }


    public void testFindGlassPaneJWindow() {
        assertGlassPaneFound(new JWindow());
    }


    public void testFindGlassPaneJInternalFrame() {
        testFindGlassPaneJInternalFrame(true);
    }


    public void testNotFindGlassPaneJInternalFrame() {
        testFindGlassPaneJInternalFrame(false);
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


    private <T extends Window & RootPaneContainer> void assertGlassPaneNotFound(T window) {
		try {
			window.setName(TARGET_NAME);
			window.setVisible(true);
			Component found = finder.findOnlyOne();
			assertThat(found, nullValue());
		} finally {
			// It's only needed for JWindow but it doesn't hurt to free resources earlier.
			window.dispose();
		}
    }


    private <T extends Window & RootPaneContainer> void assertGlassPaneFound(T window) {
		try {
			Component target = buildGlassPane(window);
			Component found = findGlassPane(window);
			assertThat(found, is(target));
		} finally {
			// It's not really needed, even for JWindow, but it doesn't hurt to free resources earlier.
			window.dispose();
		}
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


    private void testFindGlassPaneJInternalFrame(boolean mustBeFound) {
        JFrame frame = new JFrame();
        JDesktopPane desktop = new JDesktopPane();
        frame.add(desktop);
        JInternalFrame internalFrame = new JInternalFrame();
        desktop.add(internalFrame);
        internalFrame.setVisible(true);

        if (mustBeFound) {
            // here the glasspane must be found
            Component target = buildGlassPane(internalFrame);
            Component found = findGlassPane(frame);
            internalFrame.dispose();
            assertThat(found, is(target));
        }
        else {
            // here the glasspane must NOT be found
            internalFrame.setName(TARGET_NAME);
            Component found = findGlassPane(frame);
            internalFrame.dispose();
            assertThat(found, nullValue());
        }
    }
}
