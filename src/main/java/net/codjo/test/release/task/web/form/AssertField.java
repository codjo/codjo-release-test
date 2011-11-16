package net.codjo.test.release.task.web.form;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebException;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
/**
 * Vérifie le contenu d'un champ de formulaire, identifié par son attribut "name".
 * @see net.codjo.test.release.task.web.form.EditForm
 */
public class AssertField extends AbstractFormStep {
    private String value;


    public void proceed(WebContext context) {
        String actual;
        try {
            actual = context.getForm().<HtmlInput>getInputByName(getName()).getValueAttribute();
        }
        catch (ElementNotFoundException e) {
            actual = context.getForm().getTextAreaByName(getName()).getText();
        }

        if (!value.equalsIgnoreCase(actual)) {
            throw new WebException("Contenu du champ '" + getName()
                                   + "' en erreur : " + value
                                   + " obtenu : " + actual);
        }
    }


    public void setValue(String value) {
        this.value = value;
    }
}
