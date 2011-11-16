/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task;
import junit.framework.TestCase;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
/**
 * Classe de test de {@link AgfTask}.
 */
public class AgfTaskTest extends TestCase {
    public void test_getProperty() throws Exception {
        AgfTask agfTask = new AgfTaskImpl();
        try {
            agfTask.getProperty("UNEXISTING project !!!");
            fail("NullPointerException attempted !");
        }
        catch (NullPointerException e) {
            ;
        }

        Project project = new Project();
        agfTask.setProject(project);
        assertNull(agfTask.getProperty("UNEXISTING_PROPERTY"));
        boolean checkProperty = true;
        assertNull(agfTask.getProperty("UNEXISTING_PROPERTY", !checkProperty));
        try {
            assertNull(agfTask.getProperty("UNEXISTING_PROPERTY", checkProperty));
            fail("BuildException attempted !");
        }
        catch (BuildException e) {
            ;
        }

        String propertyKey = "PROPERTY_KEY";
        String propertyValue = "PROPERTY_VALUE";
        project.setProperty(propertyKey, propertyValue);
        assertEquals(propertyValue, agfTask.getProperty(propertyKey));
        assertEquals(propertyValue, agfTask.getProperty(propertyKey, checkProperty));
    }


    public void test_getProperty_defaultValue() throws Exception {
        AgfTask agfTask = new AgfTaskImpl();
        Project project = new Project();
        agfTask.setProject(project);

        assertEquals("default-value", agfTask.getProperty("key", "default-value"));
        project.setProperty("key", "value");
        assertEquals("value", agfTask.getProperty("key", "default-value"));
    }

    static class AgfTaskImpl extends AgfTask {
        @Override
        public void execute() {}
    }
}
