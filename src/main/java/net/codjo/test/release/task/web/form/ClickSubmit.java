package net.codjo.test.release.task.web.form;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebException;
import net.codjo.test.release.task.web.WebStep;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
/**
 * Déclenche la validation d'un formulaire via un bouton "submit" identifié par son champ "name" au sein du
 * formulaire. 
 * @see net.codjo.test.release.task.web.form.EditForm
 */
public class ClickSubmit implements WebStep {
    private String name;
    private String value;


    public void proceed(WebContext context) throws IOException {
        HtmlInput input = null;
        if (name != null) {
            input = context.getForm().getInputByName(name);
        }
        if (input == null && value != null) {
            input = context.getForm().getInputByValue(value);
        }

        if (input != null) {
            context.setPage((HtmlPage)input.click());
        }
        else {
            throw new WebException("Impossible de trouver le bouton submit");
        }
    }


    public void setValue(String value) {
        this.value = value;
    }


    public void setName(String name) {
        this.name = name;
    }
}
