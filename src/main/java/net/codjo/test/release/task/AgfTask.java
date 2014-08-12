/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import net.codjo.test.release.TestEnvironment;
import net.codjo.test.release.util.ssh.SecureCommand;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.TaskLogger;
/**
 * Classe de base pour les Task.
 */
public abstract class AgfTask extends Task {
    public static final String REMOTE_SERVER = "remote.server";
    public static final String REMOTE_USER = "remote.user";
    public static final String REMOTE_PORT = "remote.port";
    public static final String REMOTE_PROJECT_DIR = "remote.project.dir";

    public static final String GUI_PREFIX = "gui";
    public static final String CLASS_SUFFIX = "class";
    public static final String METHOD_SUFFIX = "method";
    public static final String ARG_SUFFIX = "arg";
    public static final String GUI_DEFAULT_CLASS = "gui.default.class";
    public static final String GUI_DEFAULT_ARG = "gui.default.arg";

    @Deprecated
    private static final String FTP_SERVER = "ftp.server";
    @Deprecated
    private static final String FTP_USER = "ftp.user";
    @Deprecated
    private static final String FTP_REMOTE_DIR = "ftp.remoteDir";

    public static final String BROADCAST_REMOTE_DIR = "broadcast.output.remote.dir";
    public static final String BROADCAST_LOCAL_DIR = "broadcast.output.dir";
    public static final String TEST_DIRECTORY = "test.dir";
    public static final String REMOTE_SUFFIX = ".remote";
    public static final String LOCAL_SUFFIX = ".local";

    protected TaskLogger logger = new TaskLogger(this);


    public String getRemoteServer() {
        try {
            return getProperty(REMOTE_SERVER, true);
        }
        catch (BuildException e) {
            info("############################################################################");
            info("# !!!                                                                  !!! #");
            info("# !!! Vous devez spécifier remote.server dans test-release.config      !!! #");
            info("# !!!                                                                  !!! #");
            info("############################################################################");
            return getProperty(FTP_SERVER);
        }
    }


    public String getRemoteUser() {
        try {
            return getProperty(REMOTE_USER, true);
        }
        catch (BuildException e) {
            info("############################################################################");
            info("# !!!                                                                  !!! #");
            info("# !!! Vous devez spécifier remote.user dans test-release.config        !!! #");
            info("# !!!                                                                  !!! #");
            info("############################################################################");
            return getProperty(FTP_USER);
        }
    }


    public int getPort() {
        return Integer.parseInt(getProperty(REMOTE_PORT, "" + SecureCommand.DEFAULT_SSH_PORT));
    }


    public String getRemoteBatchDir() {
        return getProperty(REMOTE_PROJECT_DIR, true) + "/APP1/BATCH";
    }


    public String getRemoteInDir() {
        try {
            return getProperty(REMOTE_PROJECT_DIR, true) + "/DAT1/IN";
        }
        catch (BuildException e) {
            info("############################################################################");
            info("# !!!                                                                  !!! #");
            info("# !!! Vous devez spécifier remote.project.dir dans test-release.config !!! #");
            info("# !!!                                                                  !!! #");
            info("############################################################################");
            return getProperty(FTP_REMOTE_DIR);
        }
    }


    public String getRemoteOutDir() {
        return getProperty(REMOTE_PROJECT_DIR, true) + "/DAT1/OUT";
    }


    @Override
    public abstract void execute();


    protected void info(String msg) {
        logger.info(msg);
    }


    protected void debug(String msg) {
        logger.debug(msg);
    }


    protected void error(Throwable error) {
        String message = error.getLocalizedMessage();
        if (message != null) {
            logger.error(message);
        }
        else {
            logger.error("Erreur " + error.getClass());
        }
    }


    protected File toAbsoluteFile(String fileName) {
        return new File(getProperty(TEST_DIRECTORY, true), fileName);
    }


    protected Connection getConnection() throws SQLException {
        return getTestEnvironement().getConnection();
    }


    protected String getProperty(String property) {
        return getProperty(property, false);
    }


    protected String getProperty(String property, String defaultValue) {
        String value = getProperty(property, false);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }


    protected String getProperty(String property, boolean checkProperty) {
        if (checkProperty) {
            checkProperty(property);
        }
        return getProject().getProperty(property);
    }


    protected Object getReference(String ref) {
        return getProject().getReference(ref);
    }


    protected void addReference(String key, Object value) {
        getProject().addReference(key, value);
    }


    protected void removeReference(String key) {
        getProject().getReferences().remove(key);
    }


    protected TestEnvironment getTestEnvironement() {
        return ((TestEnvironment)getReference(TestEnvironment.class.getName()));
    }


    private void checkProperty(String property) {
        if (!getProject().getProperties().containsKey(property)) {
            throw new BuildException("manque argument " + property + " dans le fichier de configuration");
        }
    }
}
