package net.codjo.test.release.task.web.form;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebException;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import java.util.ArrayList;
import java.util.List;
/**
 * Vérifie le contenu d'une combo d'un formulaire.
 * @see net.codjo.test.release.task.web.form.EditForm
 */
public class AssertSelectOptions extends AbstractFormStep {
    private List<Option> options = new ArrayList<Option>();


    public void proceed(WebContext context) {
        HtmlSelect select = context.getForm().getSelectByName(getName());
        List<String> actual = getActualOptions(select);
        if (actual.size() != options.size() || !options.toString().equals(actual.toString())) {
            throw new WebException(
                  "Les options diffèrent de ce qui est attendu :\n"
                  + "Attendu(" + options.size() + ") : " + options + "\n"
                  + "Obtenu(" + actual.size() + ") : " + actual);
        }
    }


    static List<String> getActualOptions(HtmlSelect select) {
        List<HtmlOption> actual = (List<HtmlOption>)select.getOptions();
        List<String> result = new ArrayList<String>();
        for (HtmlOption option : actual) {
            result.add(option.asText());
        }
        return result;
    }


    public void addOption(Option option) {
        options.add(option);
    }
}
