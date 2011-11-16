package net.codjo.test.release;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import static net.codjo.test.release.TestReport.ONE_MEGA;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestReportTest {
    private File fileTest;


    @Before
    public void before() throws IOException {
        fileTest = new File(TestReportTest.class.getResource(".").getPath() + "/testFile.txt");
        fileTest.createNewFile();
    }


    @After
    public void after() {
        FileUtil.deleteRecursively(fileTest);
    }


    @SuppressWarnings({"UnusedDeclaration"})
    @Test
    public void test_initHeaders() throws Exception {
        assertThat(fileTest.isFile(), equalTo(true));
        assertThat(fileTest.exists(), equalTo(true));
        TestReport report = new TestReport(fileTest.getAbsolutePath(), Arrays.asList("HEADER1", "HEADER2"));
        assertThat(FileUtil.loadContent(fileTest), equalTo("HEADER1\tHEADER2\t\r\n"));
    }


    @Test(expected = IllegalArgumentException.class)
    public void test_setValue_badField() throws Exception {
        TestReport report = new TestReport(fileTest.getAbsolutePath(), Arrays.asList("COLUMN1", "COLUMN2"));
        report.setFieldValue("TOTO", "maValeur");
    }


    @Test
    public void test_logContent() throws Exception {
        TestReport testReport = new TestReport(fileTest.getAbsolutePath());
        testReport.logTestName("fredTest");
        testReport.logMemoryBeforeTest((double)3 * (ONE_MEGA), (double)5 * (ONE_MEGA));
        testReport.logMemoryAfterTest((double)2 * (ONE_MEGA), (double)5 * (ONE_MEGA));
        testReport.logTime(5000);
        testReport.setFieldValue("Tokio load time", (long)3000);
        testReport.flush();
        List<String> elements = processLogContent(FileUtil.loadContent(fileTest));

        assertThat(elements.size(), equalTo(20));
        assertThat(elements.get(0), equalTo("Test"));
        assertThat(elements.get(1), equalTo("Total before"));
        assertThat(elements.get(2), equalTo("Used before"));
        assertThat(elements.get(3), equalTo("Free before"));
        assertThat(elements.get(4), equalTo("Total after"));
        assertThat(elements.get(5), equalTo("Used after"));
        assertThat(elements.get(6), equalTo("Free after"));
        assertThat(elements.get(7), equalTo("Time"));
        assertThat(elements.get(8), equalTo("Tokio load time"));
        assertThat(elements.get(9), equalTo("Tokio insert time"));
        assertThat(elements.get(10), equalTo("fredTest"));
        assertThat(elements.get(11), equalTo("5.0"));
        assertThat(elements.get(12), equalTo("2.0"));
        assertThat(elements.get(13), equalTo("3.0"));
        assertThat(elements.get(14), equalTo("5.0"));
        assertThat(elements.get(15), equalTo("3.0"));
        assertThat(elements.get(16), equalTo("2.0"));
        assertThat(elements.get(17), equalTo("5000"));
        assertThat(elements.get(18), equalTo("3000"));
    }


    // TODO A refactorer ??
    static List<String> processLogContent(String logContent) {
        final String retour = "\r\n";
        List<String> objects = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(logContent, TestReport.TAB, false);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (!(retour.equals(token))) {
                objects.add(token.replaceAll(retour, ""));
            }
        }
        return objects;
    }
}
