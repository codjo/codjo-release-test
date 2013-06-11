package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import net.codjo.test.release.task.web.finder.ComponentFinder;
import net.codjo.test.release.task.web.finder.ResultHandler;
/**
 *
 */
public class ClickLink implements WebStep {

    private String text;
    private String id;
    private String xpath;
    private Integer waitForBackgroundJavaScript=null;


    public void proceed(WebContext context) throws IOException {
        try {
            HtmlElement element = findAnchor(context);
            if (waitForBackgroundJavaScript != null) {
                context.getWebClient().waitForBackgroundJavaScript(waitForBackgroundJavaScript);
            }
            HtmlPage click = (HtmlPage)element.click();
            context.setPage(click);
        }
        catch (FailingHttpStatusCodeException e) {
            throw new WebException("Erreur lors du click sur le lien '" + text +
                                   "' : " + e.getMessage());
        }
    }


    private HtmlElement findAnchor(WebContext context) {
        ComponentFinder<HtmlElement> finder = new ComponentFinder<HtmlElement>(text, id, xpath);
        final ResultHandler resultHandler = buildResultHandler();
        try {
            return finder.find(context, resultHandler);
        }
        catch (ElementNotFoundException e) {
            throw new WebException(resultHandler.getErrorMessage(e.getAttributeValue()));
        }
    }


    public void setText(String text) {
        this.text = text;
    }


    public void setId(String id) {
        this.id = id;
    }


    public void setXpath(String xpath) {
        this.xpath = xpath;
    }


    public void setWaitForBackgroundJavaScript(Integer waitForBackgroundJavaScript) {
        this.waitForBackgroundJavaScript = waitForBackgroundJavaScript;
    }


    private ResultHandler buildResultHandler() {
        return new ResultHandler() {
            public void handleElementFound(HtmlElement element, String key) throws WebException {
                if (!(element instanceof HtmlAnchor)) {
                    throw new WebException("L'élément '" + key + "' n'est pas un lien <a ...>");
                }
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
                return "Aucun lien trouvé avec " + type + key;
            }


            public void handleElementNotFound(ElementNotFoundException e, String id) throws WebException{
                throw new WebException(getErrorMessage(id));
            }
        };
    }
}
