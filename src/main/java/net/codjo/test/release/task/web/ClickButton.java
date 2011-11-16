package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
/**
 *
 */
public class ClickButton implements WebStep {
    private String id;


    public void proceed(WebContext context) throws IOException {
        try {
            HtmlElement element = context.getHtmlPage().getHtmlElementById(id);
            if (!(element instanceof HtmlButtonInput)) {
                throw new WebException(
                      "L'élément '" + id + "' n'est pas de la forme <input type='button' ...>");
            }
            HtmlButtonInput button = (HtmlButtonInput)element;
            context.setPage((HtmlPage)button.click());
        }
        catch (ElementNotFoundException e) {
            throw new WebException("Aucun élément trouvé avec l'identifiant: " + id);
        }
    }


    public void setId(String id) {
        this.id = id;
    }
}
