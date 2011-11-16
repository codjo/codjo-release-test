package net.codjo.test.release.task.batch;
import net.codjo.test.release.ReleaseTestRunner;
import net.codjo.test.release.task.util.RemoteCommandMock;
import java.io.File;
import org.apache.tools.ant.BuildException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
/**
 *
 */
public class BatchImportTaskTest extends BatchTestCase<BatchImportTask> {

    @Override
    protected BatchImportTask createBatchTask() {
        BatchImportTask importTask = new BatchImportTask();
        importTask.setFile("myFileToImport");
        importTask.setInitiator("myUser");

        return importTask;
    }


    @Test
    public void test_antGeneration() throws Exception {
        File releaseTestFile = new File(getClass().getResource("BatchImportTask.xml").getFile());

        try {
            ReleaseTestRunner.executeTestFile(releaseTestFile.getParentFile(), releaseTestFile);
            fail();
        }
        catch (BuildException e) {
            assertEquals("manque argument batch.configuration dans le fichier de configuration",
                         e.getMessage());
        }
    }


    @Test
    public void test_getRemoteCommand() throws Exception {
        project.setProperty("variable2", "valeur2");

        assertTrue(batchTask.getRemoteCommand() instanceof BatchImportSecureCommand);
        assertEquals("myProjectDir/APP1/BATCH",
                     ((BatchImportSecureCommand)batchTask.getRemoteCommand()).getWorkingDir());
        assertEquals(
              "./import.ksh myUser myFileToImport",
              ((BatchImportSecureCommand)batchTask.getRemoteCommand()).getCommand());
    }


    @Test
    public void test_executeLocal() throws Exception {
        batchTask.execute();

        BatchMainMock.assertLog(
              "main("
              + "-initiator, myUser, "
              + "-type, import, "
              + "-argument, "
              + new File(project.getBaseDir(), "target\\inbox\\myFileToImport") + ", "
              + "-configuration, myBatchConfiguration)");
    }


    @Test
    public void test_executeRemote() throws Exception {
        project.setProperty("agf.test.remote", "YES");
        batchTask.setRemoteCommand(new RemoteCommandMock(log));

        batchTask.execute();

        log.assertContent("execute()");
    }


    @Test
    public void test_executeRemote_mustFail() throws Exception {
        project.setProperty("agf.test.remote", "YES");
        RemoteCommandMock commandMock = new RemoteCommandMock(log);
        commandMock.mockFailure();
        batchTask.setRemoteCommand(commandMock);

        try {
            batchTask.execute();
        }
        catch (Exception e) {
            assertEquals("batch-import initiator >myUser< file >myFileToImport<... en erreur",
                         e.getMessage());
        }
    }
}
