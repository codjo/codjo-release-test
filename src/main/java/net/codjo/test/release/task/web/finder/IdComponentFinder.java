package net.codjo.test.release.task.web.finder;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebException;
/**
 *
 */
public class IdComponentFinder<T> implements IComponentFinder<T> {
    private String id;


    public IdComponentFinder(String id) {
        this.id = id;
    }


    public T find(WebContext context, ResultHandler resultHandler) {
        if (id != null) {
            HtmlPage page = context.getHtmlPage();
            try {
                HtmlElement element = page.getHtmlElementById(this.id);
                if (resultHandler != null) {
                    resultHandler.handleElementFound(element, this.id);
                }
                return (T)element;
            }
            catch (ElementNotFoundException e) {
                if (resultHandler != null) {
                    resultHandler.handleElementNotFound(e, this.id);
                    return null;
                }
                else {
                    throw new WebException(e.getMessage());
                }
            }
        }
        else {
            return null;
        }
    }
}
