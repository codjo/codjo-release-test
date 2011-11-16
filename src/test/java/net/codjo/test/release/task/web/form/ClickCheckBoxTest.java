package net.codjo.test.release.task.web.form;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebStepTestCase;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
/**
 *
 */
public class ClickCheckBoxTest extends WebStepTestCase {

    public void test_checkbox() throws Exception {
        WebContext context = loadPage(wrapHtml(formWithField("checkbox", "boite")));
        context.setForm((HtmlForm)context.getHtmlPage().getHtmlElementById("form"));
        click("boite", context);
        assertEquals("boite = [on]\n", context.getHtmlPage().getWebResponse().getContentAsString());
    }


    public void test_checkboxNotFound() throws Exception {
        WebContext context = loadPage(wrapHtml(formWithField("checkbox", "boiteboite")));
        context.setForm((HtmlForm)context.getHtmlPage().getHtmlElementById("form"));
        try {
            click("boite", context);
            fail();
        }
        catch (BuildException e) {
            assertEquals("L'élément 'boite' est introuvable dans le formulaire.", e.getMessage());
        }
    }


    public void test_notACheckbox() throws Exception {
        WebContext context = loadPage(wrapHtml(formWithField("text", "boite")));
        context.setForm((HtmlForm)context.getHtmlPage().getHtmlElementById("form"));
        try {
            click("boite", context);
            fail();
        }
        catch (BuildException e) {
            assertEquals("L'élément 'boite' n'est pas une checkbox.", e.getMessage());
        }
    }


    private void click(String name, WebContext context) throws IOException {
        ClickCheckBox step = new ClickCheckBox();
        step.setName(name);
        step.proceed(context);
    }


    private String formWithField(String type, String name) {
        return "<form method='post' id='form'>"
               + "<input type='" + type + "' name='" + name + "'"
               + "       onClick='document.forms[\"form\"].submit();'/>"
               + "</form>";
    }
}
