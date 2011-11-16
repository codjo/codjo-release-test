package net.codjo.test.release.task.gui;
import junit.extensions.jfcunit.JFCTestCase;
import junit.framework.TestCase;

/**
 *
 */
public class StepListTest extends TestCase {
    private StepList stepList;


    @Override
    protected void setUp() throws Exception {
        stepList = new GroupStep();
    }


    public void test_proceed() throws Exception {
        MockStep step1 = new MockStep();
        MockStep step2 = new MockStep();

        stepList.setName("test");
        stepList.addStep(step1);
        stepList.addStep(step2);

        TestContext context = new TestContext(new JFCTestCase(""));
        stepList.proceed(context);

        assertEquals("Step 2 du groupe 'test' (StepListTest$MockStep)",
                     context.getTestLocation().getLocationMessage());

        assertEquals(1, step1.callCount);
        assertEquals(1, step2.callCount);
        assertTrue(step1.callTime < step2.callTime);
    }


    public static class MockStep implements GuiStep {
        private int callCount = 0;
        private long callTime = 0;


        public void proceed(TestContext context) {
            callCount++;
            littleSleep();
            callTime = System.currentTimeMillis();
        }


        private void littleSleep() {
            long time = System.currentTimeMillis();
            while (System.currentTimeMillis() == time) {
                // Attendre la prochaine milliseconde
            }
        }
    }
}
