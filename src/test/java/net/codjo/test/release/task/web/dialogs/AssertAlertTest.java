package net.codjo.test.release.task.web.dialogs;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebStepTestCase;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import java.io.IOException;
import junit.framework.AssertionFailedError;
/**
 *
 */
public class AssertAlertTest extends WebStepTestCase {
    public void testAlert() throws Exception {
        WebContext context = loadPage(
              wrapHtml("<a href='#' id='link' onclick='alert(&quot;hello!&quot;);'/>"));
        click(context, "link", "hello!");
    }


    public void testNoAlertRaised() throws Exception {
        checkError("", "any", "L'alerte 'any' n'a pas été levée");
    }


    public void testAlertWithAnotherMessage() throws Exception {
        checkError("onclick='alert(&quot;other&quot;);'", "hello!",
                   "L'alerte 'hello!' n'a pas été levée - alertes=  [other]");
    }


    private void checkError(String onclick, String expectedAlertMessage, String expectedErrorMessage)
          throws Exception {
        WebContext context = loadPage(wrapHtml("<a href='#' id='link' " + onclick + "/>"));
        try {
            click(context, "link", expectedAlertMessage);
            fail();
        }
        catch (AssertionFailedError e) {
            assertEquals(expectedErrorMessage, e.getMessage());
        }
    }


    private void click(WebContext context, String id, String expectedMessage) throws IOException {
        HtmlAnchor link = (HtmlAnchor)context.getHtmlPage().getHtmlElementById(id);
        link.click();
        AssertAlert assertion = new AssertAlert();
        assertion.addText(expectedMessage);
        assertion.proceed(context);
    }
}
