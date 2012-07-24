/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.tokio;
import net.codjo.tokio.JDBCScenario;
import net.codjo.tokio.model.Scenario;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import junit.framework.TestCase;
import org.apache.tools.ant.Project;
/**
 * 
 */
public class AssertTest extends TestCase {
    private Assert assertion;
    private JDBCScenario jdbcScenario;
    private Connection con;

    @Override
    protected void setUp() throws Exception {
        Class.forName("fakedb.FakeDriver");
        con = DriverManager.getConnection("jdbc:fakeDriver");
        assertion = new AssertMock();
        jdbcScenario = assertion.getJdbcsc();
    }


    public void test_executeTableAssertFailed() throws Exception {
        assertion.setTable("FIRST_TABLE");
        assertion.setProject(new Project());
        jdbcScenario.getScenario().getOutputDataSet().buildTable("FIRST_TABLE");
        jdbcScenario.getScenario().getOutputDataSet().buildTable("SECOND_TABLE");
        try {
            assertion.execute();
            fail("execution en erreur");
        }
        catch (IllegalArgumentException e) {
            assertEquals("Erreur de comparaison sur la table FIRST_TABLE avec le tri par défaut\n",
                e.getMessage());
        }
    }


    public void test_executeOrderedTableAssertFailed()
            throws Exception {
        assertion.setTable("FIRST_TABLE");
        assertion.setOrder("COLUMN1");
        assertion.setNullFirst(false);
        assertion.setProject(new Project());
        jdbcScenario.getScenario().getOutputDataSet().buildTable("FIRST_TABLE");
        jdbcScenario.getScenario().getOutputDataSet().buildTable("SECOND_TABLE");
        try {
            assertion.execute();
            fail("execution en erreur");
        }
        catch (IllegalArgumentException e) {
            assertEquals("Erreur de comparaison sur la table FIRST_TABLE avec le tri : COLUMN1\n",
                e.getMessage());
        }
    }


    public void test_executeAllTablesAssertFailed()
            throws Exception {
        assertion.setAllTables(true);
        assertion.setProject(new Project());
        jdbcScenario.getScenario().getOutputDataSet().buildTable("FIRST_TABLE");
        jdbcScenario.getScenario().getOutputDataSet().buildTable("SECOND_TABLE");
        try {
            assertion.execute();
            fail("execution en erreur");
        }
        catch (IllegalArgumentException e) {
            assertEquals("Erreur de comparaison sur la table FIRST_TABLE\n", e.getMessage());
        }
    }

    private class AssertMock extends Assert {
        private JDBCScenario jdbcScenarioMock;

        @Override
        JDBCScenario getJdbcsc() {
            if (jdbcScenarioMock == null) {
                Scenario scenario = new Scenario("monScenario", "mon commentaire");
                jdbcScenarioMock = new JDBCScenarioMock(scenario);
            }
            return jdbcScenarioMock;
        }


        @Override
        protected Connection getConnection() throws SQLException {
            return con;
        }
    }


    private class JDBCScenarioMock extends JDBCScenario {
        private boolean returnTrue = false;

        JDBCScenarioMock(Scenario sc) {
            super(sc);
        }

        @Override
        public boolean verifyOutputs(Connection con, String tableName)
                throws SQLException {
            return returnTrue;
        }


        @Override
        public boolean verifyOutputs(Connection con, String tableName, String orderClause, boolean nullFirst)
                throws SQLException {
            return returnTrue;
        }


        @Override
        public String getLastVerifyOutputsReport() {
            return "";
        }


        void setReturnTrue(boolean isTrue) {
            returnTrue = isTrue;
        }
    }
}
