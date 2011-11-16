package net.codjo.test.release.task.web;
/**
 *
 */
public class AssertPresentTest extends WebStepTestCase {
    public void test_ok() throws Exception {
        checkPage("<span id='container'/>", "container", true);
    }


    public void test_error() throws Exception {
        try {
            checkPage("<span id='container'/>", "unknown", true);
            fail();
        }
        catch (Exception e) {
            assertEquals("L'element 'unknown' n'est pas present dans la page", e.getMessage());
        }
    }


    public void test_notPresent() throws Exception {
        checkPage("<span id='container' present='false'/>", "unknown", false);
    }


    public void test_notPresent_error() throws Exception {
        try {
            checkPage("<span id='container' present='false'/>", "container", false);
            fail();
        }
        catch (Exception e) {
            assertEquals("L'element 'container' est present dans la page", e.getMessage());
        }
    }


    private void checkPage(String content, String id, boolean isPresent) throws Exception {
        WebContext context = loadPage(wrapHtml("title", content));
        AssertPresent step = new AssertPresent();
        step.setId(id);
        step.setPresent(isPresent);
        step.proceed(context);
    }
}
