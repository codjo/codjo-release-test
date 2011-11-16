/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task;
/**
 * 
 */
public class InvalidAttributesException extends RuntimeException {
    public InvalidAttributesException() {
        super("Attributs invalides. La balise <arg/> doit comporter un et un seul attribut parmi 'line',"
            + " 'importFile', 'exportFile'.");
    }
}
