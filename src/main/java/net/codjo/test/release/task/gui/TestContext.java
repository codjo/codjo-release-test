/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.JFCTestHelper;
import net.codjo.test.release.task.util.TestLocation;
import org.apache.tools.ant.Project;
/**
 * Contexte d'exécution des tests passé entre les steps.
 */
public class TestContext {
    private static final String TEST_LOCATION = "testLocation";
    private static final String CURRENT_COMPONENT = "currentComponent";
    private JFCTestCase testCase = null;
    private JFCTestHelper helper = new JFCTestHelper();
    private Project project;
    private Map objects = new HashMap();


    public TestContext(JFCTestCase testCase) {
        this(testCase, new Project());
    }


    public TestContext(JFCTestCase testCase, Project project) {
        testCase.setHelper(helper);
        this.testCase = testCase;
        this.project = project;
        putObject(TEST_LOCATION, new TestLocation());
    }


    public JFCTestHelper getHelper() {
        return helper;
    }


    public JFCTestCase getTestCase() {
        return testCase;
    }


    public String replaceProperties(String value) {
        return project.replaceProperties(value);
    }


    public String getProperty(String name) {
        return project.getProperty(name);
    }


    public void setProperty(String name, String value) {
        project.setProperty(name, value);
    }


    public void setCurrentComponent(Component component) {
        putObject(CURRENT_COMPONENT, component);
    }


    public Component getCurrentComponent() {
        return (Component)getObject(CURRENT_COMPONENT);
    }


    public TestLocation getTestLocation() {
        return (TestLocation)getObject(TEST_LOCATION);
    }


    private void putObject(Object key, Object value) {
        objects.put(key, value);
    }


    private Object getObject(Object key) {
        return objects.get(key);
    }
}
