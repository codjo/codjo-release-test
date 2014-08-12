package net.codjo.test.release.task.batch;
import net.codjo.test.release.task.util.RemoteCommand;
import net.codjo.test.release.util.ssh.ChannelExecSecureCommand;

class BatchImportSecureCommand extends ChannelExecSecureCommand implements RemoteCommand {

    BatchImportSecureCommand(String user, String host, String batchDirectory, String initiator, String file) {
        this(user, host, DEFAULT_SSH_PORT, batchDirectory, initiator, file);
    }


    BatchImportSecureCommand(String user, String host, int port, String batchDirectory, String initiator, String file) {
        super(user, host, port, batchDirectory, "./import.ksh " + initiator + " " + file);
        setTimeout(360 * 1000);
    }
}
