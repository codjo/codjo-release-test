package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
/**
 * Simule un click dans une checkbox hors formulaire.
 */
public class ClickCheckBox extends AbstractCheckBoxStep {

    @Override
    protected void run(HtmlCheckBoxInput checkbox, WebContext context) throws IOException {
        context.setPage((HtmlPage)checkbox.click());
    }
}
