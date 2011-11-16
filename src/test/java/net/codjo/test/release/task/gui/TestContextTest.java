package net.codjo.test.release.task.gui;

import junit.extensions.jfcunit.JFCTestCase;
import org.apache.tools.ant.Project;

public class TestContextTest extends JFCTestCase {
    private TestContext context;


    public void test_replaceProperty_unknown() throws Exception {
        context = new TestContext(this);

        assertEquals("ma ${chaine}", context.replaceProperties("ma ${chaine}"));
    }


    public void test_replaceProperty() throws Exception {
        Project project = new Project();
        project.setProperty("chaine", "ok");

        context = new TestContext(this, project);

        assertEquals("ma ok", context.replaceProperties("ma ${chaine}"));
    }


    public void test_setProperty() throws Exception {
        context = new TestContext(this);
        context.setProperty("chaine", "ok");

        assertEquals("ma ok", context.replaceProperties("ma ${chaine}"));
    }
}
