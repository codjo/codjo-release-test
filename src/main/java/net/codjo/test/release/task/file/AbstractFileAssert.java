package net.codjo.test.release.task.file;
import net.codjo.test.release.task.AgfTask;
import net.codjo.test.release.task.util.RemoteCommand;
import java.io.File;
import org.apache.tools.ant.BuildException;

public abstract class AbstractFileAssert extends AgfTask {
    private boolean remoteEnabled = true;
    private RemoteCommand remoteCommand;


    public void setRemote(boolean remoteEnabled) {
        this.remoteEnabled = remoteEnabled;
    }


    protected abstract String getFileName();


    protected void copyFromRemote() {
        if (getTestEnvironement().isRemoteMode() && remoteEnabled) {
            try {
                new File(getProperty(BROADCAST_LOCAL_DIR, true)).mkdirs();
                getCopyFromRemoteCommand().execute();
            }
            catch (BuildException e) {
                throw e;
            }
            catch (Exception e) {
                throw new BuildException("Impossible de récupérer les fichiers distants", e);
            }
        }
    }


    protected File getActualFile() {
        return new File(getProperty(BROADCAST_LOCAL_DIR, true), getFileName());
    }


    private RemoteCommand getCopyFromRemoteCommand() {
        if (remoteCommand == null) {
            remoteCommand = new FileAssertRemoteCommand(getRemoteUser(),
                                                        getRemoteServer(),
                                                        getProperty(BROADCAST_REMOTE_DIR, true),
                                                        getProperty(BROADCAST_LOCAL_DIR, true),
                                                        getFileName());
            remoteCommand.setLog(logger);
        }
        return remoteCommand;
    }


    void setCopyFromRemoteCommand(RemoteCommand remoteCommand) {
        this.remoteCommand = remoteCommand;
    }
}
