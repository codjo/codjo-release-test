/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import net.codjo.test.release.TestEnvironment;
import net.codjo.test.release.agent.AgentTestEnvironment;
import java.util.Arrays;
import junit.framework.TestCase;
import org.apache.tools.ant.Project;
/**
 * Classe de test de {@link net.codjo.test.release.task.gui.GuiTask}.
 */
public class GuiTaskTest extends TestCase {
    private GuiTask task;
    private Project project;


    public void test_determineOrderedArguments() {
        project.setProperty("gui.default.arg.0", "val1");
        project.setProperty("gui.default.arg.1", "val2");
        project.setProperty("gui.default.arg.2", "val3");

        String[] actual = task.determineArguments();

        assertEquals("[val1, val2, val3]", Arrays.asList(actual).toString());
    }


    public void test_determineNamedArguments() {
        project.setProperty("gui.default.arg.user", "val1");
        project.setProperty("gui.default.arg.password", "val2");
        project.setProperty("gui.default.arg.server.host", "val3");
        project.setProperty("gui.default.arg.server.port", "val4");
        project.setProperty("gui.default.arg.specific", "val5");

        String[] actual = task.determineArguments();

        assertEquals("[val1, val2, val3, val4, val5]", Arrays.asList(actual).toString());
    }


    public void test_determineArguments_noArgs() {
        String[] actual = task.determineArguments();
        assertNotNull(actual);
        assertEquals("[]", Arrays.asList(actual).toString());
    }


    public void test_determineOrderedArguments_override() {
        project.setProperty("gui.default.arg.0", "val1");
        project.setProperty("gui.default.arg.1", "val2");
        project.setProperty("gui.default.arg.2", "val3");
        task.setUser("bobo");
        task.setPassword("noWay");

        String[] actual = task.determineArguments();

        assertEquals("[bobo, noWay, val3]", Arrays.asList(actual).toString());
    }


    public void test_determineNamedArguments_override() {
        project.setProperty("gui.default.arg.user", "val1");
        project.setProperty("gui.default.arg.password", "val2");
        project.setProperty("gui.default.arg.server.host", "val3");
        project.setProperty("gui.default.arg.server.port", "val4");
        project.setProperty("gui.default.arg.specific", "val5");
        task.setUser("guigui");
        task.setPassword("topSecret");

        String[] actual = task.determineArguments();

        assertEquals("[guigui, topSecret, val3, val4, val5]", Arrays.asList(actual).toString());
    }


    public void test_determineArguments_overrideNoArgs() {
        task.setUser("bobo");
        task.setPassword("noWay");

        String[] actual = task.determineArguments();

        assertEquals("[bobo, noWay]", Arrays.asList(actual).toString());
    }


    public void test_guiTaskSpecificOrderedArgs() {
        // valeur récupérée par héritage
        task.getProject().setProperty("gui.default.class", "net.codjo.test.release.test.standard.SampleGui");
        task.getProject().setProperty("gui.admin.arg.0", "val0");
        task.getProject().setProperty("gui.admin.arg.1", "val1");
        // valeur récupérée par héritage
        task.getProject().setProperty("gui.default.arg.2", "val2");

        task.setGui("admin");

        String classPropertyName = task.getClassPropertyName();
        assertEquals("gui.admin.class", classPropertyName);
        String argPropertyName = task.getArgumentPropertyName();
        assertEquals("gui.admin.arg", argPropertyName);

        assertEquals("net.codjo.test.release.test.standard.SampleGui",
                     task.getClassValue());
        task.getProject()
              .setProperty("gui.admin.class", "net.codjo.test.release.test.tableeditor.SampleTableEditorGui");
        assertEquals("net.codjo.test.release.test.tableeditor.SampleTableEditorGui",
                     task.getClassValue());
        assertEquals("val0", task.getArgumentValue("0"));
        assertEquals("val1", task.getArgumentValue("1"));
        assertEquals("val2", task.getArgumentValue("2"));
    }


    public void test_guiTaskDefaultOrderedArgs() {
        task.getProject().setProperty("gui.default.class", "net.codjo.test.release.test.standard.SampleGui");
        task.getProject().setProperty("gui.default.arg.0", "val0");
        task.getProject().setProperty("gui.default.arg.1", "val1");

        String classPropertyName = task.getClassPropertyName();
        assertEquals("gui.default.class", classPropertyName);
        String argPropertyName = task.getArgumentPropertyName();
        assertEquals("gui.default.arg", argPropertyName);

        assertEquals("net.codjo.test.release.test.standard.SampleGui",
                     task.getClassValue());
        assertEquals("val0", task.getArgumentValue("0"));
        assertEquals("val1", task.getArgumentValue("1"));
    }


    public void test_guiTaskAdditionalArguments() {
        task.getProject().setProperty("gui.default.arg.0", "val0");
        task.getProject().setProperty("gui.default.arg.1", "val1");
        task.setAdditionalArguments("val2 val3");

        String[] actual = task.determineArguments();

        assertEquals("[val0, val1, val2, val3]", Arrays.asList(actual).toString());
    }


    public void test_guiTaskSpecificNamedArgs() {
        // valeur récupérée par héritage
        task.getProject().setProperty("gui.default.class", "net.codjo.test.release.test.standard.SampleGui");
        task.getProject().setProperty("gui.admin.arg.user", "val0");
        task.getProject().setProperty("gui.admin.arg.password", "val1");
        task.getProject().setProperty("gui.admin.arg.server.host", "val2");
        task.getProject().setProperty("gui.admin.arg.server.port", "val3");
        // valeur récupérée par héritage
        task.getProject().setProperty("gui.default.arg.specific", "val4");

        task.setGui("admin");

        String classPropertyName = task.getClassPropertyName();
        assertEquals("gui.admin.class", classPropertyName);
        String argPropertyName = task.getArgumentPropertyName();
        assertEquals("gui.admin.arg", argPropertyName);

        assertEquals("net.codjo.test.release.test.standard.SampleGui",
                     task.getClassValue());
        task.getProject()
              .setProperty("gui.admin.class", "net.codjo.test.release.test.tableeditor.SampleTableEditorGui");
        assertEquals("net.codjo.test.release.test.tableeditor.SampleTableEditorGui",
                     task.getClassValue());
        assertEquals("val0", task.getArgumentValue("user"));
        assertEquals("val1", task.getArgumentValue("password"));
        assertEquals("val2", task.getArgumentValue("server.host"));
        assertEquals("val3", task.getArgumentValue("server.port"));
        assertEquals("val4", task.getArgumentValue("specific"));
    }


    public void test_guiTaskDefaultNamedArgs() {
        task.getProject().setProperty("gui.default.class", "net.codjo.test.release.test.standard.SampleGui");
        task.getProject().setProperty("gui.default.arg.user", "val0");
        task.getProject().setProperty("gui.default.arg.password", "val1");

        String classPropertyName = task.getClassPropertyName();
        assertEquals("gui.default.class", classPropertyName);
        String argPropertyName = task.getArgumentPropertyName();
        assertEquals("gui.default.arg", argPropertyName);

        assertEquals("net.codjo.test.release.test.standard.SampleGui",
                     task.getClassValue());
        assertEquals("val0", task.getArgumentValue("user"));
        assertEquals("val1", task.getArgumentValue("password"));
    }


    public void test_execute_exit() throws Exception {
        task.getProject().setProperty("gui.default.class", "net.codjo.test.release.test.standard.ExitingGui");
        task.getProject().setProperty("gui.default.arg.user", "val0");
        task.getProject().setProperty("gui.default.arg.password", "val1");

        task.open();
        try {
            task.execute();
            fail("Un System.exit() doit génrérer une exception !!!");
        }
        catch (Exception e) {
            // Tout s'est bien passé.
        }
        finally {
            task.close();
        }
    }


    @Override
    protected void setUp() {
        project = new Project();
        project.addReference(TestEnvironment.class.getName(), new AgentTestEnvironment(project));

        task = new GuiTask();
        task.setProject(project);
        GuiTask secondTask = new GuiTask();
        secondTask.setProject(project);
    }
}
