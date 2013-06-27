package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import java.io.IOException;
import net.codjo.test.release.task.web.finder.ComponentFinder;
import net.codjo.test.release.task.web.finder.ResultHandler;
/**
 *
 */
public class AssertPresent implements WebStep {
    private String id;
    private String xpath;
    private Boolean present = Boolean.TRUE;


    public void proceed(WebContext context) throws IOException, WebException {
        ComponentFinder<HtmlElement> factory = new ComponentFinder<HtmlElement>(null, id, xpath);
        try {
            factory.find(context, buildResultHandler());
        }
        catch (ElementNotFoundException e) {
            if (Boolean.TRUE.equals(present)) {
                throw new WebException("L'element '" + e.getAttributeValue() + "' n'est pas present dans la page");
            }
        }
    }


    public void setPresent(Boolean present) {
        this.present = present;
    }


    public void setId(String id) {
        this.id = id;
    }


    public void setXpath(String xpath) {
        this.xpath = xpath;
    }


    private ResultHandler buildResultHandler() {
        return new ResultHandler() {
            public void handleElementFound(HtmlElement element, String key) throws WebException {
                if (!Boolean.TRUE.equals(present)) {
                    throw new WebException("L'element '" + key + "' est present dans la page");
                }
            }


            public String getErrorMessage(final String key) {
                return "L'element '" + key + "' n'est pas present dans la page";
            }


            public void handleElementNotFound(ElementNotFoundException e, String id) {
                if (Boolean.TRUE.equals(present)) {
                    throw new WebException("L'element '" + id + "' n'est pas present dans la page");
                }
            }
        };
    }
}
