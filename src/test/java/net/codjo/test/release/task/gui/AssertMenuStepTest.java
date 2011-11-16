/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
/**
 * Classe de test de {@link AssertMenuStepTest}.
 */
public class AssertMenuStepTest extends JFCTestCase {
    private AssertMenuStep step;
    private JPanel parasite = new JPanel();
    private JCheckBoxMenuItem jCheckBoxMenuItem;
    private JRadioButtonMenuItem jRadioButtonMenuItem;


    @Override
    protected void setUp() {
        step = new AssertMenuStep();
        step.setTimeout(1);
        step.setDelay(5);
        step.setWaitingNumber(10);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    public void test_nok_notFound() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("Branche");
        try {
            step.proceed(context);
            fail("Le composant n'existe pas.");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
    }


    public void test_nok_unknownComponent() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName(parasite.getName());
        try {
            step.proceed(context);
            fail("Type de composant non supporté.");
        }
        catch (GuiConfigurationException ex) {
            ; // Cas normal
        }
    }


    public void test_ok() throws Exception {
        showFrame();

        // JCheckBoxMenuItem
        step.setName(jCheckBoxMenuItem.getName());
        jCheckBoxMenuItem.setSelected(true);
        step.setChecked(true);
        step.proceed(new TestContext(this));
        jCheckBoxMenuItem.setSelected(false);
        step.setLabel("A");
        step.setChecked(false);
        step.proceed(new TestContext(this));

        // JRadioButtonMenuItem
        step.setName(jRadioButtonMenuItem.getName());
        jRadioButtonMenuItem.setSelected(true);
        step.setLabel(null);
        step.setChecked(true);
        step.proceed(new TestContext(this));
        step.setLabel("B");
        jRadioButtonMenuItem.setSelected(false);
        step.setChecked(false);
        step.proceed(new TestContext(this));
    }


    private void showFrame() {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        jCheckBoxMenuItem = new JCheckBoxMenuItem("jCheckBoxMenuItem ", true);
        jCheckBoxMenuItem.setName("jCheckBoxMenuItem");
        jCheckBoxMenuItem.setText("A");
        jRadioButtonMenuItem = new JRadioButtonMenuItem("jRadioButtonMenuItem", true);
        jRadioButtonMenuItem.setName("jRadioButtonMenuItem");
        jRadioButtonMenuItem.setText("B");

        frame.getContentPane().add(jCheckBoxMenuItem);
        frame.getContentPane().add(jRadioButtonMenuItem);

        panel.add(parasite);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }
}
