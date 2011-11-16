package net.codjo.test.release.task.web;
import org.apache.tools.ant.BuildException;
/**
 *
 */
public class AssertImageTest extends WebStepTestCase {

    public void test_imageSrcPresent() throws Exception {
        String page = wrapHtml("<img src=\"patati.png\"/>");

        assertImage(page, "patati.png");
    }


    public void test_twoImageSrcPresent() throws Exception {
        String page = wrapHtml("<img src=\"patati.png\"/><br/><img src=\"patatra.png\"/>");

        assertImage(page, "patatra.png");
    }


    public void test_twoImageSrcNotFound() throws Exception {
        String page = wrapHtml("<img src=\"patati.png\"/><br/><img src=\"patatra.png\"/>");
        try {
            assertImage(page, "patata.png");
            fail();
        }
        catch (BuildException e) {
            assertEquals(
                  "Image 'patata.png' non présente dans la page 'Ma page' (http://localhost:8181/test_twoImageSrcNotFound.html)",
                  e.getMessage());
        }
    }


    @Override
    protected String wrapHtml(String content) {
        return wrapHtml("Ma page", content);
    }


    private void assertImage(String actual, String expected) throws Exception {
        runStep(actual, expected);
    }


    private void runStep(String actual, String expectedSrc)
          throws Exception {
        WebContext context = loadPage(wrapHtml(actual));

        AssertImage step = new AssertImage();
        step.setSrc(expectedSrc);
        step.proceed(context);
    }
}
