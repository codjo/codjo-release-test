package net.codjo.test.release.task.util;
import org.apache.tools.ant.util.TaskLogger;
/**
 *
 */
public interface RemoteCommand {
    int execute() throws Exception;


    void setLog(TaskLogger log);
}
