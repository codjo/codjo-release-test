package net.codjo.test.release.task.imports;
import net.codjo.test.release.task.util.RemoteCommand;
import net.codjo.test.release.util.ssh.ChannelSftpSecureCommand;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
/**
 *
 */
class CopyToInboxSecureCommand extends ChannelSftpSecureCommand implements RemoteCommand {
    private final String source;
    private final String remoteDirectory;


    CopyToInboxSecureCommand(String user, String host, String source, String remoteDirectory) {
        super(user, host);
        this.source = source;
        this.remoteDirectory = remoteDirectory;
    }


    @Override
    protected void execute(ChannelSftp channel) throws SftpException {
        cd(channel, remoteDirectory);
        put(channel, source, ChannelSftp.OVERWRITE);
    }
}
