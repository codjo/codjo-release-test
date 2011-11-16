package net.codjo.test.release.task.web.form;
import net.codjo.test.release.task.web.WebContext;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
/**
 *
 */
public class ClickRadioButton extends AbstractFormRadioButtonStep {

    @Override
    protected void run(HtmlInput radio, WebContext context) throws IOException {
        context.setPage((HtmlPage)radio.click());
    }
}
