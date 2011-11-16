package net.codjo.test.release.task.batch;
import net.codjo.test.common.LogString;
import net.codjo.test.common.fixture.DirectoryFixture;
import net.codjo.test.release.TestEnvironment;
import net.codjo.test.release.TestEnvironmentMock;
import net.codjo.test.release.task.util.RemoteCommandMock;
import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
/**
 *
 */
public abstract class BatchTestCase<T extends AbstractBatchTask> {
    protected Project project = new Project();
    protected LogString log = new LogString();
    protected DirectoryFixture directory = DirectoryFixture.newTemporaryDirectoryFixture();
    protected File broadcastOuputDir = new File(directory, "broadcastOutputDir");
    protected T batchTask;


    @Before
    public void setUp() throws Exception {
        directory.doSetUp();
        batchTask = createBatchTask();
        batchTask.setProject(project);
        project.addReference(TestEnvironment.class.getName(), new TestEnvironmentMock(project));

        project.setProperty(BatchExportTask.REMOTE_SERVER, "myServer");
        project.setProperty(BatchExportTask.REMOTE_USER, "myHost");
        project.setProperty(BatchExportTask.REMOTE_PROJECT_DIR, "myProjectDir");
        project.setProperty(BatchExportTask.BATCH_START_CLASS,
                            "net.codjo.test.release.task.batch.BatchMainMock");
        project.setProperty(BatchExportTask.BATCH_CONFIGURATION, "myBatchConfiguration");
        project.setProperty(BatchExportTask.BROADCAST_LOCAL_DIR, broadcastOuputDir.getAbsolutePath());
    }


    @After
    public void tearDown() throws Exception {
        directory.doTearDown();
    }


    @Test
    public void test_returnCode() throws Exception {
        assertEquals(0, batchTask.getReturnCode());
    }


    @Test
    public void test_returnCode_local() {
        BatchMainMock.mockReturnCode(2);

        batchTask.setReturnCode(2);

        batchTask.execute();
    }


    @Test
    public void test_returnCode_local_mustFail() {
        batchTask.setReturnCode(2);

        try {
            batchTask.execute();
            fail();
        }
        catch (BuildException e) {
            assertEquals("Erreur dans l'exécution du Batch, code retour : expected > 2 < actual > 0 <",
                         e.getLocalizedMessage());
        }
    }


    @Test
    public void test_returnCode_remote() {
        project.setProperty("agf.test.remote", "YES");

        RemoteCommandMock remoteCommandMock = new RemoteCommandMock();
        remoteCommandMock.mockReturnCode(2);
        batchTask.setRemoteCommand(remoteCommandMock);
        batchTask.setReturnCode(2);

        batchTask.execute();
    }


    @Test
    public void test_returnCode_remote_mustFail() {
        project.setProperty("agf.test.remote", "YES");

        batchTask.setRemoteCommand(new RemoteCommandMock());
        batchTask.setReturnCode(2);

        try {
            batchTask.execute();
            fail();
        }
        catch (BuildException e) {
            assertEquals("Erreur dans l'exécution du Batch, code retour : expected > 2 < actual > 0 <",
                         e.getLocalizedMessage());
        }
    }


    protected abstract T createBatchTask();
}
