package net.codjo.test.release.task.util;
import junit.framework.Assert;
import org.junit.Test;
/**
 *
 */
public class AbstractRepeatableTaskTest {
    private AbstractRepeatableTask repeatableTask;
    private String test = "A";


    public AbstractRepeatableTaskTest() {
        repeatableTask = new AbstractRepeatableTask(10, 1000, 1000) {

            @Override
            protected boolean internalExecute() throws Exception {
                return "B".equals(test);
            }
        };
    }


    @Test
    public void test_nominal() throws Exception {

        Thread testThread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                }
                catch (InterruptedException e) {
                    ;
                }
                test = "B";
            }
        };
        testThread.start();

        Assert.assertTrue(repeatableTask.execute());
    }


    @Test
    public void test_ko() throws Exception {
        Assert.assertFalse(repeatableTask.execute());
    }
}
