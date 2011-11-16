/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
/**
 * Exception levée lorsqu'un composant n'est pas trouvé lors d'un test IHM.
 */
public class GuiFindException extends GuiException {
    public GuiFindException(String message) {
        super(message);
    }


    public GuiFindException(String message, Throwable cause) {
        super(message, cause);
    }
}
