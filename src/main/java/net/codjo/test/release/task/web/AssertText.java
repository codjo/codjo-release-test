package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.net.MalformedURLException;
import net.codjo.test.release.task.web.finder.ComponentFinder;
import net.codjo.test.release.task.web.finder.ResultHandler;
/**
 *
 */
public class AssertText implements WebStep {
    private String value = "";
    private Boolean present = Boolean.TRUE;
    private ComponentIdentifier identifier = new ComponentIdentifier();


    public void proceed(WebContext context) throws IOException {
        HtmlPage page = context.getHtmlPage();
        String expected = context.replaceProperties(value);

        String content = getContent(page, context);
        if (content.contains(expected) != present) {
            if (identifier.getArgValue() != null) {
                throw new WebException(
                      "Texte '" + expected + "' " + getErrorText() + " dans le container '"
                      + identifier.getArgValue() + "' de la page '" + page.getFullyQualifiedUrl("") + "'");
            }
            throw new WebException("Texte '" + expected + "' " + getErrorText() + " "
                                   + "dans la page '" + page.getTitleText() + "' "
                                   + "(" + page.getFullyQualifiedUrl("") + ")");
        }
    }


    private String getContent(HtmlPage page, WebContext context) throws IOException {
        if (identifier.getArgValue() == null) {
            return page.asText();
        }
        final HtmlElement container = findContainer(page, context);
        if (container != null) {
            return container.asText();
        }
        return page.asText();
    }


    private HtmlElement findContainer(HtmlPage page, WebContext context) {
        ComponentFinder<HtmlElement> finder
              = new ComponentFinder<HtmlElement>(ComponentIdentifier.toArgumentMap(identifier));
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
        identifier.setId(containerId);
    }


    public void setPresent(Boolean present) {
        this.present = present != null ? present : Boolean.TRUE;
    }


    public void setContainerXpath(String containerXpath) {
        identifier.setXpath(containerXpath);
    }


    public void setContainerCssClass(String containerCssClass) {
        identifier.setCssClass(containerCssClass);
    }


    public void setContainerIndex(Integer containerIndex) {
        identifier.setIndex(containerIndex);
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
