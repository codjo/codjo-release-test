package net.codjo.test.release.task.excel;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

public class ExcelApplicationManagerTest {

    private ExcelApplicationManager excelApplicationManager;

    private static final String EMPTY_LIST = null;


    @Before
    public void setUp() throws Exception {
        excelApplicationManager = new ExcelApplicationManager();
    }


    @After
    public void tearDown() throws Exception {
        excelApplicationManager.quit();
    }


    @Test
    public void test_wrongMatcherName() throws Exception {
        try {
            excelApplicationManager.openWorkBook(getClass().getResource("actual.xls").getPath());
            excelApplicationManager
                  .assertContent(new File(getClass().getResource("expected_ok.xls").toURI()),
                                 EMPTY_LIST,
                                 "bold,italic,toto,margin");
            fail();
        }
        catch (BuildException e) {
            assertEquals("Matcher 'toto' inconnu. Liste des matchers disponibles : \n"
                         + "\t - font-size\n"
                         + "\t - italic\n"
                         + "\t - bold\n"
                         + "\t - margin-size\n"
                         + "\t - alignement\n"
                         + "\t - font-color\n"
                         + "\t - border\n"
                         + "\t - background-color\n"
                         + "\t - merge-region\n"
                  , e.getMessage());
        }
    }


    @Test
    public void test_assertContent() throws Exception {
        excelApplicationManager.openWorkBook(getClass().getResource("actual.xls").getPath());
        excelApplicationManager
              .assertContent(new File(getClass().getResource("expected_ok.xls").toURI()),
                             EMPTY_LIST,
                             EMPTY_LIST);
    }


    @Test
    public void test_assertContentAnStyleOk() throws Exception {
        excelApplicationManager.openWorkBook(getClass().getResource("actual.xls").getPath());
        excelApplicationManager
              .assertContent(new File(getClass().getResource("expected_ok.xls").toURI()),
                             EMPTY_LIST,
                             "border,font-size");
    }


    @Test
    public void test_assertContentOneSheet() throws Exception {
        excelApplicationManager.openWorkBook(getClass().getResource("actual.xls").getPath());
        excelApplicationManager
              .assertContent(new File(getClass().getResource("expected_ok_one_sheet.xls").toURI()),
                             "Feuil1", EMPTY_LIST);
    }


    @Test
    public void test_assertContentOnlyTwoSheets() throws Exception {
        excelApplicationManager.openWorkBook(getClass().getResource("actual.xls").getPath());
        excelApplicationManager
              .assertContent(new File(getClass().getResource("expected_ok_three_sheets.xls").toURI()),
                             "Feuil1,Feuil2", EMPTY_LIST);
    }
}
