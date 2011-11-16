/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.tokio;
import java.io.File;
import junit.framework.TestCase;
import org.apache.tools.ant.Project;
/**
 *
 */
public class LoadTest extends TestCase {
    private Load load = new LoadMock();
    private Project project = new Project();


    public void test_load() throws Exception {
        project.setProperty("scenario.name", "my_name");
        load.setProject(project);
        try {
            load.execute();
            assertEquals("my_name", load.getLoader().getScenario("my_name").getName());
        }
        catch (IllegalArgumentException e) {
            fail("Erreur de chargement du tokio : " + e.getLocalizedMessage());
        }
    }


    private class LoadMock extends Load {
        @Override
        protected File getTokioFile() {
            return new File(getClass().getResource(LoadTest.class.getSimpleName() + ".tokio").getFile());
        }
    }
}
