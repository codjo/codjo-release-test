package net.codjo.test.release.task.imports;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import net.codjo.test.release.task.util.RemoteCommand;
import net.codjo.test.release.util.ssh.ChannelSftpSecureCommand;
/**
 *
 */
class CopyToInboxSecureCommand extends ChannelSftpSecureCommand implements RemoteCommand {
    private final String source;
    private final String remoteDirectory;


    CopyToInboxSecureCommand(String user, String host, String source, String remoteDirectory) {
        this(user, host, DEFAULT_SSH_PORT, source, remoteDirectory);
    }

    CopyToInboxSecureCommand(String user, String host, int port, String source, String remoteDirectory) {
        super(user, host, port);
        this.source = source;
        this.remoteDirectory = remoteDirectory;
    }


    @Override
    protected void execute(ChannelSftp channel) throws SftpException {
        cd(channel, remoteDirectory);
        put(channel, source, ChannelSftp.OVERWRITE);
    }
}
