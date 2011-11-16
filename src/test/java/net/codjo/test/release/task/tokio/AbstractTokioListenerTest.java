package net.codjo.test.release.task.tokio;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import net.codjo.test.release.TestReport;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.Task;

public abstract class AbstractTokioListenerTest {
    private static final BigDecimal PRECISION_EQUAL = new BigDecimal(0.1);
    protected File fileTest;
    protected TestReport testReport;


    protected static List<String> processLogContent(String logContent) {
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


    public void before() throws IOException {
        fileTest = new File(AbstractTokioListenerTest.class.getResource(".").getPath() + "/fileTest.txt");
        fileTest.createNewFile();
        testReport = new TestReport(fileTest.getAbsolutePath());
    }


    public void after() {
        FileUtil.deleteRecursively(fileTest);
    }


    protected void checkTestFile() {
        assertThat(fileTest.exists(), equalTo(true));
        assertThat(fileTest.isFile(), equalTo(true));
    }


    protected static BuildEvent createTaskBuildEvent(final Task task) {
        return new BuildEvent(task);
    }


    protected void activateListener(AbstractTokioListener tokioLoadListener,
                                    final Task task,
                                    final int duration)
          throws InterruptedException {
        tokioLoadListener.taskStarted(createTaskBuildEvent(task));
        Thread.sleep(duration);
        tokioLoadListener.taskFinished(createTaskBuildEvent(task));
    }


    protected static boolean equalsWithErrorPrecision(final BigDecimal bigDecimal1,
                                                      final BigDecimal bigDecimal2) {
        BigDecimal diff = (bigDecimal1.add(bigDecimal2.negate())).abs();
        return (diff.divide(bigDecimal2)).compareTo(PRECISION_EQUAL) <= 0;
    }


    protected static class TestTask extends Task {
        // EMPTY
    }
}
