/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.beans.PropertyVetoException;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
/**
 * Classe de test de {@link CloseFrameStep}.
 */
public class CloseFrameStepTest extends JFCTestCase {
    private static final String TITLE = "Frame de test";
    private CloseFrameStep step;
    private JInternalFrame frame;
    private JFrame hiddenFrame;
    private JInternalFrame anotherFrame;


    public void test_defaults() throws Exception {
        assertEquals(null, step.getTitle());
    }


    public void test_internalFrameOk() throws Exception {
        showInternalFrame();
        assertTrue(frame.isDisplayable());

        step.setTitle(TITLE);
        step.proceed(new TestContext(this));
        assertFalse("Close doit fermer la frame", frame.isDisplayable());
    }


    public void test_nok_internalFrameNotFound() throws Exception {
        showInternalFrame();

        TestContext context = new TestContext(this);
        step.setTitle("Un titre fantaisiste");

        try {
            step.proceed(context);
            fail("Pas d'erreur alors que la frame n'existe pas.");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
    }


    public void test_frameOk() throws Exception {
        showFrame();
        assertTrue(hiddenFrame.isDisplayable());

        hiddenFrame.setTitle(TITLE);
        step.setTitle(TITLE);
        step.proceed(new TestContext(this));
        assertFalse("Close doit fermer la frame", hiddenFrame.isDisplayable());
    }


    public void test_nok_frameNotFound() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setTitle("Un titre fantaisiste");

        try {
            step.proceed(context);
            fail("Pas d'erreur alors que la frame n'existe pas.");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
    }


    public void test_otherFrameSelected() throws Exception {
        showInternalFrame();

        assertTrue(frame.isSelected());
        assertFalse(anotherFrame.isSelected());

        TestContext context = new TestContext(this);
        step.setTitle(TITLE);
        step.proceed(context);
        flushAWT();

        assertFalse(frame.isSelected());
        assertTrue(anotherFrame.isSelected());
    }


    private void showInternalFrame() {
        JFrame mainFrame = new JFrame("Fenêtre principale");
        mainFrame.setSize(200, 150);

        JDesktopPane desktopPane = new JDesktopPane();
        mainFrame.getContentPane().add(desktopPane);
        mainFrame.setVisible(true);

        frame = new JInternalFrame(TITLE);
        frame.setSize(80, 60);
        desktopPane.add(frame);
        frame.setVisible(true);

        anotherFrame = new JInternalFrame("Another frame");
        anotherFrame.setSize(80, 60);
        desktopPane.add(anotherFrame);
        anotherFrame.setVisible(true);

        try {
            frame.setSelected(true);
        }
        catch (PropertyVetoException e) {
            ;
        }
        flushAWT();
    }


    private void showFrame() {
        hiddenFrame = new JFrame("Fenêtre cachée par la deuxiéme");
        hiddenFrame.setSize(200, 150);
        hiddenFrame.setVisible(true);

        JFrame secondFrame = new JFrame("Fenêtre visible qui cache la première");
        secondFrame.setSize(200, 150);
        secondFrame.setVisible(true);

        flushAWT();
    }


    @Override
    protected void setUp() throws Exception {
        step = new CloseFrameStep();
        step.setTimeout(1);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }
}
