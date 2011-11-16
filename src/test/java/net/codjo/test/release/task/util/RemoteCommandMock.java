package net.codjo.test.release.task.util;
import net.codjo.test.common.LogString;
import org.apache.tools.ant.util.TaskLogger;
/**
 *
 */
public class RemoteCommandMock implements RemoteCommand {
    private final LogString log;
    private boolean mustFail = false;
    private int returnCode = 0;


    public RemoteCommandMock() {
        this(new LogString());
    }


    public RemoteCommandMock(LogString log) {
        this.log = log;
    }


    public int execute() throws Exception {
        log.call("execute");
        if (mustFail) {
            throw new Exception("Failure !!!");
        }
        return returnCode;
    }


    public void setLog(TaskLogger taskLogger) {
        log.call("setLog");
    }


    public void mockFailure() {
        mustFail = true;
    }


    @SuppressWarnings({"ParameterHidesMemberVariable"})
    public void mockReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }
}
