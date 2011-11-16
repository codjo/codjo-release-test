package net.codjo.test.release.task.web;
import java.io.IOException;

public class RefreshTest extends WebStepTestCase {
    public void test_ok() throws Exception {
        addPage("target.html", wrapHtml("Target title", "Target content"));
        WebContext context = loadPage(wrapHtml("New title", "<h1>Hi there.</h1>"));
        refresh(context);
        assertEquals("New title", context.getHtmlPage().getTitleText());
    }


    private void refresh(WebContext context) throws IOException {
        Refresh step = new Refresh();
        step.proceed(context);
    }
}
