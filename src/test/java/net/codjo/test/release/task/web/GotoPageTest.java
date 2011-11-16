package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
/**
 *
 */
public class GotoPageTest extends WebStepTestCase {
    public void test() throws Exception {
        addPage("new.html", wrapHtml("Target title", "Target content"));
        WebContext context = loadPage(wrapHtml("Source content"));

        assertEquals("Source content", context.getHtmlPage().asText());

        GotoPage step = new GotoPage();
        step.setUrl(getUrl("new.html"));
        step.proceed(context);

        HtmlPage newPage = context.getHtmlPage();
        assertEquals("Target title\r\nTarget content", newPage.asText());
    }


    public void test_invalidUrl() throws Exception {
        WebContext context = loadPage(wrapHtml("source"));

        GotoPage step = new GotoPage();
        try {
            step.setUrl("http://www.sdkfjsdlkjfqmsldkfjqsmldkfjqmlsdkf.xyz");
            step.proceed(context);
            fail();
        }
        catch (Exception e) {
            assertEquals("Impossible de se connecter a l'adresse : "
                         + "http://www.sdkfjsdlkjfqmsldkfjqsmldkfjqmlsdkf.xyz",
                         e.getMessage());
        }
    }
}
