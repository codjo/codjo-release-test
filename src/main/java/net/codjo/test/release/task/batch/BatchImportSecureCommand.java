package net.codjo.test.release.task.batch;
import net.codjo.test.release.task.util.RemoteCommand;
import net.codjo.test.release.util.ssh.ChannelExecSecureCommand;

class BatchImportSecureCommand extends ChannelExecSecureCommand implements RemoteCommand {

    BatchImportSecureCommand(String user, String host, String batchDirectory, String initiator, String file) {
        super(user, host, batchDirectory, "./import.ksh " + initiator + " " + file);
        setTimeout(360 * 1000);
    }
}
