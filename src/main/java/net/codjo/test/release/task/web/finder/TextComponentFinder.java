package net.codjo.test.release.task.web.finder;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import net.codjo.test.release.task.web.WebContext;
/**
 *
 */
public class TextComponentFinder<T> implements IComponentFinder<T> {
    private String text;


    public TextComponentFinder(String text) {
        this.text = text;
    }


    public T find(WebContext context, ResultHandler resultHandler) {
        if (this.text != null) {
            HtmlPage page = context.getHtmlPage();
            String text = context.replaceProperties(this.text);
            try {
                final HtmlAnchor anchorByText = page.getAnchorByText(text);
                if (resultHandler != null) {
                    resultHandler.handleElementFound(anchorByText, text);
                }
                return (T)anchorByText;//TODO FINDER a generifier
            }
            catch (ElementNotFoundException e) {
                if (resultHandler != null) {
                    resultHandler.handleElementNotFound(e, text);
                }
                return null;
            }
        }
        else {
            return null;
        }
    }
}
