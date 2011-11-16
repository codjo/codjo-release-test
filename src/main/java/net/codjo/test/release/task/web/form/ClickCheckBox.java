package net.codjo.test.release.task.web.form;
import net.codjo.test.release.task.web.WebContext;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
/**
 * Simule un click dans une checkbox de formulaire.
 * @see net.codjo.test.release.task.web.form.EditForm
 */
public class ClickCheckBox extends AbstractFormCheckBoxStep {

    @Override
    protected void run(HtmlInput checkBox, WebContext context) throws IOException {
        context.setPage((HtmlPage)checkBox.click());
    }
}
