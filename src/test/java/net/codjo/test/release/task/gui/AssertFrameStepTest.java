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
 * Classe de test de {@link AssertFrameStep}.
 */
public class AssertFrameStepTest extends JFCTestCase {
    private static final String TITLE = "Frame de test pour AssertFrameStepTest";
    private static final String MAIN_TITLE = "Fenêtre principale";
    private AssertFrameStep step;


    @Override
    protected void setUp() throws Exception {
        step = new AssertFrameStep();
        step.setTimeout(1);
        step.setDelay(5);
        step.setWaitingNumber(10);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    public void test_default_values() throws Exception {
        int timeout = 2;
        step.setTimeout(timeout);

        assertEquals(false, step.getClosed());
        assertEquals("Quand Closed=false, timeout std", 2, step.getTimeout());

        step.setClosed(true);
        assertEquals("Quand Closed=true, timeout std aussi!", 2, step.getTimeout());
    }


    public void test_ok_internalFrame() throws Exception {
        showFrame();

        // par nom complet
        step.setTitle(TITLE);
        step.proceed(new TestContext(this));

        // Cas closed = true
        step.setTitle("Not Opened");
        step.setClosed(true);
        step.proceed(new TestContext(this));
    }


    public void test_ok_frame() throws Exception {
        showFrame();

        // par nom complet
        step.setTitle(MAIN_TITLE);
        step.proceed(new TestContext(this));
    }


    public void test_ok_frame_matching_contains() throws Exception {
        showFrame();

        step.setTitle("Frame de test");
        step.setMatching("contains");
        step.proceed(new TestContext(this));
    }


    public void test_ok_frame_using_property() throws Exception {
        showFrame();

        TestContext testContext = new TestContext(this);
        testContext.setProperty("nom", "AssertFrameStepTest");

        step.setTitle("Frame de test pour ${nom}");
        step.proceed(testContext);
    }


    public void test_nok() throws Exception {
        showFrame();

        // Frame exists but expected false
        step.setTitle(TITLE);
        step.setClosed(true);
        try {
            step.proceed(new TestContext(this));
            fail("Frame ouverte.");
        }
        catch (GuiAssertException ex) {
            ; // OK
        }

        // Frame not exists but expected true
        step.setTitle("unknown");
        step.setClosed(false);
        step.setTimeout(0);
        try {
            step.proceed(new TestContext(this));
            fail("Frame inconnue.");
        }
        catch (GuiAssertException ex) {
            ; // OK
        }
    }


    private void showFrame() {
        JFrame mainFrame = new JFrame(MAIN_TITLE);
        mainFrame.setSize(200, 150);

        JDesktopPane desktopPane = new JDesktopPane();
        mainFrame.getContentPane().add(desktopPane);
        mainFrame.setVisible(true);

        JInternalFrame frame = new JInternalFrame(TITLE);
        frame.setSize(80, 60);
        desktopPane.add(frame);
        try {
            frame.setSelected(true);
        }
        catch (PropertyVetoException ex) {
            ;
        }

        frame.setVisible(true);
        flushAWT();
    }
}
