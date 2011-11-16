/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
/**
 * Classe de test de {@link AssertListStep}.
 */
public class AssertListStepTest extends JFCTestCase {
    private AssertListStep step;


    @Override
    protected void setUp() throws Exception {
        step = new AssertListStep();
        step.setTimeout(1);
        step.setDelay(5);
        step.setWaitingNumber(10);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    public void test_defaults() throws Exception {
        assertEquals(null, step.getName());
        assertEquals(null, step.getExpected());
        assertEquals(-1, step.getRow());
    }


    public void test_ok() throws Exception {
        showFrame();

        step.setName("ListTest");
        step.setRow(1);
        step.setExpected("val1");
        step.proceed(new TestContext(this));

        step.setName("ComboTest");
        step.setRow(2);
        step.setExpected("comb2");
        step.proceed(new TestContext(this));

        step.setName("PopupTest");
        step.setRow(2);
        step.setExpected("ligne3");
        step.proceed(new TestContext(this));
    }


    public void test_ok_dynamicProperties() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        context.setProperty("property", "val1");

        step.setName("ListTest");
        step.setRow(1);
        step.setExpected("${property}");
        step.proceed(context);
    }


    public void test_comboModes() throws Exception {
        showFrame();

        step.setName("ComboTest");
        step.setRow(1);
        step.setExpected("label1");
        step.proceed(new TestContext(this));

        step.setName("ComboTest");
        step.setRow(1);
        step.setMode(AssertListStep.MODEL_MODE);
        step.setExpected("label1");
        try {
            step.proceed(new TestContext(this));
            fail("mode model");
        }
        catch (Exception e) {
            ;
        }
    }


    public void test_listModes() throws Exception {
        showFrame();

        step.setName("ListTest");
        step.setRow(1);
        step.setExpected("label1");
        step.proceed(new TestContext(this));

        step.setName("ListTest");
        step.setRow(1);
        step.setMode(AssertListStep.MODEL_MODE);
        step.setExpected("label1");
        try {
            step.proceed(new TestContext(this));
            fail("mode model");
        }
        catch (Exception e) {
            ;
        }

        step.setName("ListTest");
        step.setRow(1);
        step.setMode(AssertListStep.MODEL_MODE);
        step.setExpected("val1");
        step.proceed(new TestContext(this));
    }


    public void test_nok_listNotFound() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("UneListInconnue");
        step.setRow(1);
        step.setExpected("expval");

        try {
            step.proceed(context);
            fail("Pas d'erreur alors que la list n'existe pas.");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
    }


    public void test_nok_badCell() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("ListTest");
        step.setRow(100);
        step.setExpected("l1c2");

        try {
            step.proceed(context);
            fail("Pas d'erreur alors que la valeur n'est pas celle attendue.");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
    }


    public void test_nok_badValue() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("ListTest");
        step.setRow(1);
        step.setExpected("expval");

        try {
            step.proceed(context);
            fail("Pas d'erreur alors que row sont invalides.");
        }
        catch (GuiAssertException ex) {
            ; // Cas normal
        }

        step.setName("ComboTest");
        step.setRow(1);
        step.setExpected("expval");

        try {
            step.proceed(context);
            fail("Pas d'erreur alors que row sont invalides.");
        }
        catch (GuiAssertException ex) {
            ; // Cas normal
        }

        step.setName("PopupTest");
        step.setRow(1);
        step.setExpected("expval");

        try {
            step.proceed(context);
            fail("Pas d'erreur alors que row sont invalides.");
        }
        catch (GuiAssertException ex) {
            ; // Cas normal
        }
    }


    private void showFrame() {
        JFrame frame = new JFrame("Test JList");

        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        JList list = new JList(new Object[]{"val0", "val1", "val2"});
        list.setCellRenderer(new ListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                return new JLabel("label" + index);
            }
        });
        list.setName("ListTest");
        panel.add(list);

        JComboBox comboBox = new JComboBox(new Object[]{"comb0", "comb1", "comb2"});
        comboBox.setRenderer(new ListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                return new JLabel("label" + index);
            }
        });
        comboBox.setName("ComboTest");
        panel.add(comboBox);

        JPopupMenu popup = new JPopupMenu();
        popup.add("ligne1");
        popup.add("ligne2");
        popup.add("ligne3");
        popup.setName("PopupTest");
        panel.add(popup);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }
}
