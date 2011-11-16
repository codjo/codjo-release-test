/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release;
import net.codjo.test.release.task.user.SenderHelperable;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.tools.ant.Project;
/**
 * Définit l'environnement de test d'une application.
 */
public interface TestEnvironment {
    Project getProject();


    Connection getConnection() throws SQLException;


    boolean isRemoteMode();


    String getDefaultUser();


    String getDefaultPassword();


    SenderHelperable createSendStrategy(String user, String password);


    void close();


    void open();
}
