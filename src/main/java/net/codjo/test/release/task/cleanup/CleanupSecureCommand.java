package net.codjo.test.release.task.cleanup;
import net.codjo.test.release.task.util.RemoteCommand;
import net.codjo.test.release.util.ssh.ChannelExecSecureCommand;
/**
 *
 */
class CleanupSecureCommand extends ChannelExecSecureCommand implements RemoteCommand {

    CleanupSecureCommand(String user, String host, String remoteDirectory) {
        this(user, host, DEFAULT_SSH_PORT, remoteDirectory);
    }

    CleanupSecureCommand(String user, String host, int port, String remoteDirectory) {
        super(user, host, port, remoteDirectory, "rm -rf *");
        setTimeout(20 * 1000);
    }
}
