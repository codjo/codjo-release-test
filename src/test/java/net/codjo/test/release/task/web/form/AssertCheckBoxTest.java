package net.codjo.test.release.task.web.form;
import net.codjo.test.release.task.web.WebException;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import java.io.IOException;
import junit.framework.AssertionFailedError;
/**
 *
 */
public class AssertCheckBoxTest extends WebFormTestCase {
    public void test_defaultCase() throws Exception {
        initForm(
              "<input type='checkbox' name='checkbox1' checked>"
              + "<input type='checkbox' name='checkbox2'>");

        assertCheckBox("checkbox1", "true");
        assertCheckBox("checkbox2", "false");
    }


    public void test_errorMessage() throws Exception {
        initForm("<input type='checkbox' name='checkbox1' checked>");
        try {
            assertCheckBox("checkbox1", "false");
            fail();
        }
        catch (AssertionFailedError e) {
            assertEquals(
                  "Etat invalide pour la checkBox 'checkbox1' du formulaire 'form' expected:<false> but was:<true>",
                  e.getMessage());
        }
    }


    public void test_checkBoxNotFound() throws Exception {
        initForm("<input type='checkbox' name='checkbox1'>");
        try {
            assertCheckBox("unknown", "true");
            fail();
        }
        catch (WebException e) {
            assertEquals("L'élément 'unknown' est introuvable dans le formulaire.", e.getMessage());
        }
    }


    public void test_notACheckBox() throws Exception {
        initForm("<input type='button' name='checkbox1'>");
        try {
            assertCheckBox("checkbox1", "true");
            fail();
        }
        catch (WebException e) {
            assertEquals("L'élément 'checkbox1' n'est pas une checkbox.", e.getMessage());
        }
    }


    private void initForm(String content) throws Exception {
        context = loadPage(wrapHtml(
              "<form method='post' id='form'>"
              + content
              + "</form>"));
        context.setForm((HtmlForm)context.getHtmlPage().getHtmlElementById("form"));
    }


    private void assertCheckBox(String name, String expectedState) throws IOException {
        AssertCheckBox checkBox = new AssertCheckBox();
        checkBox.setName(name);
        checkBox.setChecked(expectedState);
        checkBox.proceed(context);
    }
}
