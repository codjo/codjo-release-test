/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release;
import junit.framework.TestCase;
/**
 * Classe de test de {@link EnvironmentException}.
 */
public class RessourceExceptionTest extends TestCase {
    public void test_constructeurs() throws Exception {
        EnvironmentException ex;

        Exception cause = new Exception();
        ex = new EnvironmentException(cause);
        assertSame(cause, ex.getCause());
    }
}
