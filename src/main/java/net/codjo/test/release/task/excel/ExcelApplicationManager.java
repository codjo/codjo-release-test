package net.codjo.test.release.task.excel;
import net.codjo.test.common.excel.ExcelComparator;
import net.codjo.test.common.excel.ExcelMatchingException;
import net.codjo.test.common.excel.ExcelUtil;
import net.codjo.test.release.task.util.AbstractRepeatableTask;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import jp.ne.so_net.ga2.no_ji.jcom.JComException;
import jp.ne.so_net.ga2.no_ji.jcom.ReleaseManager;
import jp.ne.so_net.ga2.no_ji.jcom.excel8.ExcelApplication;
import jp.ne.so_net.ga2.no_ji.jcom.excel8.ExcelWorkbook;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.tools.ant.BuildException;

public class ExcelApplicationManager {
    private ExcelApplication excelApplication = null;
    private static final Logger LOG = Logger.getLogger(ExcelApplicationManager.class);
    private static final String EXPORT_XLS = "exportExcel_" + System.currentTimeMillis() + ".xls";
    private File tmpDir = new File(System.getProperty("java.io.tmpdir"), "excel");


    public ExcelApplicationManager() {
        try {
            tmpDir.mkdir();
            if (!tmpDir.exists()) {
                throw new IllegalStateException("Impossible de cr\u0233er le r\u0233pertoire '" + tmpDir + "'");
            }

            excelApplication = new ExcelApplication(new ReleaseManager());
            excelApplication.Visible(true);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        catch (UnsatisfiedLinkError e) {
            throw new RuntimeException(e);
        }
    }


    public boolean isStarted() {
        return excelApplication != null;
    }


    public ExcelWorkbook openWorkBook(String filename) throws JComException, IOException {
        return excelApplication.Workbooks().Open(new File(filename).getCanonicalPath());
    }


    public void closeAllFiles() throws JComException {
        if (excelApplication.Workbooks() != null) {
            excelApplication.Workbooks().Close();
        }
    }


    public void quit() throws JComException {
        try {
            closeAllFiles();
            Thread.sleep(250);
            excelApplication.Quit();
            Thread.sleep(250);
            excelApplication.getReleaseManager().release();
            Thread.sleep(1000);
            excelApplication = null;
        }
        catch (InterruptedException e) {
            ;
        }
    }


    public void assertContent(File expectedFile, String sheets, String matchers)
          throws Exception {
        final String actualFile = tmpDir.getAbsolutePath() + System.getProperty("file.separator")
                                  + EXPORT_XLS;
        LOG.debug("Saving to " + actualFile);
        final File file = new File(actualFile);

        try {
            AbstractRepeatableTask repeatableTask = new AbstractRepeatableTask() {
                @Override
                protected boolean internalExecute() throws Exception {
                    try {
                        return (excelApplication.Workbooks().Count() > 0);
                    }
                    catch (JComException jce) {
                        ExcelApplicationManager.LOG.warn("Impossible to count Excel Workbooks", jce);
                        return false;
                    }
                }
            };
            repeatableTask.execute();

            int nbWorkbooks = excelApplication.Workbooks().Count();
            Validate
                  .isTrue(nbWorkbooks == 1,
                          "Excel ne contient pas un workbook unique (nb = " + nbWorkbooks + ")");

            ExcelWorkbook openedWorkbook = excelApplication.Workbooks().Item(1);

            if (file.exists()) {
                file.delete();
            }
            openedWorkbook.SaveAs(actualFile);
            openedWorkbook.Close(false, null, false);

            HSSFWorkbook expectedWorkbook = ExcelUtil.loadWorkbook(expectedFile);
            HSSFWorkbook actualWorkbook = ExcelUtil.loadWorkbook(new File(actualFile));

            ExcelComparator.execute(expectedWorkbook,
                                    actualWorkbook,
                                    buildList(sheets),
                                    ExcelComparator.buildSheetMatcherList(buildList(matchers)));
        } catch (ExcelMatchingException ex) {
            LOG.info(ex.getMessage());
            throw new BuildException(ex.getMessage());
        }
        finally {
            if (file.exists()) {
                file.delete();
            }
        }
    }


    private static List<String> buildList(String sheets) {
        List<String> sheetNamesToCompare = Collections.emptyList();
        if (sheets != null) {
            sheetNamesToCompare = Arrays.asList(sheets.split(","));
        }
        return sheetNamesToCompare;
    }
}
