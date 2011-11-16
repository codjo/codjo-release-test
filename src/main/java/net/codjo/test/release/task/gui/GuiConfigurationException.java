/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
/**
 * Exception levée lorsqu'il y a une erreur de configuration.
 */
public class GuiConfigurationException extends GuiException {
    public GuiConfigurationException(String message) {
        super(message);
    }


    public GuiConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
