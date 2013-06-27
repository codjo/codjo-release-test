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
    private Integer index;


    public XpathComponentFinder(String xpath) {
        this(xpath, null);
    }


    public XpathComponentFinder(String xpath, Integer index) {
        this.xpath = xpath;
        this.index = index;
    }


    public T find(WebContext context, ResultHandler resultHandler) {
        if (this.xpath != null) {
            HtmlPage page = context.getHtmlPage();
            List<?> elementList = page.getByXPath(this.xpath);
            if (elementList.isEmpty()) {
                resultHandler.handleElementNotFound(new ElementNotFoundException("", "", this.xpath), this.xpath);
                return null;
            }
            else {
                final int listSize = elementList.size();
                if (onlyOneResult(listSize) || indexHasBeenSet(listSize)) {
                    final int indexInList = index == null ? 0 : index - 1;
                    if (indexInList > listSize) {
                        throw new WebException(
                              "Impossible de trouver l'index " + index + " dans la liste des résultats de taille "
                              + listSize + ", pour l'expression xpath: " + xpath + " ");
                    }
                    HtmlElement element = (HtmlElement)elementList.get(indexInList);
                    if (resultHandler != null) {
                        resultHandler.handleElementFound(element, this.xpath);
                    }
                    return (T)element;
                }
                else {
                    StringBuilder message = new StringBuilder(
                          "Ambiguité, plusieurs éléments ont été trouvé avec l'expression xpath:'" + this.xpath
                          + "'\n");
                    for (Object o : elementList) {
                        message.append("\tobject : ").append(o.toString()).append("\n");
                    }
                    throw new WebException(message.toString());
                }
            }
        }
        else {
            return null;
        }
    }


    private boolean indexHasBeenSet(int listSize) {
        return (listSize > 0 && index != null);
    }


    private boolean onlyOneResult(int listSize) {
        return listSize == 1;
    }
}
