/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
/**
 * Classe de test de {@link AssertVisibleStep}.
 */
public class AssertVisibleStepTest extends JFCTestCase {
    private AssertVisibleStep step;
    private JComboBox combo;
    private JInternalFrame internalFrame;


    @Override
    protected void setUp() throws Exception {
        step = new AssertVisibleStep();
        step.setTimeout(1);
        step.setDelay(2);
        step.setWaitingNumber(5);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    public void test_defaultVisibleValue() throws Exception {
        assertTrue(step.isExpected());
    }


    public void test_knownComponent() throws Exception {
        showFrame();
        checkComponentVisible("LabelTest", true);
        checkComponentVisible("ListTest", true);
    }


    public void test_knownComponentVisibility() throws Exception {
        showFrame();
        combo.setVisible(true);
        checkComponentVisible("combo", true);

        try {
            checkComponentVisible("combo", false);
            fail();
        }
        catch (GuiConfigurationException e) {
            assertEquals(AssertVisibleStep.computeVisibleComponentMessage("combo"), e.getMessage());
        }

        combo.setVisible(false);
        checkComponentVisible("combo", false);

        try {
            checkComponentVisible("combo", true);
            fail();
        }
        catch (GuiConfigurationException e) {
            assertEquals(AssertVisibleStep.computeNotVisibleComponentMessage("combo"), e.getMessage());
        }
    }


    public void test_unknownComponent() throws Exception {
        showFrame();
        checkComponentVisible("unknownComponent", false);
    }


    public void test_knownComponentFailure() throws Exception {
        showFrame();

        try {
            checkComponentVisible("LabelTest", false);
            fail();
        }
        catch (GuiConfigurationException e) {
            assertEquals(AssertVisibleStep.computeVisibleComponentMessage("LabelTest"), e.getMessage());
        }
    }


    public void test_unknownComponentFailure() throws Exception {
        showFrame();

        try {
            checkComponentVisible("unknownComponent", true);
            fail();
        }
        catch (GuiConfigurationException e) {
            assertEquals(AssertVisibleStep.computeNotVisibleComponentMessage("unknownComponent"),
                         e.getMessage());
        }
    }


    public void test_glassPaneComponent() throws Exception {
        JLabel label = new JLabel("je suis là");
        label.setName("glassPaneComponent");

        showFrame();
        checkComponentVisible("glassPaneComponent", false);

        internalFrame.setGlassPane(label);
        label.setVisible(true);

        checkComponentVisible("glassPaneComponent", true);

        label.setVisible(false);
        checkComponentVisible("glassPaneComponent", false);
    }


    public void test_glassPaneComponent_manyInternalFrames() throws Exception {
        JInternalFrame internalFrame1 = createInternalFrame("glassPaneComponent");
        JInternalFrame internalFrame2 = createInternalFrame("glassPaneComponent");
        showInternalFrames(internalFrame1, internalFrame2);

        internalFrame2.setSelected(true);

        checkComponentVisible("glassPaneComponent", false);

        internalFrame1.getGlassPane().setVisible(true);

        checkComponentVisible("glassPaneComponent", false);

        internalFrame2.getGlassPane().setVisible(true);

        checkComponentVisible("glassPaneComponent", true);

        internalFrame1.setSelected(true);

        checkComponentVisible("glassPaneComponent", true);

        internalFrame1.getGlassPane().setVisible(false);

        checkComponentVisible("glassPaneComponent", false);
    }


    private void showInternalFrames(JInternalFrame... internalFrames) {
        JDesktopPane desktopPane = new JDesktopPane();
        for (JInternalFrame frame : internalFrames) {
            desktopPane.add(frame);
            frame.setVisible(true);
        }

        JFrame frame = new JFrame("Test assertVisible");
        frame.add(desktopPane);
        frame.setSize(400, 200);
        frame.setVisible(true);
        flushAWT();
    }


    private JInternalFrame createInternalFrame(String glassPaneName) {
        JInternalFrame internalFrame1 = new JInternalFrame();
        internalFrame1.setGlassPane(createLabel(glassPaneName));
        return internalFrame1;
    }


    private void checkComponentVisible(String name, boolean visible) {
        step.setName(name);
        step.setExpected(visible);
        step.proceed(new TestContext(this));
    }


    private void showFrame() {
        JFrame frame = new JFrame("Test assertVisible");

        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        JLabel label = new JLabel("Hello");
        label.setName("LabelTest");
        panel.add(label);

        JList list = new JList(new Object[]{"val0", "val1", "val2"});
        list.setName("ListTest");
        panel.add(list);

        combo = new JComboBox(new Object[]{"poire", "pomme", "mirabelle"});
        combo.setName("combo");
        panel.add(combo);

        internalFrame = new JInternalFrame();
        internalFrame.setName("glassPaneContainer");
        panel.add(internalFrame);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }


    private JLabel createLabel(String name) {
        JLabel label = new JLabel();
        label.setName(name);
        return label;
    }
}
