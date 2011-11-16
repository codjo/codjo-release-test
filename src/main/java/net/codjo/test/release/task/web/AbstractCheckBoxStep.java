package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import java.io.IOException;
/**
 *
 */
public abstract class AbstractCheckBoxStep implements WebStep {
    protected String id;


    public void proceed(WebContext context) throws IOException {
        HtmlElement element;
        try {
            element = context.getHtmlPage().getHtmlElementById(id);
        }
        catch (ElementNotFoundException e) {
            throw new WebException("L'élément '" + id + "' est introuvable.");
        }
        if (!(element instanceof HtmlCheckBoxInput)) {
            throw new WebException("L'élément '" + id + "' n'est pas une checkbox.");
        }

        HtmlCheckBoxInput checkbox = (HtmlCheckBoxInput)element;
        run(checkbox, context);
    }


    protected abstract void run(HtmlCheckBoxInput checkbox, WebContext context) throws IOException;


    public void setId(String id) {
        this.id = id;
    }
}
