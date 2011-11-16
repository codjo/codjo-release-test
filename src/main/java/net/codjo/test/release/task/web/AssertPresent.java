package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
/**
 *
 */
public class AssertPresent implements WebStep {

    private String id;

    private Boolean present = Boolean.TRUE;


    public void proceed(WebContext context) throws IOException, WebException {
        HtmlPage page = context.getHtmlPage();
        try {
            page.getHtmlElementById(id);
            if (!Boolean.TRUE.equals(present)) {
                throw new WebException("L'element '" + id + "' est present dans la page");
            }
        }
        catch (ElementNotFoundException e) {
            if (Boolean.TRUE.equals(present)) {
                throw new WebException("L'element '" + id + "' n'est pas present dans la page");
            }
        }
    }


    public void setPresent(Boolean present) {
        this.present = present;
    }


    public void setId(String id) {
        this.id = id;
    }
}
