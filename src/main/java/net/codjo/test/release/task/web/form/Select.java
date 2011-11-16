package net.codjo.test.release.task.web.form;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebException;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import java.util.List;
/**
 * Sélectionne une entrée dans une combo de formulaire.
 * @see net.codjo.test.release.task.web.form.EditForm
 */
public class Select extends AbstractFormStep {
    private String value;


    public void proceed(WebContext context) {
        HtmlSelect select;
        try {
            select = context.getForm().getSelectByName(getName());
        }
        catch (ElementNotFoundException e) {
            throw new WebException("Le select '" + getName() + "' n'existe pas dans le formulaire.");
        }

        for (Object obj : select.getOptions()) {
            HtmlOption option = (HtmlOption)obj;
            if (option.asText().equals(value)) {
                context.setPage((HtmlPage)select.setSelectedAttribute(option, true));
                return;
            }
        }
        List<String> actual = AssertSelectOptions.getActualOptions(select);
        throw new WebException(
              "L'option '" + value + "' n'est pas disponible pour le select '" + getName() + "'.\n"
              + "Les options possibles sont : " + actual + ".");
    }


    public void setValue(String value) {
        this.value = value;
    }
}
