package net.codjo.test.release.task.batch;
import net.codjo.test.release.task.imports.CopyToInbox;
import net.codjo.test.release.task.util.RemoteCommand;
import java.util.Arrays;
import java.util.List;
/**
 *
 */
public class BatchImportTask extends AbstractBatchTask {
    private String initiator;
    private String file;
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


    @Override
    public int getReturnCode() {
        return returnCode;
    }


    @Override
    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }


    @Override
    protected String getExecuteDescription() {
        return "batch-import initiator >" + getInitiator() + "< file >" + getFile() + "<...";
    }


    @Override
    protected RemoteCommand createRemoteCommand() {
        return new BatchImportSecureCommand(getProperty(REMOTE_USER, true),
                                            getProperty(REMOTE_SERVER, true),
                                            getRemoteBatchDir(),
                                            getInitiator(),
                                            getFile());
    }


    @Override
    protected List<String> buildLocalArguments() {
        return Arrays.asList(
              "-initiator", getInitiator(),
              "-type", "import",
              "-argument", CopyToInbox.fileInTemp(getProject(), getFile()).toString(),
              "-configuration", getProperty(AbstractBatchTask.BATCH_CONFIGURATION, true));
    }
}
