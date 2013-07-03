package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import net.codjo.test.release.task.web.finder.ComponentFinder;
import net.codjo.test.release.task.web.finder.ResultHandler;
/**
 *
 */
public class Click implements WebStep {
    private String text = null;
    private String id;
    private String xpath;


    public void proceed(WebContext context) throws IOException {
        try {
            HtmlElement element = findElement(context);
            context.setPage((HtmlPage)element.click());
            context.getWebClient().waitForBackgroundJavaScript(WAIT_FOR_JAVASCRIPT);
        }
        catch (ElementNotFoundException e) {
            throw new WebException("Aucun élément trouvé avec l'identifiant: " + id);
        }
        catch (FailingHttpStatusCodeException e) {
            throw new WebException("Erreur lors du click sur le lien '" + text + "' : " + e.getMessage());
        }
    }


    private HtmlElement findElement(WebContext context) {
        ComponentFinder<HtmlElement> finder = new ComponentFinder<HtmlElement>(text, id, xpath);
        final ResultHandler resultHandler = buildResultHandler();
        try {
            return finder.find(context, resultHandler);
        }
        catch (ElementNotFoundException e) {
            throw new WebException(resultHandler.getErrorMessage(e.getAttributeValue()));
        }
    }


    protected void setText(String text) {
        this.text = text;
    }


    public void setId(String id) {
        this.id = id;
    }


    public void setXpath(String xpath) {
        this.xpath = xpath;
    }


    protected ResultHandler buildResultHandler() {
        return new DefaultResultHandler();
    }


    protected class DefaultResultHandler implements ResultHandler   {
        public void handleElementFound(HtmlElement element, String key) throws WebException {
        }


        public String getErrorMessage(final String key) {
            String type = "";
            if (text != null) {
                type = "le texte: ";
            }
            if (id != null) {
                type = "l'identifiant: ";
            }
            if (xpath != null) {
                type = "l'expression xpath: ";
            }
            return "Aucun élément trouvé avec " + type + key;
        }


        public void handleElementNotFound(ElementNotFoundException e, String id) throws WebException {
            throw new WebException(getErrorMessage(id));
        }
    }
}
