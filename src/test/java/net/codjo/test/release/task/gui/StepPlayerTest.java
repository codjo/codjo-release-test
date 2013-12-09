/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import junit.extensions.jfcunit.WindowMonitor;
import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
/**
 * Classe de test de {@link StepPlayer}.
 */
public class StepPlayerTest {
    private static final Logger LOG = Logger.getLogger(StepPlayerTest.class);

    private StepPlayer player;
    private Project project;
    private String testName;


    @Before
    public void setUp() throws Exception {
        project = new Project();
        player = new StepPlayer(project, null);
    }


    @Test
    public void test_play() throws Exception {
        MockStep step = new MockStep();
        player.play(step);

        Assert.assertEquals(1, step.callCount);
        Assert.assertNotNull(step.context);
        Assert.assertSame(player, step.context.getTestCase());
        Assert.assertNotSame(Thread.currentThread(), step.callThread);

        project.setProperty("fromProject", "it works !");
        Assert.assertEquals("it works !", step.context.replaceProperties("${fromProject}"));
    }


    @Test
    public void test_play_nok_GuiException() throws Exception {
        project.setUserProperty("ant.file", "toto.xml");
        GuiException expectedException = new GuiException("Exemple de GuiException");
        MockStep step = new MockStep(expectedException);

        try {
            player.play(step);
            Assert.fail("La step doit échouer");
        }
        catch (GuiException ex) {
            Assert.assertSame(expectedException, ex.getCause());
            Assert.assertEquals("Localisation impossible \n--> Exemple de GuiException", ex.getMessage());
            Assert.assertTrue(player.determineScreenShotFile().exists());
        }
    }


    @Test
    public void test_play_nok_Throwable() throws Exception {
        RuntimeException expectedCause = new RuntimeException("Exemple d'erreur grave");
        MockStep step = new MockStep(expectedCause);

        try {
            player.play(step);
            Assert.fail("La step doit échouer");
        }
        catch (GuiException ex) {
            Assert.assertSame(expectedCause, ex.getCause());
            Assert.assertEquals("Localisation impossible \n--> Exemple d'erreur grave", ex.getMessage());
        }
    }


    @Test
    public void test_determineScreenShotFile() throws Exception {
        project.setUserProperty("ant.file", "toto.xml");
        File actual = player.determineScreenShotFile();

        Assert.assertNotNull(actual);
        Assert.assertEquals(new File("./target/toto.xml.jpeg"), actual);
    }


    @Test
    public void test_determineScreenShotFile_withoutAntFileName()
          throws Exception {
        File actual = player.determineScreenShotFile();

        Assert.assertNotNull(actual);
        Assert.assertEquals(new File("./target"), actual.getParentFile());

        String actualName = actual.getName();
        Assert.assertTrue(actualName.startsWith("Error-"));
        Assert.assertTrue(actualName.endsWith(".jpeg"));

        String timestamp = actualName.substring("Error-".length(), actualName.length() - ".jpeg".length());
        Date date = new SimpleDateFormat("yyyyMMdd-HHmmss").parse(timestamp);
        long delay = Math.abs(System.currentTimeMillis() - date.getTime());
        Assert.assertTrue(delay < 5000);
    }


    @Test
    public void test_CleanUp() throws Exception {
        StringWriter errorWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(errorWriter);

        StringBuilder message = new StringBuilder();
        boolean noError = true;
        for (int nbDialogs = 0; nbDialogs <= 3; nbDialogs++) {
            for (Modality modality : Modality.values()) {
                for (ParentType parentType : ParentType.values()) {
                    boolean ok;
                    try {
                        test_CleanUp(nbDialogs, modality, parentType);
                        ok = true;
                    }
                    catch (Throwable e) {
                        if (noError) {
                            printWriter.println("List of errors :");
                            noError = false;
                        }
                        printWriter.println(String.format("\t ===== %-80s =====", testName));
                        e.printStackTrace(printWriter);
                        ok = false;
                    }
                    message.append(String.format("%-80s", testName)).append(" : ").append(ok ? "OK\n" : "KO !\n");
                }
            }
        }
        LOG.info("result=\n" + message.toString());

        assertTrue("The test has failed for these cases :\n" + errorWriter.toString(), noError);
    }


    public void test_CleanUp(final int nbDialogs, final Modality modality, final ParentType parentType)
          throws Exception {
        String testID = "CleanUp_" + nbDialogs + '_' + modality.ordinal() + '_' + parentType.ordinal();

        testName = testID + " test_CleanUp(" + nbDialogs + " dialogs, " + modality + ", " + parentType + ")";
        LOG.info("***** Start of " + testName + " *****");
        LOG.info("testID=" + testID);

        final JFrame mainFrame = new JFrame("Main frame");
        mainFrame.setName(testID);

        try {
            JButton button = createButtonOpeningDialogs(nbDialogs, modality, parentType, mainFrame, testID);
            mainFrame.setContentPane(button);
            mainFrame.pack();
            mainFrame.setSize(100, 100);
            mainFrame.setVisible(true);
            mainFrame.addNotify();

            ClickStep click = new ClickStep();
            click.setName(button.getName());
            player.play(click);

            player.cleanUp();

            StringBuilder errorMessage = new StringBuilder("These windows were not closed:\n");
            if (!getUnclosedWindows(errorMessage).isEmpty()) {
                fail(errorMessage.toString());
            }
        }
        finally {
            // close all windows that might stay due to a bug in cleanUp
            // or a test failure
            for (final Window w : WindowMonitor.getWindows()) {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        w.setVisible(false);
                        w.dispose();
                    }
                });
            }
        }
    }


    private List<Window> getUnclosedWindows(StringBuilder unclosedWindows) {
        List<Window> windows = new ArrayList<Window>();

        Window[] wins = WindowMonitor.getWindows();
        List<Window> closedWindows = new ArrayList<Window>();

        StringBuilder sb = new StringBuilder();
        sb.append("\nunclosed windows");
        if (wins.length == 0) {
            sb.append(" : NONE\n");
        }
        else {
            unclosedWindows.append('\n');
            for (Window w : wins) {
                if (w instanceof Dialog) {
                    if (w.isVisible()) {
                        unclosedWindows.append('\t').append(w).append('\n');
                        windows.add(w);
                    }
                    else {
                        closedWindows.add(w);
                    }
                }
            }
            sb.append(unclosedWindows);
        }

        sb.append("closed windows");
        if (closedWindows.isEmpty()) {
            sb.append(" : NONE\n");
        }
        else {
            sb.append('\n');
            for (Window w : closedWindows) {
                sb.append('\t').append(w).append('\n');
            }
        }

        LOG.warn(sb.toString());

        return windows;
    }


    private JDialog[] dialogs;


    private JButton createButtonOpeningDialogs(final int nbDialogs,
                                               final Modality modality,
                                               final ParentType parentType,
                                               final JFrame mainFrame, final String testID) {
        JButton button = new JButton(new AbstractAction("Open Dialogs") {
            public void actionPerformed(ActionEvent e) {
                dialogs = new JDialog[nbDialogs];
                for (int i = 0; i < nbDialogs; i++) {
                    final Window parent;
                    switch (parentType) {
                        case MAIN_FRAME:
                            parent = mainFrame;
                            break;

                        case DIALOG_MAIN_FRAME_AS_ROOT:
                        case DIALOG_NO_PARENT_AS_ROOT:
                            if (i > 0) {
                                parent = dialogs[i - 1];
                            }
                            else {
                                parent = (parentType == ParentType.DIALOG_MAIN_FRAME_AS_ROOT) ? mainFrame : null;
                            }
                            break;

                        case NO_PARENT:
                        default:
                            parent = null;
                    }

                    final JDialog dialog;
                    if (parent instanceof Dialog) {
                        dialog = new JDialog((Dialog)parent, "Dialog " + i, modality == Modality.MODAL);
                    }
                    else {
                        dialog = new JDialog((Frame)parent, "Dialog " + i, modality == Modality.MODAL);
                    }
                    dialog.setName(testID + "_Dialog#" + i);
                    dialog.setContentPane(new JLabel(dialog.getName()));
                    dialog.pack();
                    dialog.setLocation(175 + i * 175, 0);
                    dialog.setSize(170, 100);
                    dialogs[i] = dialog;

                    if (dialog.isModal()) {
                        // invoke setVisible(true) in a dedicated thread because it's a blocking call
                        new Thread() {
                            @Override
                            public void run() {
                                dialog.setVisible(true);
                            }
                        }.start();
                    }
                    else {
                        dialog.setVisible(true);
                    }
                }
            }
        });
        button.setName("button");
        return button;
    }


    private static class MockStep implements GuiStep {
        private int callCount = 0;
        private TestContext context;
        private Thread callThread;
        private RuntimeException exception;


        private MockStep() {
        }


        private MockStep(RuntimeException exception) {
            this.exception = exception;
        }


        public void proceed(TestContext ctx) {
            callCount++;
            context = ctx;
            callThread = Thread.currentThread();
            if (exception != null) {
                throw exception;
            }
        }
    }

    private static class MockExitStep extends MockStep {
        @Override
        public void proceed(TestContext ctx) {
            super.proceed(ctx);
            System.exit(1);
        }
    }

    private static enum Modality {
        NOT_MODAL,
        MODAL;
    }

    private static enum ParentType {
        NO_PARENT,
        MAIN_FRAME,
        DIALOG_MAIN_FRAME_AS_ROOT,
        DIALOG_NO_PARENT_AS_ROOT;
    }
}
