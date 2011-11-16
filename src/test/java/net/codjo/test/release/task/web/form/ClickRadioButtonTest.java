package net.codjo.test.release.task.web.form;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebStepTestCase;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import java.io.IOException;
/**
 *
 */
public class ClickRadioButtonTest extends WebStepTestCase {

    public void test_radioButtonNotFound() throws Exception {
        WebContext context = loadPage(wrapHtml(
              "<form method='post' id='form'>"
              + "<input type='radio' id='radioGroup_FCPAAAMViewer' value='value1' checked='checked' name='radioGroupgroup'/>"
              + "<input type='radio' id='radioGroup_FCPAAAMViewer' value='value2' checked='checked' name='radiodioGroupgroup'/>"
              + "</form>"));
        context.setForm((HtmlForm)context.getHtmlPage().getHtmlElementById("form"));
        try {
            click("radioGroup", context, "value1");
            fail();
        }
        catch (Exception e) {
            assertEquals("L'élément 'radioGroup' est introuvable dans le formulaire.", e.getMessage());
        }
    }


    private void click(String name, WebContext context, String value) throws IOException {
        ClickRadioButton step = new ClickRadioButton();
        step.setName(name);
        step.setValue(value);
        step.proceed(context);
    }
}
