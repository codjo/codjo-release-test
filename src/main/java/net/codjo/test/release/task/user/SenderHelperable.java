/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.user;
/**
 */
public interface SenderHelperable {
    void open();


    void close();


    String sendRequest(String text);
}
