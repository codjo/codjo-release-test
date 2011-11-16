/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task;
import javax.swing.JComboBox;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
/**
 * DOCUMENT ME!
 *
 * @version $Revision: 1.7 $
 */
public class UtilTest extends TestCase {
    public UtilTest(String name) {
        super(name);
    }

    public void test_compare() throws Exception {
        Util.compare("same", "same");
        Util.compare("same\nê", "same\nê");
    }


    public void test_compare_diff() throws Exception {
        boolean failed = true;
        try {
            Util.compare("val A", "val B");
            failed = false;
            System.out.println("sssssssss");
        }
        catch (AssertionFailedError e) {
            failed = true;
        }
        assertTrue("La comparaison doit echouer", failed);
    }


    public void test_flatten() throws Exception {
        String val = "le petit    chien \n est grand";
        Util.compare("le petit chien est grand", Util.flatten(val));

        val = "le petit    chien\nest grand";
        Util.compare("le petit chienest grand", Util.flatten(val));
    }


    public void test_computeClassName() throws Exception {
        assertEquals("JComboBox", Util.computeClassName(JComboBox.class));
        assertEquals("UtilTest", Util.computeClassName(this.getClass()));
    }
}
