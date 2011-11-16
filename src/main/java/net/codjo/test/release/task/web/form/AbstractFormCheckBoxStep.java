package net.codjo.test.release.task.web.form;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebException;
import net.codjo.test.release.task.web.WebStep;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import java.io.IOException;
/**
 *
 */
public abstract class AbstractFormCheckBoxStep implements WebStep {
    protected String name;


    public void proceed(WebContext context) throws IOException {
        HtmlInput checkBox;
        try {
            checkBox = context.getForm().getInputByName(name);
        }
        catch (ElementNotFoundException e) {
            throw new WebException("L'élément '" + name + "' est introuvable dans le formulaire.");
        }
        if (!(checkBox instanceof HtmlCheckBoxInput)) {
            throw new WebException("L'élément '" + name + "' n'est pas une checkbox.");
        }

        run(checkBox, context);
    }


    protected abstract void run(HtmlInput checkBox, WebContext context) throws IOException;


    public void setName(String name) {
        this.name = name;
    }
}
