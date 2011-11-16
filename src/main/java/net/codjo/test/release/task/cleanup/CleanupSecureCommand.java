package net.codjo.test.release.task.cleanup;
import net.codjo.test.release.task.util.RemoteCommand;
import net.codjo.test.release.util.ssh.ChannelExecSecureCommand;
/**
 *
 */
class CleanupSecureCommand extends ChannelExecSecureCommand implements RemoteCommand {

    CleanupSecureCommand(String user, String host, String remoteDirectory) {
        super(user, host, remoteDirectory, "rm -rf *");
        setTimeout(20 * 1000);
    }
}
