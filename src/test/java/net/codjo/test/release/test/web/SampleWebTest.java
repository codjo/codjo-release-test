package net.codjo.test.release.test.web;
import java.io.IOException;
import net.codjo.test.release.test.AbstractSampleGuiTestCase;
import net.codjo.util.file.FileUtil;
/**
 *
 */
public class SampleWebTest extends AbstractSampleGuiTestCase {

    private DummyWebServer server = new DummyWebServer();


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        server.addPage("dragAndDropImplementation.js", getDragAndSropJavascript());
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


    private String getDragAndSropJavascript() throws IOException {
        return FileUtil.loadContent(getClass().getResource(
              "/net/codjo/test/release/javascript/dragAndDropImplementation.js"));
    }
}
