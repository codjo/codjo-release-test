/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task;
import net.codjo.test.release.TestEnvironment;
import net.codjo.test.release.TestEnvironmentMock;
import net.codjo.test.release.agent.AgentTestEnvironment;
import junit.framework.TestCase;
import org.apache.tools.ant.Project;
/**
 * Classe de test de {@link ReleaseTask}.
 */
public class ReleaseTaskTest extends TestCase {
    private ReleaseTask releaseTask;
    private Project project;

    public void test_execute() throws Exception {
        releaseTask.execute();
        assertEquals(AgentTestEnvironment.class,
            releaseTask.getProject().getReference(TestEnvironment.class.getName()).getClass());

        project.setProperty("testEnvironment.class", TestEnvironmentMock.class.getName());
        releaseTask.execute();
        assertEquals(TestEnvironmentMock.class,
            releaseTask.getProject().getReference(TestEnvironment.class.getName()).getClass());
    }


    @Override
    protected void setUp() throws Exception {
        releaseTask = new ReleaseTask();
        project = new Project();
        releaseTask.setProject(project);
    }
}
