package net.codjo.test.release.task.web.form;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebStepTestCase;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
/**
 *
 */
public class SetFieldTest extends WebStepTestCase {

    public void test_checkbox() throws Exception {
        WebContext context = loadPage(wrapHtml(
              "<form method='post' id='form'><input type='checkbox' name='boite'/><input type='submit' name='gogogo'/></form>"));
        context.setForm((HtmlForm)context.getHtmlPage().getHtmlElementById("form"));
        setField("boite", "true", context);
        click("gogogo", context);
        assertEquals("boite = [on]\ngogogo = [Submit Query]\n",
                     context.getHtmlPage().getWebResponse().getContentAsString());
    }


    public void test_checkboxOff() throws Exception {
        WebContext context = loadPage(wrapHtml(
              "<form method='post' id='form'><input type='checkbox' name='boite'/><input type='submit' name='gogogo'/></form>"));
        context.setForm((HtmlForm)context.getHtmlPage().getHtmlElementById("form"));
        setField("boite", "not true", context);
        click("gogogo", context);
        assertEquals("gogogo = [Submit Query]\n",
                     context.getHtmlPage().getWebResponse().getContentAsString());
    }


    public void test_textField() throws Exception {
        WebContext context = loadPage(wrapHtml(
              "<form method='post' id='form'><input type='text' name='texte'/><input type='submit' name='gogogo'/></form>"));
        context.setForm((HtmlForm)context.getHtmlPage().getHtmlElementById("form"));
        setField("texte", "1234", context);
        click("gogogo", context);
        assertEquals("texte = [1234]\ngogogo = [Submit Query]\n",
                     context.getHtmlPage().getWebResponse().getContentAsString());
    }


    public void test_fieldNotFound() throws Exception {
        WebContext context = loadPage(wrapHtml(
              "<form method='post' id='form'><input type='text' name='texte'/></form>"));
        context.setForm((HtmlForm)context.getHtmlPage().getHtmlElementById("form"));
        try {
            setField("unknown", "1234", context);
            fail();
        }
        catch (BuildException e) {
            assertEquals("Le composant 'unknown' n'est pas présent", e.getMessage());
        }
    }


    public void test_textTextArea() throws Exception {
        WebContext context = loadPage(wrapHtml(
              "<form method='post' id='form'><textarea cols='40' rows='10' name='texte'></textarea><input type='submit' name='gogogo'/></form>"));
        context.setForm((HtmlForm)context.getHtmlPage().getHtmlElementById("form"));
        setField("texte", "1234\nazerty", context);
        click("gogogo", context);
        assertEquals("texte = [1234\r\nazerty]\ngogogo = [Submit Query]\n",
                     context.getHtmlPage().getWebResponse().getContentAsString("ISO-8859-1"));
    }


    private void setField(String name, String value, WebContext context) throws IOException {
        SetField step = new SetField();
        step.setName(name);
        step.setValue(value);
        step.proceed(context);
    }


    private void click(String name, WebContext context) throws IOException {
        ClickSubmit step = new ClickSubmit();
        step.setName(name);
        step.proceed(context);
    }
}
