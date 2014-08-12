package net.codjo.test.release.task.batch;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.codjo.test.release.task.Arg;
import net.codjo.test.release.task.util.RemoteCommand;
/**
 *
 */
public class BatchSegmentationTask extends AbstractBatchTask {
    private String initiator;
    private String segmentations;
    private String date;
    private int returnCode = 0;


    public String getInitiator() {
        return initiator;
    }


    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }


    public String getSegmentations() {
        return segmentations;
    }


    public void setSegmentations(String segmentations) {
        this.segmentations = segmentations;
    }


    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }


    @Override
    public int getReturnCode() {
        return returnCode;
    }


    @Override
    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }


    public void addExtraArg(Arg arg) {
        addArg(arg);
    }


    @Override
    protected String getExecuteDescription() {
        return "batch-segmentation initiator >" + getInitiator() + "< segmentations >" + getSegmentations()
               + "<...";
    }


    @Override
    protected RemoteCommand createRemoteCommand() {
        return new BatchSegmentationSecureCommand(getProperty(REMOTE_USER, true),
                                                  getProperty(REMOTE_SERVER, true),
                                                  getPort(),
                                                  getRemoteBatchDir(),
                                                  getInitiator(),
                                                  getSegmentations(),
                                                  getDate(),
                                                  argumentsToString(getArgs()));
    }


    @Override
    protected List<String> buildLocalArguments() {
        List<String> args = new ArrayList<String>(Arrays.asList(
              "-initiator", getInitiator(),
              "-type", "segmentation",
              "-segmentations", getSegmentations(),
              "-configuration", getProperty(AbstractBatchTask.BATCH_CONFIGURATION, true),
              "-date", getDate()));
        args.addAll(argumentsToList(argumentsToString(getArgs())));
        return args;
    }
}