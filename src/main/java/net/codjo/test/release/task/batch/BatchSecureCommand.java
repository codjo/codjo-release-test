package net.codjo.test.release.task.batch;
import net.codjo.test.release.task.util.RemoteCommand;
import net.codjo.test.release.util.ssh.ChannelExecSecureCommand;
/**
 *
 */
class BatchSecureCommand extends ChannelExecSecureCommand implements RemoteCommand {

    BatchSecureCommand(String user, String host, String workingDir, String script, String arg) {
        this(user, host, DEFAULT_SSH_PORT, workingDir, script, arg);
    }


    BatchSecureCommand(String user, String host, int port, String workingDir, String script, String arg) {
        super(user, host, port, workingDir, "./" + script + " " + arg);
    }
}
