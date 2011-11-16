package net.codjo.test.release.test.standard;
import net.codjo.test.common.PathUtil;
import static net.codjo.test.release.task.AgfTask.TEST_DIRECTORY;
import net.codjo.test.release.test.AbstractSampleGuiTestCase;
import org.apache.tools.ant.Project;

public class GroupTest extends AbstractSampleGuiTestCase {
    private Project project;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        project = new Project();
        project.setProperty(TEST_DIRECTORY, PathUtil.findResourcesFileDirectory(getClass()).getPath());
    }


    public void test_groupDisabled() throws Exception {
        runScenario("Group.xml", project);
    }
}
