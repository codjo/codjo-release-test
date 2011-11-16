/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task;
import net.codjo.test.release.TestEnvironment;
import net.codjo.test.release.agent.AgentTestEnvironment;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.util.TaskLogger;
/**
 * Tag release-test.
 *
 * @author $Author: crego $
 * @version $Revision: 1.18 $
 */
public class ReleaseTask extends Task implements TaskContainer {
    private TaskLogger logger = new TaskLogger(this);
    private List<Task> tasks = new ArrayList<Task>();
    private String name;
    private boolean enabled = true;


    public boolean isEnabled() {
        return enabled;
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }


    public void addTask(Task task) {
        logger.debug("Ajout du tag : " + task);
        if (task instanceof UnknownElement) {
            task.maybeConfigure();
            task = (Task)((UnknownElement)task).getRealThing();
        }
        tasks.add(task);
    }


    @Override
    public void execute() {
        logger.info("Exécution test " + getName());
        try {
            executeImpl();
        }
        catch (IllegalAccessException e) {
            throw new BuildException(e);
        }
        catch (NoSuchMethodException e) {
            throw new BuildException(e);
        }
        catch (InvocationTargetException e) {
            throw new BuildException(e);
        }
        catch (InstantiationException e) {
            throw new BuildException(e);
        }
        catch (ClassNotFoundException e) {
            throw new BuildException(e);
        }
    }


    private void executeImpl()
          throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
                 InvocationTargetException, InstantiationException {
        TestEnvironment testEnvironment;
        String environmentClass = getProject().getProperty("testEnvironment.class");
        if (environmentClass != null) {
            Constructor constructor = Class.forName(environmentClass).getConstructor(Project.class);
            testEnvironment = (TestEnvironment)constructor.newInstance(getProject());
        }
        else {
            testEnvironment = new AgentTestEnvironment(getProject());
        }
        getProject().addReference(TestEnvironment.class.getName(), testEnvironment);
        testEnvironment.open();
        if (testEnvironment.isRemoteMode()) {
            logger.info("\ten mode REMOTE_SUFFIX");
        }
        else {
            logger.info("\ten mode LOCAL_SUFFIX");
        }
        try {
            openResources();
            for (Object task : tasks) {
                Task subTask = (Task)task;
                subTask.perform();
            }
        }
        finally {
            try {
                closeRessources();
            }
            finally {
                testEnvironment.close();
            }
        }
    }


    private void openResources() {
        for (Task subTask : tasks) {
            if (subTask instanceof Resource) {
                ((Resource)subTask).open();
            }
        }
    }


    private void closeRessources() {
        for (Task subTask : tasks) {
            if (subTask instanceof Resource) {
                ((Resource)subTask).close();
            }
        }
    }


    public void addDescription(Description other) {
    }
}
