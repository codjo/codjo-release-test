package net.codjo.test.release.task.web.dialogs;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebStepTestCase;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.w3c.dom.html.HTMLBRElement;
/**
 *
 */
public class SetConfirmationTest extends WebStepTestCase {
    public void test_confirm() throws Exception {
        checkConfirm(true, "after");
        checkConfirm(false, "before");
    }


    private void checkConfirm(boolean accept, String expectedValue) throws Exception {
        WebContext context =
              loadPage(wrapHtml(
                    "<h1 id='h1'>before</h1>"
                    + "<a href='#' id='link' "
                    + "   onclick='if (confirm(&quot;are you sure?&quot;)) document.getElementById(&quot;h1&quot;).innerHTML=&quot;after&quot;'/>"));

        SetConfirmation confirmation = new SetConfirmation();
        confirmation.setAccept(accept);
        confirmation.proceed(context);

        HtmlAnchor link = (HtmlAnchor)context.getHtmlPage().getHtmlElementById("link");
        link.click();

        assertEquals(expectedValue, context.getHtmlPage().<HtmlElement>getHtmlElementById("h1").asText());
    }
}
