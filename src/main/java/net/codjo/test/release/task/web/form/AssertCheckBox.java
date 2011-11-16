package net.codjo.test.release.task.web.form;
import net.codjo.test.release.task.web.WebContext;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import java.io.IOException;
import junit.framework.Assert;
/**
 *
 */
public class AssertCheckBox extends AbstractFormCheckBoxStep {

    private String checked;


    @Override
    protected void run(HtmlInput checkBox, WebContext context) throws IOException {
        boolean expected = "true".equals(checked);
        Assert.assertEquals("Etat invalide pour la checkBox '" + name + "' du formulaire '" +
                            context.getForm().getId() + "'", expected, checkBox.isChecked());
    }


    public void setChecked(String expectedState) {
        this.checked = expectedState;
    }
}
