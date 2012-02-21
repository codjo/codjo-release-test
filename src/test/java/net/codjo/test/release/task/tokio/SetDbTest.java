package net.codjo.test.release.task.tokio;
import net.codjo.test.common.LogString;
import net.codjo.test.release.TestEnvironment;
import net.codjo.test.release.TestEnvironmentMock;
import net.codjo.tokio.JDBCScenario;
import net.codjo.tokio.model.Scenario;
import java.sql.Connection;
import java.sql.SQLException;
import junit.framework.TestCase;
import org.apache.tools.ant.Project;
/**
 *
 */
public class SetDbTest extends TestCase {
    private LogString log = new LogString();
    private Project project = new Project();


    public void test_simple() throws Exception {
        SetDb setDb = new SetDb() {

            @Override
            protected JDBCScenario createJdbcScenario(Scenario tokioStory) {
                log.call("createJdbcScenario");
                return new JDBCScenarioMock(new LogString("jdbcScenario", log));
            }
        };

        setDb.setRefId("load-tag.id");
        setDb.setScenario("story-id");
        setDb.setProject(project);

        project.addReference("load-tag.id", new LoadMock(new LogString("load", log)));
        project.addReference(TestEnvironment.class.getName(), new TestEnvironmentMock(project));

        setDb.execute();

        log.assertContent("load.getScenario(story-id)"
                          + ", createJdbcScenario()"
                          + ", jdbcScenario.insertInputInDb(good connection)");
    }


    private static class LoadMock extends Load {
        private Scenario scenario;
        private final LogString log;


        LoadMock(LogString logString) {
            log = logString;
        }


        @Override
        Scenario getScenario(String name) {
            log.call("getScenario", name);
            return scenario;
        }


        public LoadMock mockGetScenario(Scenario mock) {
            this.scenario = mock;
            return this;
        }
    }
    private static class JDBCScenarioMock extends JDBCScenario {
        private final LogString log;


        private JDBCScenarioMock(LogString log) {
            super(null);
            this.log = log;
        }


        @Override
        public void insertInputInDb(Connection con) throws SQLException {
            log.call("insertInputInDb", (con==null ? "connection is null" : "good connection"));
        }
    }
}
