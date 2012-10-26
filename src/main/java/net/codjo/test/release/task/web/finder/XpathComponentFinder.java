package net.codjo.test.release.task.web.finder;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.util.List;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebException;
/**
 *
 */
public class XpathComponentFinder<T> implements IComponentFinder<T> {
    private String xpath;


    public XpathComponentFinder(String xpath) {
        this.xpath = xpath;
    }


    public T find(WebContext context, ResultHandler resultHandler) {
        if (this.xpath != null) {
            HtmlPage page = context.getHtmlPage();
            List<?> elementList = page.getByXPath(this.xpath);
            if (elementList.isEmpty()) {
                resultHandler.handleElementNotFound(new ElementNotFoundException("", "", this.xpath), this.xpath);
                return null;
            }
            else if (elementList.size() == 1) {
                HtmlElement element = (HtmlElement)elementList.get(0);
                if (resultHandler != null) {
                    resultHandler.handleElementFound(element, this.xpath);
                }
                return (T)element;
            }
            else {
                StringBuilder message = new StringBuilder(
                      "Ambiguité, plusieurs éléments ont été trouvé avec l'expression xpath:'" + this.xpath + "'\n");
                for (Object o : elementList) {
                    message.append("\tobject : ").append(o.toString()).append("\n");
                }
                throw new WebException(message.toString());
            }
        }
        else {
            return null;
        }
    }
}
