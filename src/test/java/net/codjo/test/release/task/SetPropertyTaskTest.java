package net.codjo.test.release.task;
import net.codjo.test.release.TestEnvironment;
import net.codjo.test.release.TestEnvironmentMock;
import org.apache.tools.ant.Project;
import static org.hamcrest.core.IsEqual.equalTo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SetPropertyTaskTest {

    private static final String PROPERTY_NAME = "operatingSystem";
    private static final String REMOTE_VALUE = "Linux";
    private static final String LOCAL_VALUE = "Windows";

    private Project project;


    @Before
    public void setUp() {

        project = new Project();

        project.addReference(TestEnvironment.class.getName(), new TestEnvironmentMock(project));
    }


    @Test
    public void shouldHaveLocalValue() throws Exception {
        SetPropertyTask setPropertyTask = createSetPropertyTask(PROPERTY_NAME, LOCAL_VALUE, REMOTE_VALUE);
        setPropertyTask.execute();

        Assert.assertThat(project.getProperty(PROPERTY_NAME), equalTo(LOCAL_VALUE));
    }


    @Test
    public void shouldHaveRemoteValue() throws Exception {
        project.setProperty("agf.test.remote", "YES");
        SetPropertyTask setPropertyTask = createSetPropertyTask(PROPERTY_NAME, LOCAL_VALUE, REMOTE_VALUE);
        setPropertyTask.execute();

        Assert.assertThat(project.getProperty(PROPERTY_NAME), equalTo(REMOTE_VALUE));
    }


    @Test
    public void shouldUseLocalValueAndReplaceProperty() throws Exception {
        project.setProperty("prenom", "John");

        SetPropertyTask setPropertyTask = createSetPropertyTask("nomComplet", "Mr ${prenom} Smith", "Remote");

        setPropertyTask.execute();

        Assert.assertThat(project.getProperty("nomComplet"), equalTo("Mr John Smith"));
    }


    @Test
    public void shouldUseRemoteValueAndReplaceProperty() throws Exception {
        project.setProperty("agf.test.remote", "YES");
        project.setProperty("prenom", "John");

        SetPropertyTask setPropertyTask = createSetPropertyTask("nomComplet", "Local", "Mr ${prenom} Smith");

        setPropertyTask.execute();

        Assert.assertThat(project.getProperty("nomComplet"), equalTo("Mr John Smith"));
    }


    private SetPropertyTask createSetPropertyTask(String propertyName,
                                                  String localValue,
                                                  String remoteValue) {
        SetPropertyTask setPropertyTask = new SetPropertyTask();
        setPropertyTask.setName(propertyName);
        setPropertyTask.setLocalValue(localValue);
        setPropertyTask.setRemoteValue(remoteValue);

        setPropertyTask.setProject(project);

        return setPropertyTask;
    }
}
