/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.imports;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;
/**
 * DOCUMENT ME!
 *
 * @version $Revision: 1.6 $
 */
public class VariableTest extends TestCase {
    private Variable var;

    public VariableTest(String name) {
        super(name);
    }

    public void test_formatValue() throws Exception {
        var.setValue("sss");
        assertEquals("sss", var.formatValue());
    }


    public void test_formatValue_format() throws Exception {
        var.setValue("today");
        var.setFormat("dd/MM/yyyy");
        assertEquals(new SimpleDateFormat("dd/MM/yyyy").format(new Date()), var.formatValue());
    }


    @Override
    protected void setUp() throws Exception {
        var = new Variable();
    }
}
