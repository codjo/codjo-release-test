package net.codjo.test.release.task.batch;
import net.codjo.test.release.task.util.RemoteCommand;
import net.codjo.test.release.util.ssh.ChannelExecSecureCommand;

class BatchExportSecureCommand extends ChannelExecSecureCommand implements RemoteCommand {

    BatchExportSecureCommand(String user,
                             String host,
                             int port,
                             String batchDirectory,
                             String initiator,
                             String file,
                             String date,
                             boolean unix2dos,
                             String extraArgs) {
        super(user, host, port, batchDirectory, "./export.ksh "
                                                + initiator + " "
                                                + file + " "
                                                + date + " "
                                                + (unix2dos ? "unix2dos" : "sans_unix2dos")
                                                + (extraArgs == null || "".equals(extraArgs) ?
                                                   "" :
                                                   " " + extraArgs));
        setTimeout(360 * 1000);
    }


    BatchExportSecureCommand(String user,
                             String host,
                             String batchDirectory,
                             String initiator,
                             String file,
                             String date,
                             boolean unix2dos,
                             String extraArgs) {
        this(user, host, DEFAULT_SSH_PORT, batchDirectory, initiator, file, date, unix2dos, extraArgs);
    }
}