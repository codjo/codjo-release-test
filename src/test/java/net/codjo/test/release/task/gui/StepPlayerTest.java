/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;
import org.apache.tools.ant.Project;
/**
 * Classe de test de {@link StepPlayer}.
 */
public class StepPlayerTest extends TestCase {
    private StepPlayer player;
    private Project project;


    @Override
    protected void setUp() throws Exception {
        project = new Project();
        player = new StepPlayer(project, null);
    }


    public void test_play() throws Exception {
        MockStep step = new MockStep();
        player.play(step);

        assertEquals(1, step.callCount);
        assertNotNull(step.context);
        assertSame(player, step.context.getTestCase());
        assertNotSame(Thread.currentThread(), step.callThread);

        project.setProperty("fromProject", "it works !");
        assertEquals("it works !", step.context.replaceProperties("${fromProject}"));
    }


    public void test_play_nok_GuiException() throws Exception {
        project.setUserProperty("ant.file", "toto.xml");
        GuiException expectedException = new GuiException("Exemple de GuiException");
        MockStep step = new MockStep(expectedException);

        try {
            player.play(step);
            fail("La step doit échouer");
        }
        catch (GuiException ex) {
            assertSame(expectedException, ex.getCause());
            assertEquals("Localisation impossible \n--> Exemple de GuiException", ex.getMessage());
            assertTrue(player.determineScreenShotFile().exists());
        }
    }


    public void test_play_nok_Throwable() throws Exception {
        RuntimeException expectedCause = new RuntimeException("Exemple d'erreur grave");
        MockStep step = new MockStep(expectedCause);

        try {
            player.play(step);
            fail("La step doit échouer");
        }
        catch (GuiException ex) {
            assertSame(expectedCause, ex.getCause());
            assertEquals("Localisation impossible \n--> Exemple d'erreur grave", ex.getMessage());
        }
    }


    public void test_determineScreenShotFile() throws Exception {
        project.setUserProperty("ant.file", "toto.xml");
        File actual = player.determineScreenShotFile();

        assertNotNull(actual);
        assertEquals(new File("./target/toto.xml.jpeg"), actual);
    }


    public void test_determineScreenShotFile_withoutAntFileName()
          throws Exception {
        File actual = player.determineScreenShotFile();

        assertNotNull(actual);
        assertEquals(new File("./target"), actual.getParentFile());

        String actualName = actual.getName();
        assertTrue(actualName.startsWith("Error-"));
        assertTrue(actualName.endsWith(".jpeg"));

        String timestamp = actualName.substring("Error-".length(), actualName.length() - ".jpeg".length());
        Date date = new SimpleDateFormat("yyyyMMdd-HHmmss").parse(timestamp);
        long delay = Math.abs(System.currentTimeMillis() - date.getTime());
        assertTrue(delay < 5000);
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
}
