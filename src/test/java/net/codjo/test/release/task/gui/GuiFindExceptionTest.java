/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import junit.framework.TestCase;
/**
 * Classe de test de {@link GuiFindException}.
 */
public class GuiFindExceptionTest extends TestCase {
    public void test_constructeurs() throws Exception {
        GuiFindException ex;

        ex = new GuiFindException("msg");
        assertEquals("msg", ex.getMessage());

        Exception cause = new Exception();
        ex = new GuiFindException("msg", cause);
        assertEquals("msg", ex.getMessage());
        assertSame(cause, ex.getCause());
    }
}
