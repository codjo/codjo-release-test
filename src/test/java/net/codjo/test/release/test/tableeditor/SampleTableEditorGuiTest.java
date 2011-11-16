/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.test.tableeditor;
import net.codjo.test.release.test.AbstractSampleGuiTestCase;
/**
 * 
 */
public class SampleTableEditorGuiTest extends AbstractSampleGuiTestCase {
    public void test_comboEditable() throws Exception {
        runScenario("SampleTableEditorGuiScenario.xml");
    }


    public void test_treeEditionInsideATable() throws Exception {
        runScenario("SampleTableEditorWithTreeGuiScenario.xml");        
    }
}
