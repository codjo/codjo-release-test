/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.ConnectionMetadata;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.tools.ant.Project;
/**
 * Environnement de test lié à l'application.
 */
public abstract class AbstractTestEnvironment implements TestEnvironment {
    private Project project;
    private Connection connection;


    protected AbstractTestEnvironment(Project project) {
        this.project = project;
    }


    public Project getProject() {
        return project;
    }


    public Connection getConnection() throws SQLException {
        if (connection == null) {
            initConnection();
        }
        return connection;
    }


    public String getDefaultUser() {
        return getProperty("testEnvironment.user");
    }


    public String getDefaultPassword() {
        return getProperty("testEnvironment.pwd");
    }


    private void initConnection() throws SQLException {
        Properties releaseTestProperties = new Properties();
        releaseTestProperties.putAll(project.getProperties());
        connection = new DatabaseFactory().createDatabaseHelper()
              .createConnection(new ConnectionMetadata(releaseTestProperties));
    }


    public boolean isRemoteMode() {
        return "YES".equalsIgnoreCase(System.getProperty("agf.test.remote"))
               || "YES".equalsIgnoreCase(project.getProperty("agf.test.remote"));
    }


    protected String getProperty(String name) {
        return project.getProperty(name);
    }


    public void close() {
        if (connection != null) {
            try {
                connection.close();
            }
            catch (SQLException e) {
                throw new EnvironmentException(e);
            }
        }
    }


    public void open() {
    }
}
