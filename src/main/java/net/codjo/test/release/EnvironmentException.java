/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release;
import org.apache.tools.ant.BuildException;
/**
 * Gestion des exceptions lancées suite des erreurs dans {@link TestEnvironment#open()} et {@link
 * TestEnvironment#close()}.
 */
public class EnvironmentException extends BuildException {
    public EnvironmentException(Throwable cause) {
        super(cause);
    }
}
