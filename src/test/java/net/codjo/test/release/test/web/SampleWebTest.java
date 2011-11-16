package net.codjo.test.release.test.web;
import net.codjo.test.release.test.AbstractSampleGuiTestCase;
/**
 *
 */
public class SampleWebTest extends AbstractSampleGuiTestCase {

    private DummyWebServer server = new DummyWebServer();


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        server.start();
    }


    @Override
    protected void tearDown() throws Exception {
        server.stop();
        super.tearDown();
    }


    public void test_standardScenario() throws Exception {
        runScenario("WebScenario_standard.xml");
    }

    public void test_parallelScenario() throws Exception {
        runScenario("WebScenario_parallel.xml");
    }
}
