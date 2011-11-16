/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.tokio;
import net.codjo.test.release.task.AgfTask;
import net.codjo.tokio.JDBCScenario;
import net.codjo.tokio.model.Scenario;
import java.sql.SQLException;
import org.apache.tools.ant.BuildException;
/**
 */
public class SetDb extends AgfTask {
    private String refId;
    private String scenario;
    private String caseId;
    private boolean deleteBeforeInsert = true;
    private JDBCScenario jdbcsc;


    public void setJdbcsc(JDBCScenario jdbcsc) {
        this.jdbcsc = jdbcsc;
    }


    public JDBCScenario getJdbcsc() {
        return jdbcsc;
    }


    public void setRefId(String refId) {
        this.refId = refId;
    }


    public String getRefId() {
        return refId;
    }


    public void setScenario(String scenario) {
        this.scenario = scenario;
    }


    public String getScenario() {
        return scenario;
    }


    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }


    public String getCaseId() {
        return caseId;
    }


    public void setDeleteBeforeInsert(boolean deleteBeforeInsert) {
        this.deleteBeforeInsert = deleteBeforeInsert;
    }


    public boolean isDeleteBeforeInsert() {
        return deleteBeforeInsert;
    }


    @Override
    public void execute() {
        try {
            if (getScenario() != null) {
                info("Chargement du scenario " + getScenario());
            }
            else {
                info("Chargement du case " + getCaseId());
            }
            Scenario tokioStory = loadScenario();
            jdbcsc = createJdbcScenario(tokioStory);
            jdbcsc.setDeleteBeforeInsert(isDeleteBeforeInsert());
            jdbcsc.insertInputInDb(getConnection());
        }
        catch (SQLException ex) {
            throw new BuildException(ex);
        }
    }


    protected JDBCScenario createJdbcScenario(Scenario tokioStory) {
        return new JDBCScenario(tokioStory);
    }


    private Scenario loadScenario() {
        Load load = (Load)getReference(getRefId());
        String name = (getCaseId() != null ? getCaseId() : getScenario());
        return load.getScenario(name);
    }
}
