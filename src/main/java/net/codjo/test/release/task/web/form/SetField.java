package net.codjo.test.release.task.web.form;
import net.codjo.test.release.task.web.WebContext;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import org.apache.tools.ant.BuildException;
/**
 * Positionne le contenu d'un champ de formulaire, identifié via son attribut "name".
 * @see net.codjo.test.release.task.web.form.EditForm
 */
public class SetField extends AbstractFormStep {
    private String value;
    private static final String TRUE = "true";


    public void proceed(WebContext context) {
        HtmlInput input = findInputByName(context);
        if (input != null) {

            if (input instanceof HtmlCheckBoxInput) {
                input.setChecked(TRUE.equals(value));
            }
            else {
                context.setPage((HtmlPage)input.setValueAttribute(value));
            }
        }
        else {
            HtmlTextArea textArea = findTextAreaByName(context);
            if (textArea == null) {
                throw new BuildException("Le composant '" + getName() + "' n'est pas présent");
            }
            textArea.setText(value);
        }
    }


    private HtmlInput findInputByName(WebContext context) {
        try {
            return context.getForm().getInputByName(getName());
        }
        catch (ElementNotFoundException e) {
            return null;
        }
    }


    private HtmlTextArea findTextAreaByName(WebContext context) {
        try {
            return context.getForm().getTextAreaByName(getName());
        }
        catch (ElementNotFoundException e) {
            return null;
        }
    }


    public void setValue(String value) {
        this.value = value;
    }
}
