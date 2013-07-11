package net.codjo.test.release.task.web;
import java.io.IOException;
import net.codjo.test.common.LogString;
import org.junit.Test;
/**
 *
 */
public class StepListTest extends WebStepTestCase {
    private StepList stepList;
    private LogString logString = new LogString();


    protected void setUp() throws Exception {
        stepList = new StepList();
        logString.clear();
    }


    @Test
    public void test_proceed() throws Exception {
        MockStep step1 = new MockStep("step1", logString);
        MockStep step2 = new MockStep("step2", logString);

        stepList.setName("test");
        stepList.addStep(step1);
        stepList.addStep(step2);
        assertEquals(stepList.getStepList().size(),2);

        stepList.proceed(null);

        logString.assertContent("proceed(step1), proceed(step2)");
    }


    public static class MockStep implements WebStep {
        private LogString logString;
        private String name;


        public MockStep(String name, LogString logString) {
            this.name = name;
            this.logString = logString;
        }


        public void proceed(WebContext context) throws IOException, WebException {
            logString.call("proceed", name);
        }
    }
}
