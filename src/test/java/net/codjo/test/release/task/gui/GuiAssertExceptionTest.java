/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import junit.framework.TestCase;
/**
 * Classe de test de {@link GuiAssertException}.
 */
public class GuiAssertExceptionTest extends TestCase {
    public void test_constructeurs() throws Exception {
        GuiAssertException ex;

        ex = new GuiAssertException("msg");
        assertEquals("msg", ex.getMessage());

        Exception cause = new Exception();
        ex = new GuiAssertException("msg", cause);
        assertEquals("msg", ex.getMessage());
        assertSame(cause, ex.getCause());
    }
}
