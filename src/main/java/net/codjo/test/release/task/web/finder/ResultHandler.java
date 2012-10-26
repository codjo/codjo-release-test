package net.codjo.test.release.task.web.finder;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import net.codjo.test.release.task.web.WebException;
/**
 *
 */
public interface ResultHandler {

    public void handleElementFound(HtmlElement element, final String key) throws WebException;


    public void handleElementNotFound(ElementNotFoundException e, String id) throws WebException;


    public String getErrorMessage(final String key);
}
