package net.codjo.test.release.task.batch;
import net.codjo.test.release.task.Arg;
import net.codjo.test.release.task.util.RemoteCommand;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 *
 */
public class BatchExportTask extends AbstractBatchTask {
    private String initiator;
    private String file;
    private String date;
    private boolean unix2dos = false;
    private int returnCode = 0;


    public String getInitiator() {
        return initiator;
    }


    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }


    public String getFile() {
        return file;
    }


    public void setFile(String file) {
        this.file = file;
    }


    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }


    public boolean isUnix2dos() {
        return unix2dos;
    }


    public void setUnix2dos(boolean unix2dos) {
        this.unix2dos = unix2dos;
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
        return "batch-export initiator >" + getInitiator() + "< file >" + getFile() + "<...";
    }


    @Override
    protected RemoteCommand createRemoteCommand() {
        return new BatchExportSecureCommand(getProperty(REMOTE_USER, true),
                                            getProperty(REMOTE_SERVER, true),
                                            getRemoteBatchDir(),
                                            getInitiator(),
                                            getFile(),
                                            getDate(),
                                            unix2dos,
                                            argumentsToString(getArgs()));
    }


    @Override
    protected List<String> buildLocalArguments() {
        List<String> args = new ArrayList<String>(Arrays.asList(
              "-initiator", getInitiator(),
              "-type", "broadcast",
              "-argument", getAndCreateBroadcasLocalDir(),
              "-configuration", getProperty(AbstractBatchTask.BATCH_CONFIGURATION, true),
              "-date", getDate()));
        args.addAll(argumentsToList(argumentsToString(getArgs())));
        return args;
    }


    private String getAndCreateBroadcasLocalDir() {
        File broadcastLocalDir = new File(getProperty(BROADCAST_LOCAL_DIR, true));
        broadcastLocalDir.mkdirs();
        return new File(broadcastLocalDir, getFile()).getPath();
    }
}
