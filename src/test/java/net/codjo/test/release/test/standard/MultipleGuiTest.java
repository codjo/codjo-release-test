/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.test.standard;
import net.codjo.test.release.test.AbstractSampleGuiTestCase;

public class MultipleGuiTest extends AbstractSampleGuiTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        AssertArgsClass.mainArgs = null;
    }


    public void test_story() throws Exception {
        runScenario("MultipleGuiScenario.xml");
    }


    public void test_storyMultipleGuiScenarioWithoutStop() throws Exception {
        runScenario("MultipleGuiScenario_withoutStop.xml");
    }


    public void test_multipleGui_multiSession() throws Exception {
        runScenario("MultipleGui_multiSession.xml");
    }


    public void test_multipleGui_noSession() throws Exception {
        runScenario("MultipleGui_noSession.xml");
    }


    public static final class AssertArgsClass {
        public static String[] mainArgs;


        private AssertArgsClass() {
        }


        public static void main(String[] args) {
            mainArgs = args;
        }
    }
}
