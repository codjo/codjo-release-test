/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import org.apache.tools.ant.BuildException;

/**
 * Exception levée lorsqu'une erreur survient lors d'un test IHM.
 */
public class GuiException extends BuildException {
    public GuiException(String message) {
        super(message);
    }


    public GuiException(String message, Throwable cause) {
        super(message, cause);
    }
}
