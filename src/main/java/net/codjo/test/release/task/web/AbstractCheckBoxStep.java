package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import java.io.IOException;
import net.codjo.test.release.task.web.finder.ComponentFinder;
import net.codjo.test.release.task.web.finder.ResultHandler;
/**
 *
 */
public abstract class AbstractCheckBoxStep implements WebStep {
    private ComponentIdentifier identifier = new ComponentIdentifier();


    public void proceed(WebContext context) throws IOException {
        HtmlCheckBoxInput checkbox = (HtmlCheckBoxInput)findElement(context);
        run(checkbox, context);
    }


    private HtmlElement findElement(WebContext context) {
        ComponentFinder<HtmlElement> finder = new ComponentFinder<HtmlElement>(ComponentIdentifier.toArgumentMap(
              identifier));
        final ResultHandler resultHandler = buildResultHandler();
        try {
            return finder.find(context, resultHandler);
        }
        catch (ElementNotFoundException e) {
            throw new WebException(resultHandler.getErrorMessage(e.getAttributeValue()));
        }
    }


    protected abstract void run(HtmlCheckBoxInput checkbox, WebContext context) throws IOException;


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

    protected String getArgValue() {
        return identifier.getArgValue();
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
