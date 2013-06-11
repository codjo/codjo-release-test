package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import net.codjo.test.release.task.web.finder.ComponentFinder;
import net.codjo.test.release.task.web.finder.ResultHandler;
/**
 *
 */
public class Click implements WebStep {
    private String id;
    private String xpath;
    private Integer waitForBackgroundJavaScript = null;


    public void proceed(WebContext context) throws IOException {
        try {
            HtmlElement element = findElement(context);
            if (waitForBackgroundJavaScript != null) {
                context.getWebClient().waitForBackgroundJavaScript(waitForBackgroundJavaScript);
            }
            context.setPage((HtmlPage)element.click());
        }
        catch (ElementNotFoundException e) {
            throw new WebException("Aucun élément trouvé avec l'identifiant: " + id);
        }
    }


    private HtmlElement findElement(WebContext context) {
        ComponentFinder<HtmlElement> finder = new ComponentFinder<HtmlElement>(null, id, xpath);
        final ResultHandler resultHandler = buildResultHandler();
        try {
            return finder.find(context, resultHandler);
        }
        catch (ElementNotFoundException e) {
            throw new WebException(resultHandler.getErrorMessage(e.getAttributeValue()));
        }
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
            }


            public String getErrorMessage(final String key) {
                String type = "";
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
        };
    }
}
