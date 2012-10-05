package net.codjo.test.release;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import static net.codjo.test.release.ReleaseTest.RELEASE_TEST_EXTENSION;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import junit.framework.TestResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SuiteBuilderTest {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
    private File logFile;
    private String testReleaseDirectoryPath = "./target/fred";


    public SuiteBuilderTest() {
        logFile = new File("./target/testreleaseSuite.log.xls");
    }


    private void createTestRelease(String testReleaseName) throws IOException {
        File testReleaseFile = new File(
              testReleaseDirectoryPath + "/" + testReleaseName + RELEASE_TEST_EXTENSION);
        String content = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
                         + "<release-test name=\"FredTestRelease\">"
                         + "    <echo>Ceci est un test</echo>"
                         + "</release-test>";
        FileUtil.saveContent(testReleaseFile, content);
    }


    static List<String> processLogContent(String logContent) {
        List<String> objects = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(logContent, TestReport.TAB, false);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (!(LINE_SEPARATOR.equals(token))) {
                objects.add(token.replaceAll(LINE_SEPARATOR, ""));
            }
        }
        return objects;
    }


    @Before
    public void before() {
        new File(testReleaseDirectoryPath).mkdirs();
    }


    @After
    public void after() {
        FileUtil.deleteRecursively(new File(testReleaseDirectoryPath));
        FileUtil.deleteRecursively(logFile);
    }


    @Test
    public void test_processLogContent() throws Exception {
        String content = "name1\tname2\t" + LINE_SEPARATOR + "val1\tval2\t" + LINE_SEPARATOR;
        List<String> elements = processLogContent(content);
        assertThat(elements.size(), equalTo(4));
        assertThat(elements.get(0), equalTo("name1"));
        assertThat(elements.get(1), equalTo("name2"));
        assertThat(elements.get(2), equalTo("val1"));
        assertThat(elements.get(3), equalTo("val2"));
    }


    @Test
    public void test_logContent() throws Exception {
        SuiteBuilder suiteBuilder = new SuiteBuilder();
        createTestRelease("fredTestRelease");
        File testDirectory = new File(testReleaseDirectoryPath);
        junit.framework.Test testSuite = suiteBuilder.createSuite(new File("."), testDirectory);
        TestResult result = new TestResult();
        testSuite.run(result);
        assertThat(logFile.exists(), equalTo(true));
        assertThat(result.errorCount(), equalTo(0));
        List<String> logElements = processLogContent(FileUtil.loadContent(logFile));
        assertThat(logElements.size(), equalTo(20));
        assertThat(logElements.get(0), equalTo("Test"));
        assertThat(logElements.get(1), equalTo("Total before"));
        assertThat(logElements.get(2), equalTo("Used before"));
        assertThat(logElements.get(3), equalTo("Free before"));
        assertThat(logElements.get(4), equalTo("Total after"));
        assertThat(logElements.get(5), equalTo("Used after"));
        assertThat(logElements.get(6), equalTo("Free after"));
        assertThat(logElements.get(7), equalTo("Time"));
        assertThat(logElements.get(8), equalTo("Tokio load time"));
        assertThat(logElements.get(9), equalTo("Tokio insert time"));
        assertThat(logElements.get(10), equalTo("fred__fredTestRelease(net.codjo.test.release.ReleaseTest)"));
    }
}
