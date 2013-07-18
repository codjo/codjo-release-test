package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.util.Map;
import net.codjo.test.release.task.web.finder.ComponentFinder;
import net.codjo.test.release.task.web.finder.ResultHandler;
/**
 *
 */
public class Click implements WebStep {
    private String text = null;
    private ComponentIdentifier identifier = new ComponentIdentifier();


    public void proceed(WebContext context) throws IOException {
        try {
            HtmlElement element = findElement(context);
            context.setPage((HtmlPage)element.click());
            context.getWebClient().waitForBackgroundJavaScript(WAIT_FOR_JAVASCRIPT);
        }
        catch (ElementNotFoundException e) {
            throw new WebException("Aucun élément trouvé avec l'identifiant: " + identifier.getArgValue());
        }
        catch (FailingHttpStatusCodeException e) {
            throw new WebException("Erreur lors du click sur le lien '" + text + "' : " + e.getMessage());
        }
    }


    private HtmlElement findElement(WebContext context) {
        ComponentFinder<HtmlElement> finder = new ComponentFinder<HtmlElement>(toArgumentMap());
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
        identifier.setId(id);
    }


    public void setXpath(String xpath) {
        identifier.setXpath(xpath);
    }


    public void setCssClass(String cssClass) {
        identifier.setCssClass(cssClass);
    }


    public void setIndex(Integer index) {
        identifier.setIndex(index);
    }


    protected ResultHandler buildResultHandler() {
        return new DefaultResultHandler();
    }


    private Map<String, String> toArgumentMap() {
        final Map<String, String> result = ComponentIdentifier.toArgumentMap(identifier);
        result.put("text", text);
        return result;
    }


    protected class DefaultResultHandler implements ResultHandler {
        public void handleElementFound(HtmlElement element, String key) throws WebException {
        }


        public String getErrorMessage(final String key) {
            String type = "";
            if (text != null) {
                type = "le texte: ";
            }
            if (identifier.getId() != null) {
                type = "l'identifiant: ";
            }
            if (identifier.getXpath() != null) {
                type = "l'expression xpath: ";
            }
            if (identifier.getCssClass() != null) {
                type = "la classe css: ";
            }
            return "Aucun élément trouvé avec " + type + key;
        }


        public void handleElementNotFound(ElementNotFoundException e, String id) throws WebException {
            throw new WebException(getErrorMessage(id));
        }
    }
}
