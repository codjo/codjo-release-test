package net.codjo.test.release.task.web;
import java.io.IOException;
import junit.framework.AssertionFailedError;
/**
 *
 */
public class AssertCheckBoxTest extends WebStepTestCase {
    public void test_defaultCase() throws Exception {
        WebContext context = loadPage(wrapHtml(
              "  <input type='checkbox' id='checkbox1' checked>"
              + "  <input type='checkbox' id='checkbox2'>"));
        assertCheckBox(context, "checkbox1", "true");
        assertCheckBox(context, "checkbox2", "false");
    }


    public void test_errorMessage() throws Exception {
        WebContext context = loadPage(wrapHtml("<input type='checkbox' id='checkbox1' checked>"));
        try {
            assertCheckBox(context, "checkbox1", "false");
            fail();
        }
        catch (AssertionFailedError e) {
            assertEquals("Etat invalide pour la checkBox 'checkbox1' expected:<false> but was:<true>", e.getMessage());
        }
    }


    public void test_checkBoxNotFound() throws Exception {
        WebContext context = loadPage(wrapHtml("<input type='checkbox' id='checkbox1'>"));
        try {
            assertCheckBox(context, "unknown", "true");
            fail();
        }
        catch (WebException e) {
            assertEquals("L'élément 'unknown' est introuvable.", e.getMessage());
        }
    }

    public void test_notACheckBox() throws Exception {
        WebContext context = loadPage(wrapHtml("<input type='button' id='checkbox1'>"));
        try {
            assertCheckBox(context, "checkbox1", "true");
            fail();
        }
        catch (WebException e) {
            assertEquals("L'élément 'checkbox1' n'est pas une checkbox.", e.getMessage());
        }
    }


    private void assertCheckBox(WebContext context, String id, String expectedState) throws IOException {
        AssertCheckBox checkBox = new AssertCheckBox();
        checkBox.setId(id);
        checkBox.setChecked(expectedState);
        checkBox.proceed(context);
    }
}
