package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import net.codjo.test.release.task.web.finder.ComponentFinder;
import net.codjo.test.release.task.web.finder.ResultHandler;
/**
 *
 */
public class AssertText implements WebStep {
    private String value = "";
    private String containerId = null;
    private Boolean present = Boolean.TRUE;
    private String containerXpath = null;
    private String containerCssClass = null;
    private Integer containerIndex = null;


    public void proceed(WebContext context) throws IOException {
        HtmlPage page = context.getHtmlPage();
        String expected = context.replaceProperties(value);

        String content = getContent(page, context);
        if (content.contains(expected) != present) {
            if (containerId != null || containerXpath != null || containerCssClass != null) {
                throw new WebException(
                      "Texte '" + expected + "' " + getErrorText() + " dans le container '"
                      + (containerId != null ?
                         containerId :
                         (containerXpath != null ? containerXpath : containerCssClass))
                      + "' de la page '" + page.getFullyQualifiedUrl("") + "'");
            }
            throw new WebException("Texte '" + expected + "' " + getErrorText() + " "
                                   + "dans la page '" + page.getTitleText() + "' "
                                   + "(" + page.getFullyQualifiedUrl("") + ")");
        }
    }


    private String getContent(HtmlPage page, WebContext context) throws IOException {
        if (containerId == null && containerXpath == null && containerCssClass==null) {
            return page.asText();
        }
        final HtmlElement container = findContainer(page, context);
        if (container != null) {
            return container.asText();
        }
        return page.asText();
    }


    private HtmlElement findContainer(HtmlPage page, WebContext context) {
        ComponentFinder<HtmlElement> finder = new ComponentFinder<HtmlElement>(toArgumentMap());
        final ResultHandler resultHandler = buildResultHandler(page);
        try {
            return finder.find(context, resultHandler);
        }
        catch (ElementNotFoundException e) {
            throw new WebException(resultHandler.getErrorMessage(e.getAttributeValue()));
        }
    }


    private String getErrorText() {
        return present ? "non présent" : "présent";
    }


    public void addText(String aValue) {
        this.value += aValue;
    }


    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }


    public void setPresent(Boolean present) {
        this.present = present != null ? present : Boolean.TRUE;
    }


    public void setContainerXpath(String containerXpath) {
        this.containerXpath = containerXpath;
    }


    public void setContainerCssClass(String containerCssClass) {
        this.containerCssClass = containerCssClass;
    }


    public void setContainerIndex(Integer containerIndex) {
        this.containerIndex = containerIndex;
    }


    private Map<String, String> toArgumentMap() {
        final HashMap<String, String> result = new HashMap<String, String>();
        result.put("id", containerId);
        result.put("xpath", containerXpath);
        result.put("cssClass", containerCssClass);
        result.put("index", ((containerIndex != null) ? String.valueOf(containerIndex) : null));
        return result;
    }


    private ResultHandler buildResultHandler(final HtmlPage page) {
        return new ResultHandler() {
            public void handleElementFound(HtmlElement element, String key) throws WebException {
            }


            public String getErrorMessage(final String key) {
                return "Container '" + key + "' " + getErrorText() + " dans la page '" + getPageUrl(page) + "'";
            }


            public void handleElementNotFound(ElementNotFoundException e, String id) throws WebException {
                throw new WebException(getErrorMessage(id));
            }
        };
    }


    private String getPageUrl(HtmlPage page) {
        try {
            return page.getFullyQualifiedUrl("").toString();
        }
        catch (MalformedURLException e) {
            return "";
        }
    }
}
