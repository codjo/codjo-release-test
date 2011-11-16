package net.codjo.test.release.task.gui;

import javax.swing.JButton;
import javax.swing.JFrame;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.JFCTestHelper;
import junit.extensions.jfcunit.TestHelper;
import junit.extensions.jfcunit.eventdata.MouseEventData;
import junit.extensions.jfcunit.finder.ComponentFinder;
import junit.extensions.jfcunit.finder.FrameFinder;

public class PauseStepTest extends JFCTestCase {
    private JFCTestHelper helper = new JFCTestHelper();


    public PauseStepTest() {
        this.setHelper(helper);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    public void test_pause() throws Exception {
        ProceedThread proceedThread = new ProceedThread();
        proceedThread.start();

        FrameFinder frameFinder = new FrameFinder(".*");
        frameFinder.setWait(3);
        JFrame pauseFrame = (JFrame)frameFinder.find();
        assertNotNull("Fenetre pause existe", pauseFrame);
        assertFalse("Le proceed bloque donc le thread est bloqué", proceedThread.proceedIsEnded);

        ComponentFinder buttonFinder = new ComponentFinder(JButton.class);
        buttonFinder.setWait(0);
        JButton continueButton = (JButton)buttonFinder.find();
        helper.enterClickAndLeave(new MouseEventData(this, continueButton));

        proceedThread.join(500);

        assertTrue("Le proceed à laché la main donc le thread est terminé", proceedThread.proceedIsEnded);
    }


    private class ProceedThread extends Thread {
        private boolean proceedIsEnded = false;


        @Override
        public void run() {
            PauseStep step = new PauseStep();

            step.proceed(new TestContext(PauseStepTest.this));

            proceedIsEnded = true;
        }
    }
}
