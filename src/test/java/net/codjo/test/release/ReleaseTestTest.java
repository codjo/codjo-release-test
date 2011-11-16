/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release;
import net.codjo.test.common.PathUtil;
import java.io.File;
import junit.framework.TestCase;
/**
 * Classe de test de {@link ReleaseTest}.
 */
public class ReleaseTestTest extends TestCase {
    private File dirFile;

    @Override
    protected void setUp() throws Exception {
        dirFile = PathUtil.findResourcesFileDirectory(getClass());
    }


    public void test_enabled() throws Exception {
        ReleaseTest releaseTest = new ReleaseTest(dirFile, new File(dirFile, "SampleGuiScenarioEnabled.xml"));
        assertTrue(releaseTest.isEnabled());
    }


    public void test_enabledNoAttr() throws Exception {
        ReleaseTest releaseTest =
            new ReleaseTest(dirFile, new File(dirFile, "SampleGuiScenarioEnabledNoAttr.xml"));
        assertTrue(releaseTest.isEnabled());
    }


    public void test_disabled() throws Exception {
        ReleaseTest releaseTest =
            new ReleaseTest(dirFile, new File(dirFile, "SampleGuiScenarioDisabled.xml"));
        assertFalse(releaseTest.isEnabled());
    }


    public void test_name() throws Exception {
        String testReleaseFileName = "SampleGuiScenarioDisabled";
        ReleaseTest releaseTest = new ReleaseTest(dirFile, new File(dirFile, testReleaseFileName + ".xml"));
        assertEquals("release__" + testReleaseFileName, releaseTest.getName());
    }
}
