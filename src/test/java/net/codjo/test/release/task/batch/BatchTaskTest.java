package net.codjo.test.release.task.batch;
import net.codjo.test.release.task.util.RemoteCommandMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
/**
 *
 */
public class BatchTaskTest extends BatchTestCase<BatchTask> {

    @Override
    protected BatchTask createBatchTask() {
        BatchTask task = new BatchTask();
        task.getLocal().setMain(BatchMainMock.class.getName());
        task.getLocal().setArg("-arg1 value1 -arg2 value2 -arg3 ${arg3}");
        return task;
    }


    @Test
    public void test_getRemoteCommandl() throws Exception {
        project.setProperty("agf.test.remote", "YES");
        project.setProperty("arg3", "value3");

        batchTask.getRemote().setScript("my-script.ksh");
        batchTask.getRemote().setArg("-arg1 value1 -arg2 value2 -arg3 ${arg3}");

        assertTrue(batchTask.getRemoteCommand() instanceof BatchSecureCommand);
        assertEquals("myProjectDir/APP1/BATCH",
                     ((BatchSecureCommand)batchTask.getRemoteCommand()).getWorkingDir());
        assertEquals("./my-script.ksh -arg1 value1 -arg2 value2 -arg3 value3",
                     ((BatchSecureCommand)batchTask.getRemoteCommand()).getCommand());
    }


    @Test
    public void test_executeLocal() throws Exception {
        project.setProperty("arg3", "value3");

        batchTask.execute();

        BatchMainMock.assertLog(
              "main("
              + "-arg1, value1, "
              + "-arg2, value2, "
              + "-arg3, value3)");
    }


    @Test
    public void test_executeLocalBecauseRemoteParameterFalse() throws Exception {
        project.setProperty("agf.test.remote", "YES");
        project.setProperty("arg3", "value3");

        batchTask.setRemote(false);
        batchTask.execute();

        BatchMainMock.assertLog(
              "main("
              + "-arg1, value1, "
              + "-arg2, value2, "
              + "-arg3, value3)");
    }


    @Test
    public void test_executeRemote() throws Exception {
        project.setProperty("agf.test.remote", "YES");
        project.setProperty("arg3", "value.");

        batchTask.getRemote().setScript("my-script.ksh");
        batchTask.getRemote().setArg("-arg1 value1 -arg2 value2 -arg3 ${arg3}");
        batchTask.setRemoteCommand(new RemoteCommandMock(log));

        batchTask.execute();

        log.assertContent("execute()");
    }


    @Test
    public void test_executeRemote_mustFail() throws Exception {
        project.setProperty("agf.test.remote", "YES");

        RemoteCommandMock commandMock = new RemoteCommandMock(log);
        commandMock.mockFailure();
        batchTask.getRemote().setScript("my-script.ksh");
        batchTask.getRemote().setArg("-arg1 value1 -arg2 value2");
        batchTask.setRemoteCommand(commandMock);

        try {
            batchTask.execute();
        }
        catch (Exception e) {
            assertEquals("batch ... en erreur", e.getMessage());
        }
    }
}
