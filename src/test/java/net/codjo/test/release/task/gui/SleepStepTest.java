package net.codjo.test.release.task.gui;

import junit.framework.TestCase;

public class SleepStepTest extends TestCase {
    private SleepStep step;


    public void test_defaults() throws Exception {
        assertEquals(0, step.getDuration().intValue());
    }


    @Override
    protected void setUp() throws Exception {
        step = new SleepStep();
        step.setTimeout(1);
    }


    public void test_proceed() throws Exception {
        final int expectedDuration = 900;
        step.setDuration(expectedDuration);

        long beforeCurrentTimeStamp = System.currentTimeMillis();
        step.proceed(null);
        long afterCurrentTimeStamp = System.currentTimeMillis();

        long actualDuration = afterCurrentTimeStamp - beforeCurrentTimeStamp;
        long minDuration = expectedDuration - 50;
        long maxDuration = expectedDuration + 50;
        assertTrue("actualDuration(" + actualDuration + ") >= " + minDuration, actualDuration >= minDuration);
        assertTrue("actualDuration(" + actualDuration + ") < " + maxDuration, actualDuration < maxDuration);
    }
}
