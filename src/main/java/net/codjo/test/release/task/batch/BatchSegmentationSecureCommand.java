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
        super(user, host, batchDirectory, "./segmentation.ksh "
                                          + initiator + " "
                                          + "\"" + segmentations + "\" "
                                          + date
                                          + (extraArgs == null || "".equals(extraArgs) ?
                                             "" :
                                             " " + extraArgs));
        setTimeout(360 * 1000);
    }
}