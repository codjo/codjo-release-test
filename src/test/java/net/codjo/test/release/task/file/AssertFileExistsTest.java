package net.codjo.test.release.task.file;
import net.codjo.test.common.PathUtil;
import net.codjo.test.release.TestEnvironment;
import net.codjo.test.release.TestEnvironmentMock;
import static net.codjo.test.release.task.AgfTask.BROADCAST_LOCAL_DIR;
import static net.codjo.test.release.task.AgfTask.TEST_DIRECTORY;
import net.codjo.test.release.task.gui.GuiAssertException;
import junit.framework.TestCase;
import org.apache.tools.ant.Project;
public class AssertFileExistsTest extends TestCase {

    private AssertFileExists fileAssert;


    @Override
    protected void setUp() throws Exception {
        Project project = new Project();
        project.setProperty(TEST_DIRECTORY, PathUtil.findResourcesFileDirectory(getClass()).getPath());
        project.setProperty(BROADCAST_LOCAL_DIR, PathUtil.findResourcesFileDirectory(getClass()).getPath());
        project.addReference(TestEnvironment.class.getName(), new TestEnvironmentMock(project));

        fileAssert = new AssertFileExists();
        fileAssert.setProject(project);
    }


    public void test_onExpectedExistentFile() {
        fileAssert.setFilename("FileAssertTest_expected.pdf");
        fileAssert.setExpected(true);
        fileAssert.execute();
    }


    public void test_onExpectedNonexistentFile() {
        fileAssert.setFilename("missing.pdf");
        fileAssert.setExpected(false);
        fileAssert.execute();
    }


    public void test_onUnexpectedExistentFile() {
        fileAssert.setFilename("FileAssertTest_expected.pdf");
        fileAssert.setExpected(false);
        try {
            fileAssert.execute();
            fail();
        }
        catch (GuiAssertException e) {
            assertEquals("Le fichier \'FileAssertTest_expected.pdf\' ne devrait pas exister", e.getMessage());
        }
    }


    public void test_onUnexpectedNonexistentFile() {
        fileAssert.setFilename("missing.pdf");
        fileAssert.setExpected(true);
        try {
            fileAssert.execute();
            fail();
        }
        catch (GuiAssertException e) {
            assertEquals("Le fichier \'missing.pdf\' est introuvable", e.getMessage());
        }
    }
}
