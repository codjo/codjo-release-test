package net.codjo.test.release.task.web.form;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebStep;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import java.io.IOException;
import java.util.List;
/**
 *
 */
public abstract class AbstractFormRadioButtonStep implements WebStep {
    protected String groupName;
    protected String value;


    public void proceed(WebContext context) throws IOException {
        List<HtmlRadioButtonInput> checkBoxs;

        checkBoxs = context.getForm().getRadioButtonsByName(groupName);

        if (!checkBoxs.isEmpty()) {
            for (HtmlInput checkBox : checkBoxs) {
                String currentValue = checkBox.getValueAttribute();
                if (value.equals(currentValue)) {
                    run(checkBox, context);
                }
            }
        }
        else {
            throw new IOException("L'élément '" + groupName + "' est introuvable dans le formulaire.");
        }
    }


    protected abstract void run(HtmlInput checkBox, WebContext context) throws IOException;


    public void setName(String name) {
        this.groupName = name;
    }


    public void setValue(String value) {
        this.value = value;
    }
}
