package net.codjo.test.release.task.tokio;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import net.codjo.util.file.FileUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TokioInsertListenerTest extends AbstractTokioListenerTest {

    @Override
    @Before
    public void before() throws IOException {
        super.before();
    }


    @Override
    @After
    public void after() {
        super.after();
    }


    @Test
    public void test_badTask() throws Exception {
        checkTestFile();
        TokioInsertListener tokioInsertListener = new TokioInsertListener(testReport);
        tokioInsertListener.taskStarted(createTaskBuildEvent(new TestTask()));
        tokioInsertListener.taskFinished(createTaskBuildEvent(new TestTask()));
        List<String> elements = processLogContent(FileUtil.loadContent(fileTest).trim());
        assertThat(elements.size(), equalTo(10));
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
    }


    @Test
    public void test_goodTask() throws Exception {
        checkTestFile();
        TokioInsertListener tokioInsertListener = new TokioInsertListener(testReport);
        activateListener(tokioInsertListener, new SetDb(), 500);
        testReport.flush();
        List<String> elements = processLogContent(FileUtil.loadContent(fileTest));
        assertThat(elements.size(), equalTo(20));
        assertThat(equalsWithErrorPrecision(new BigDecimal(elements.get(19)), new BigDecimal(500)),
                   equalTo(true));
    }


    @Test
    public void test_mulitTask() throws Exception {
        checkTestFile();
        TokioInsertListener tokioInsertListener = new TokioInsertListener(testReport);
        activateListener(tokioInsertListener, new SetDb(), 500);
        activateListener(tokioInsertListener, new SetDb(), 500);
        testReport.flush();
        List<String> elements = processLogContent(FileUtil.loadContent(fileTest));
        assertThat(elements.size(), equalTo(20));
        assertThat(equalsWithErrorPrecision(new BigDecimal(elements.get(19)), new BigDecimal(1000)),
                   equalTo(true));
    }
}
