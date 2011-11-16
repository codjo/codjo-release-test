package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
/**
 *
 */
public class AssertText implements WebStep {
    private String value = "";
    private String containerId = null;
    private Boolean present = Boolean.TRUE;


    public void proceed(WebContext context) throws IOException {
        HtmlPage page = context.getHtmlPage();

        String expected = context.replaceProperties(value);

        String content = getContent(page);
        if (content.contains(expected) != present) {
            if (containerId != null) {
                throw new WebException(
                      "Texte '" + expected + "' " + getErrorText() + " dans le container '" + containerId
                      + "' de la page '" + page.getFullyQualifiedUrl("") + "'");
            }
            throw new WebException("Texte '" + expected + "' " + getErrorText() + " "
                                   + "dans la page '" + page.getTitleText() + "' "
                                   + "(" + page.getFullyQualifiedUrl("") + ")");
        }
    }


    private String getContent(HtmlPage page) throws IOException {
        if (containerId == null) {
            return page.asText();
        }
        try {
            return page.<HtmlElement>getHtmlElementById(containerId).asText();
        }
        catch (ElementNotFoundException e) {
            throw new WebException(
                  "Container '" + containerId + "' " + getErrorText() + " dans la page '"
                  + page.getFullyQualifiedUrl("")
                  + "'");
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
}
