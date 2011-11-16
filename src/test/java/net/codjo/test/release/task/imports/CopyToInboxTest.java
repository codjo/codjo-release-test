package net.codjo.test.release.task.imports;
import net.codjo.test.common.LogString;
import net.codjo.test.common.PathUtil;
import net.codjo.test.common.fixture.DirectoryFixture;
import net.codjo.test.release.TestEnvironment;
import net.codjo.test.release.TestEnvironmentMock;
import net.codjo.test.release.task.util.RemoteCommandMock;
import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
/**
 *
 */
public class CopyToInboxTest {
    private CopyToInbox task;
    private Project project;
    private LogString log = new LogString();
    private static final String FILE_NAME = "copyToInboxTest.txt";
    private DirectoryFixture directoryFixture;
    private DirectoryFixture inbox;
    private static final String INBOX_SPECIFIC = "inDirectory";


    @Before
    public void setUp() throws Exception {
        inbox = new DirectoryFixture(new File(PathUtil.findTargetDirectory(getClass()).getPath(),
                                              CopyToInbox.DEFAULT_INBOX).getPath());
        inbox.deleteRecursively();

        directoryFixture = new DirectoryFixture(
              new File(PathUtil.findTargetDirectory(getClass()).getPath(), INBOX_SPECIFIC).getPath());
        directoryFixture.deleteRecursively();

        project = new Project();
        project.addReference(TestEnvironment.class.getName(), new TestEnvironmentMock(project));
        project.setProperty("test.dir", new File(getClass().getResource(FILE_NAME).getFile()).getParent());
        project.setProperty(INBOX_SPECIFIC + ".local", directoryFixture.getAbsolutePath());
        project.setProperty(INBOX_SPECIFIC + ".remote", "/AGFAM/DEV/DAF_WEB1/LIB/DAT1/IN");

        task = new CopyToInbox();
        task.setProject(project);
        task.setRemoteCommand(new FtpCommandMock(log));
        task.setFile(FILE_NAME);
    }


    @After
    public void tearDown() throws Exception {
        directoryFixture.doTearDown();
        inbox.doTearDown();
    }


    @Test
    public void test_ftp() {
        project.setProperty("agf.test.remote", "YES");
        project.setProperty("ftp.remoteDir", "//BALBALBAL//IN");

        task.execute();

        log.assertContent("FtpCommandMock.execute(//BALBALBAL//IN)");
    }


    @Test
    public void test_remoteSpecificDirectory() {
        project.setProperty("agf.test.remote", "YES");
        project.setProperty("ftp.remoteDir", "//BALBALBAL//IN");

        task.setIn(INBOX_SPECIFIC);

        task.execute();

        log.assertContent("FtpCommandMock.execute(/AGFAM/DEV/DAF_WEB1/LIB/DAT1/IN)");
    }


    @Test
    public void test_defaultInDirectory() {
        task.execute();

        assertTrue(inbox.exists());
        assertTrue(new File(inbox.getPath(), FILE_NAME).exists());
        log.assertContent("");
    }


    @Test
    public void test_copyToSpecificDirectory() {
        task.setIn(INBOX_SPECIFIC);

        task.execute();

        assertTrue(new File(directoryFixture.getPath(), FILE_NAME).exists());
        assertFalse(inbox.exists());
        log.assertContent("");
    }


    @Test
    public void test_missingKeyProperty() {
        String undefinedKey = "undefineKey";
        task.setIn(undefinedKey);

        try {
            task.execute();
            fail();
        }
        catch (BuildException exception) {
            assertEquals("manque argument " + undefinedKey + ".local" + " dans le fichier de configuration",
                         exception.getLocalizedMessage());
        }
        assertFalse(inbox.exists());

        project.setProperty("agf.test.remote", "YES");
        try {
            task.execute();
            fail();
        }
        catch (BuildException exception) {
            assertEquals("manque argument " + undefinedKey + ".remote" + " dans le fichier de configuration",
                         exception.getLocalizedMessage());
        }
        log.assertContent("");
    }


    @Test
    public void test_remoteExecute_ko() throws Exception {
        RemoteCommandMock remoteCommandMock = new RemoteCommandMock();
        remoteCommandMock.mockFailure();
        task.setRemoteCommand(remoteCommandMock);

        project.setProperty("agf.test.remote", "YES");
        try {
            task.execute();
            fail();
        }
        catch (Exception e) {
            assertEquals("Impossible de copier le fichier", e.getMessage());
        }
    }


    class FtpCommandMock extends RemoteCommandMock {
        private LogString log;


        FtpCommandMock(LogString log) {
            super(log);
            this.log = log;
        }


        @Override
        public int execute() throws Exception {
            log.call("FtpCommandMock.execute", task.determineRemoteInbox());
            return 0;
        }
    }
}
