/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.ant;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.Diagnostics;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.util.JavaEnvUtils;
/**
 * Cette classe permet d'exécuer un script Ant.
 *
 * @version $Revision: 1.12 $
 */
public final class AntRunner {
    /**
     * Cache of the Ant version information when it has been loaded.
     */
    private static String antVersion = null;
    /**
     * File that we are using for configuration.
     */
    private File buildFile;


    private AntRunner() {
    }


    /**
     * Exécute un fichier Ant
     *
     * @param buildFile le fichier à exécuter
     */
    public static void start(File buildFile) {
        start(new Project(), buildFile);
    }


    public static void start(Project project, File buildFile) {
        Diagnostics.validateVersion();
        AntRunner runner = new AntRunner();
        runner.buildFile = buildFile;
        runner.runProject(project);
    }


    /**
     * Exécute un projet Ant
     *
     * @param project le projet à exécuter
     */
    private void runProject(Project project) {
        // Add the default listener
        project.addBuildListener(createLogger());

        // use a system manager that prevents from System.exit()
        // only in JDK > 1.1
        SecurityManager oldsm = null;
        if (!JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_0)
            && !JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_1)) {
            oldsm = System.getSecurityManager();

            //SecurityManager can not be installed here for backwards
            //compatability reasons (PD). Needs to be loaded prior to
            //ant class if we are going to implement it.
            //System.setSecurityManager(new NoExitSecurityManager());
        }

        try {
            project.fireBuildStarted();
            project.init();
            project.setUserProperty("ant.version", getAntVersion());
            project.setUserProperty("ant.file", buildFile.getAbsolutePath());
            ProjectHelper projectHelper = ProjectHelper.getProjectHelper();
            projectHelper.parse(project, buildFile);

            project.executeTarget(project.getDefaultTarget());

            project.fireBuildFinished(null);
        }
        catch (RuntimeException e) {
            project.fireBuildFinished(e);
            throw e;
        }
        finally {
            // put back the original security manager
            //The following will never eval to true. (PD)
            if (oldsm != null) {
                System.setSecurityManager(oldsm);
            }
        }
    }


    /**
     * Crée le logger qui affiche les logs Ant sur System.out et System.err
     *
     * @return the logger instance for this build.
     */
    private BuildLogger createLogger() {
        BuildLogger logger = new TestLogger();

        logger.setMessageOutputLevel(Project.MSG_INFO);
        logger.setOutputPrintStream(System.out);
        logger.setErrorPrintStream(System.err);
        logger.setEmacsMode(false);

        return logger;
    }


    /**
     * Returns the Ant version information, if available. Once the information has been loaded once, it's
     * cached and returned from the cache on future calls.
     *
     * @return the Ant version information as a String (always non-<code>null</code> )
     *
     * @throws BuildException if the version information is unavailable
     */
    public static synchronized String getAntVersion()
          throws BuildException {
        if (antVersion == null) {
            try {
                Properties props = new Properties();
                InputStream in = AntRunner.class.getResourceAsStream("/org/apache/tools/ant/version.txt");
                props.load(in);
                in.close();

                StringBuffer msg = new StringBuffer();
                msg.append("Apache Ant version ");
                msg.append(props.getProperty("VERSION"));
                msg.append(" compiled on ");
                msg.append(props.getProperty("DATE"));
                antVersion = msg.toString();
            }
            catch (IOException ioe) {
                throw new BuildException("Could not load the version information:" + ioe.getMessage());
            }
            catch (NullPointerException npe) {
                throw new BuildException("Could not load the version information.");
            }
        }
        return antVersion;
    }
}
