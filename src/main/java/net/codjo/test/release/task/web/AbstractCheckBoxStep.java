package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import java.io.IOException;
import net.codjo.test.release.task.web.finder.ComponentFinder;
import net.codjo.test.release.task.web.finder.ResultHandler;
/**
 *
 */
public abstract class AbstractCheckBoxStep implements WebStep {
    protected String id;
    private String xpath;


    public void proceed(WebContext context) throws IOException {
        HtmlElement element;
        ComponentFinder<HtmlAnchor> finder = new ComponentFinder<HtmlAnchor>(null, id, xpath);
        final ResultHandler resultHandler = buildResultHandler();
        try {
            element = finder.find(context, resultHandler);
        }
        catch (ElementNotFoundException e) {
            throw new WebException(resultHandler.getErrorMessage(e.getAttributeValue()));
        }

        HtmlCheckBoxInput checkbox = (HtmlCheckBoxInput)element;
        run(checkbox, context);
    }


    protected abstract void run(HtmlCheckBoxInput checkbox, WebContext context) throws IOException;


    public void setId(String id) {
        this.id = id;
    }


    public void setXpath(String xpath) {
        this.xpath = xpath;
    }


    private ResultHandler buildResultHandler() {
        return new ResultHandler() {
            public void handleElementFound(HtmlElement element, String key) throws WebException {
                if (!(element instanceof HtmlCheckBoxInput)) {
                    throw new WebException("L'élément '" + key + "' n'est pas une checkbox.");
                }
            }


            public String getErrorMessage(final String key) {
                return "L'élément '" + key + "' est introuvable.";
            }


            public void handleElementNotFound(ElementNotFoundException e, String id) throws WebException {
                throw new WebException(getErrorMessage(id));
            }
        };
    }
}
