package net.codjo.test.release.task.file;
import net.codjo.test.release.task.util.RemoteCommand;
import net.codjo.test.release.util.ssh.ChannelSftpSecureCommand;
import com.jcraft.jsch.ChannelSftp;
/**
 *
 */
class FileAssertRemoteCommand extends ChannelSftpSecureCommand implements RemoteCommand {
    private final String remoteDirectory;
    private final String localDirectory;
    private final String fileToGet;


    FileAssertRemoteCommand(String user,
                            String host,
                            String remoteDirectory,
                            String localDirectory,
                            String fileToGet) {
        super(user, host);
        this.remoteDirectory = remoteDirectory;
        this.localDirectory = localDirectory;
        this.fileToGet = fileToGet;
    }


    @Override
    protected void execute(ChannelSftp channel) throws Exception {
        cd(channel, remoteDirectory);
        get(channel, fileToGet, localDirectory);
        rm(channel, fileToGet);
    }
}
