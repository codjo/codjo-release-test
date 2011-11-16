package net.codjo.test.release.task.web.form;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebException;
import net.codjo.test.release.task.web.WebStepTestCase;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import java.io.IOException;
/**
 *
 */
public class SelectTest extends WebStepTestCase {

    public void test_ok() throws Exception {
        WebContext context = loadPage(wrapHtml(
              "<form method='post' id='form'>"
              + "<select name='coin'>"
              + "<option value=1>Coucou</option>"
              + "<option value=2>Ca va ?</option>"
              + "<option value=3>Ouais et toi ?</option>"
              + "</select>"
              + "<input type='submit' value='go'/>"
              + "</form>"));
        context.setForm((HtmlForm)context.getHtmlPage().getHtmlElementById("form"));
        select("coin", "Ca va ?", context);
        click("go", context);
        assertEquals("coin = [2]\n", context.getHtmlPage().getWebResponse().getContentAsString());
    }


    public void test_ko() throws Exception {
        WebContext context = loadPage(wrapHtml(
              "<form method='post' id='form'>"
              + "<select name='coin'>"
              + "<option value=1>A</option>"
              + "<option value=2>B</option>"
              + "<option value=3>C</option>"
              + "</select>"
              + "<input type='submit' value='go'/>"
              + "</form>"));
        context.setForm((HtmlForm)context.getHtmlPage().getHtmlElementById("form"));
        try {
            select("coin", "Inconnu", context);
            fail();
        }
        catch (WebException e) {
            assertEquals(
                  "L'option 'Inconnu' n'est pas disponible pour le select 'coin'.\nLes options possibles sont : [A, B, C].",
                  e.getMessage());
        }
    }


    public void test_selectNotFound() throws Exception {
        WebContext context = loadPage(wrapHtml(
              "<form method='post' id='form'>"
              + "<select name='coin'>"
              + "</select>"
              + "<input type='submit' value='go'/>"
              + "</form>"));
        context.setForm((HtmlForm)context.getHtmlPage().getHtmlElementById("form"));
        try {
            select("Inconnu", "123", context);
            fail();
        }
        catch (WebException e) {
            assertEquals("Le select 'Inconnu' n'existe pas dans le formulaire.", e.getMessage());
        }
    }


    private void select(String name, String value, WebContext context) throws IOException {
        Select step = new Select();
        step.setValue(value);
        step.setName(name);
        step.proceed(context);
    }


    private void click(String name, WebContext context) throws IOException {
        ClickSubmit step = new ClickSubmit();
        step.setValue(name);
        step.proceed(context);
    }
}
