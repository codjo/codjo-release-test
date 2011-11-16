package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
/**
 *
 */
public class Refresh implements WebStep {

    public void proceed(WebContext context) throws IOException {
        String url = context.getHtmlPage().getFullyQualifiedUrl("").toExternalForm();
        context.setPage((HtmlPage)context.getWebClient().getPage(url));
    }
}
