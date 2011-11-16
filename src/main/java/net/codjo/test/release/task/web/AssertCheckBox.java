package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import java.io.IOException;
import junit.framework.Assert;
/**
 *
 */
public class AssertCheckBox extends AbstractCheckBoxStep {

    private String checked;


    @Override
    protected void run(HtmlCheckBoxInput checkBox, WebContext context) throws IOException {
        boolean expected = "true".equals(checked);
        Assert.assertEquals("Etat invalide pour la checkBox '" + id + "'", expected, checkBox.isChecked());
    }


    public void setChecked(String expectedState) {
        this.checked = expectedState;
    }
}
