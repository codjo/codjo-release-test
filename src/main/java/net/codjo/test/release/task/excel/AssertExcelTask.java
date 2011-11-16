package net.codjo.test.release.task.excel;

import net.codjo.test.release.task.AgfTask;
import net.codjo.test.release.task.Resource;
import jp.ne.so_net.ga2.no_ji.jcom.JComException;
import org.apache.tools.ant.BuildException;

public class AssertExcelTask extends AgfTask implements Resource {
    private String expected;
    private String sheets;
    private String matchers;
    private ExcelApplicationManager excelApplicationManager;

    @Override
    public void execute() {
        try {
            excelApplicationManager.assertContent(toAbsoluteFile(expected), sheets, matchers);
            info("....AssertExcel OK <");
        }
        catch (BuildException comparaisonException) {
            info("Comparaison en erreur:");
            throw comparaisonException;
        }
        catch (Exception e) {
            error(e);
            throw new BuildException(e);
        }
    }


    public void setExpected(String expected) {
        this.expected = expected;
    }


    public void setSheets(String sheets) {
        this.sheets = sheets;
    }


    public void setMatchers(String matchers) {
        this.matchers = matchers;
    }


    public void open() {
        excelApplicationManager = new ExcelApplicationManager();
    }


    public void close() {
        if (excelApplicationManager.isStarted()) {
            try {
                excelApplicationManager.quit();
            }
            catch (JComException e) {
                throw new RuntimeException("Impossible de fermer Excel.", e);
            }
        }
    }
}
