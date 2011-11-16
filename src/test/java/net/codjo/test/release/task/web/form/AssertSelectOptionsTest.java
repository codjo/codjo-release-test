package net.codjo.test.release.task.web.form;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebException;
import net.codjo.test.release.task.web.WebStepTestCase;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import java.io.IOException;
/**
 *
 */
public class AssertSelectOptionsTest extends WebStepTestCase {

    public void test_ok() throws Exception {
        WebContext context = loadPage(wrapHtml(
              "<form method='post' id='form'><select name='coin'>"
              + "<option id=1>Coucou</option>"
              + "<option id=2>Ca va ?</option>"
              + "<option id=3>Ouais et toi ?</option>"
              + "<option id=4>ça roule</option>"
              + "</select></form>"));
        context.setForm((HtmlForm)context.getHtmlPage().getHtmlElementById("form"));
        assertSelectOptions(context, "coin", "Coucou", "Ca va ?", "Ouais et toi ?", "ça roule");
    }


    public void test_ko() throws Exception {
        WebContext context = loadPage(wrapHtml(
              "<form method='post' id='form'><select name='coin'>"
              + "<option id=1>Coucou</option>"
              + "<option id=4>ça roule</option>"
              + "</select></form>"));
        context.setForm((HtmlForm)context.getHtmlPage().getHtmlElementById("form"));
        try {
            assertSelectOptions(context, "coin", "Coucou", "Ca va ?", "Ouais et toi ?", "ça roule");
            fail();
        }
        catch (WebException e) {
            assertEquals("Les options diffèrent de ce qui est attendu :\n"
                         + "Attendu(4) : [Coucou, Ca va ?, Ouais et toi ?, ça roule]\n"
                         + "Obtenu(2) : [Coucou, ça roule]", e.getMessage());
        }
    }


    private void assertSelectOptions(WebContext context, String name, String... values) throws IOException {
        AssertSelectOptions step = new AssertSelectOptions();
        step.setName(name);
        for (String value : values) {
            Option option = new Option();
            option.addText(value);
            step.addOption(option);
        }
        step.proceed(context);
    }
}
