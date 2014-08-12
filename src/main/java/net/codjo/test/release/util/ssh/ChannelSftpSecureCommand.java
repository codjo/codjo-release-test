package net.codjo.test.release.util.ssh;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
/**
 *
 */
public abstract class ChannelSftpSecureCommand extends SecureCommand<ChannelSftp> {

    protected ChannelSftpSecureCommand(String user, String host, int port) {
        super("sftp", user, host, port);
    }


    protected void cd(ChannelSftp channel, String remoteDirectory) throws SftpException {
        info("[sftp] cd " + remoteDirectory);
        channel.cd(remoteDirectory);
    }


    protected void get(ChannelSftp channel, String fileToGet, String localDirectory) throws SftpException {
        info("[sftp] get " + fileToGet + " " + localDirectory);
        channel.get(fileToGet, localDirectory);
    }


    protected void rm(ChannelSftp channel, String fileToGet) throws SftpException {
        info("[sftp] rm " + fileToGet);
        channel.rm(fileToGet);
    }


    protected void put(ChannelSftp channel, String source, int overwrite) throws SftpException {
        info("[sftp] put " + source + " . " + overwrite);
        channel.put(source, ".", overwrite);
    }
}
