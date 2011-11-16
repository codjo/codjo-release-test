package net.codjo.test.release.task.cleanup;
import net.codjo.test.common.Directory;
import net.codjo.test.common.Directory.NotDeletedException;
import net.codjo.test.release.task.AgfTask;
import net.codjo.test.release.task.util.RemoteCommand;
import java.io.File;
import org.apache.tools.ant.BuildException;

public class CleanupTask extends AgfTask {
    private String dir;
    private RemoteCommand remoteCommand;


    public String getDir() {
        return dir;
    }


    public void setDir(String dir) {
        this.dir = dir;
    }


    @Override
    public void execute() {
        debug("Begin cleaning up");
        if (getDir() == null) {
            throw new BuildException("L'attribut 'dir' est obligatoire et n'est pas spécifié.");
        }

        if (getTestEnvironement().isRemoteMode()) {
            info("Cleaning up directory : " + getProperty(getDir() + REMOTE_SUFFIX, true));
            doRemoteCleanup();
        }
        else {
            info("Cleaning up directory : " + getProperty(getDir() + LOCAL_SUFFIX, true));
            doLocalCleanup();
        }
    }


    private RemoteCommand getRemoteCommand() {
        if (remoteCommand == null) {
            remoteCommand = new CleanupSecureCommand(getRemoteUser(),
                                                     getRemoteServer(),
                                                     getProperty(getDir() + REMOTE_SUFFIX, true));
            remoteCommand.setLog(logger);
        }
        return remoteCommand;
    }


    void setRemoteCommand(RemoteCommand remoteCommand) {
        this.remoteCommand = remoteCommand;
    }


    private void doLocalCleanup() {
        File folder = new File(getProperty(getDir() + LOCAL_SUFFIX, true));
        if (!folder.exists()) {
            return;
        }
        try {
            for (File file : folder.listFiles()) {
                new Directory(file.getPath()).deleteRecursively();
            }
        }
        catch (NotDeletedException e) {
            throw new BuildException("Impossible de supprimer le fichier " + e.getLocalizedMessage());
        }
    }


    private void doRemoteCleanup() {
        try {
            getRemoteCommand().execute();
        }
        catch (Exception e) {
            throw new BuildException("Impossible de supprimer les fichiers");
        }
    }
}
