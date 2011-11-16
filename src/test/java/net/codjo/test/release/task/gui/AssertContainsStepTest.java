/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Component;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
/**
 *
 */
public class AssertContainsStepTest extends JFCTestCase {
    private JComboBox comboBox;
    private JList list;
    private AssertContainsStep step;
    private JComboBox comboBoxWithRenderer;


    @Override
    protected void setUp() {
        step = new AssertContainsStep();
        step.setTimeout(1);
        step.setDelay(5);
        step.setWaitingNumber(10);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    public void test_ok() throws Exception {
        showFrame();

        // JComboBox
        step.setName(comboBox.getName());
        step.setExpected("ligne3");
        step.proceed(new TestContext(this));

        // JList
        step.setName(list.getName());
        step.setExpected("ligneD");
        step.proceed(new TestContext(this));
    }


    public void test_notFound() throws Exception {
        showFrame();

        // JComboBox
        step.setName(comboBox.getName());
        step.setExpected("ligne7");
        try {
            step.proceed(new TestContext(this));
            fail("valeur inexistante");
        }
        catch (GuiAssertException e) {
            assertEquals("Le composant comboBox ne contient pas la valeur ligne7", e.getMessage());
        }

        // JList
        step.setName(list.getName());
        step.setExpected("ligneF");
        try {
            step.proceed(new TestContext(this));
            fail("valeur inexistante");
        }
        catch (GuiAssertException e) {
            assertEquals("Le composant list ne contient pas la valeur ligneF", e.getMessage());
        }
    }


    public void test_comboBoxOkWithRenderer() throws Exception {
        showFrame();

        // JComboBox
        step.setName(comboBoxWithRenderer.getName());
        step.setExpected("renderer3");
        step.proceed(new TestContext(this));

        step.setExpected("renderer3");
        step.setMode(AssertContainsStep.DISPLAY_MODE);
        step.proceed(new TestContext(this));

        step.setExpected("ligne4");
        step.setMode(AssertContainsStep.MODEL_MODE);
        step.proceed(new TestContext(this));
    }


    public void test_comboBoxNotFoundWithRenderer()
          throws Exception {
        showFrame();

        // JComboBox
        step.setName(comboBoxWithRenderer.getName());
        step.setExpected("renderer3");
        step.setMode(AssertContainsStep.MODEL_MODE);
        try {
            step.proceed(new TestContext(this));
            fail("mode model");
        }
        catch (Exception e) {
            assertEquals("Le composant comboBoxWithRenderer ne contient pas la valeur renderer3",
                         e.getMessage());
        }

        step.setExpected("ligne1");
        step.setMode(AssertContainsStep.DISPLAY_MODE);
        try {
            step.proceed(new TestContext(this));
            fail("mode display");
        }
        catch (Exception e) {
            assertEquals("Le composant comboBoxWithRenderer ne contient pas la valeur ligne1",
                         e.getMessage());
        }
    }


    private void showFrame() {
        JFrame frame = new JFrame();

        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        addComboBox(panel);
        addComboBoxWithRenderer(panel);
        addList(panel);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }


    private void addComboBox(JPanel panel) {
        comboBox = new JComboBox(new String[]{"ligne1", "ligne2", "ligne3", "ligne4", "ligne5"});
        comboBox.setName("comboBox");
        panel.add(comboBox);
    }


    private void addComboBoxWithRenderer(JPanel panel) {
        DefaultComboBoxModel model =
              new DefaultComboBoxModel(new String[]{"ligne1", "ligne2", "ligne3", "ligne4", "ligne5"});
        comboBoxWithRenderer = new JComboBox(model);
        comboBoxWithRenderer.setRenderer(new ListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                return new JLabel("renderer" + index);
            }
        });
        comboBoxWithRenderer.setName("comboBoxWithRenderer");
        panel.add(comboBoxWithRenderer);
    }


    private void addList(JPanel panel) {
        list = new JList(new String[]{"ligneA", "ligneB", "ligneC", "ligneD", "ligneE"});
        list.setName("list");
        panel.add(list);
    }
}
