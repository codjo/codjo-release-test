package net.codjo.test.release.task.batch;
import net.codjo.test.release.task.util.RemoteCommand;
import net.codjo.test.release.util.ssh.ChannelExecSecureCommand;

class BatchSegmentationSecureCommand extends ChannelExecSecureCommand implements RemoteCommand {

    BatchSegmentationSecureCommand(String user,
                                   String host,
                                   String batchDirectory,
                                   String initiator,
                                   String segmentations,
                                   String date,
                                   String extraArgs) {
        this(user, host, DEFAULT_SSH_PORT, batchDirectory, initiator, segmentations, date, extraArgs);
    }


    BatchSegmentationSecureCommand(String user,
                                   String host,
                                   int port,
                                   String batchDirectory,
                                   String initiator,
                                   String segmentations,
                                   String date,
                                   String extraArgs) {
        super(user, host, port, batchDirectory, "./segmentation.ksh "
                                                + initiator + " "
                                                + "\"" + segmentations + "\" "
                                                + date
                                                + (extraArgs == null || "".equals(extraArgs) ?
                                                   "" :
                                                   " " + extraArgs));
        setTimeout(360 * 1000);
    }
}