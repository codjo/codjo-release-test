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
        assertCheckBox(context, "checkbox1", null, "true");
        assertCheckBox(context, "checkbox2", null, "false");
    }

    public void test_defaultCaseWithXpath() throws Exception {
        WebContext context = loadPage(wrapHtml(
              "  <input type='checkbox' id='checkbox1' checked>"
              + "  <input type='checkbox' id='checkbox2'>"));
        assertCheckBox(context, null, "//input[@id='checkbox1']", "true");
        assertCheckBox(context, null, "//input[@id='checkbox2']", "false");
    }


    public void test_errorMessage() throws Exception {
        WebContext context = loadPage(wrapHtml("<input type='checkbox' id='checkbox1' checked>"));
        try {
            assertCheckBox(context, "checkbox1", null, "false");
            fail();
        }
        catch (AssertionFailedError e) {
            assertEquals("Etat invalide pour la checkBox 'checkbox1' expected:<false> but was:<true>", e.getMessage());
        }
    }


    public void test_checkBoxNotFound() throws Exception {
        WebContext context = loadPage(wrapHtml("<input type='checkbox' id='checkbox1'>"));
        try {
            assertCheckBox(context, "unknown", null, "true");
            fail();
        }
        catch (WebException e) {
            assertEquals("L'élément 'unknown' est introuvable.", e.getMessage());
        }
    }

    public void test_checkBoxNotFoundWithXpath() throws Exception {
        WebContext context = loadPage(wrapHtml("<input type='checkbox' id='checkbox1'>"));
        try {
            assertCheckBox(context, null, "//unknown", "true");
            fail();
        }
        catch (WebException e) {
            assertEquals("L'élément '//unknown' est introuvable.", e.getMessage());
        }
    }

    public void test_notACheckBox() throws Exception {
        WebContext context = loadPage(wrapHtml("<input type='button' id='checkbox1'>"));
        try {
            assertCheckBox(context, "checkbox1", null, "true");
            fail();
        }
        catch (WebException e) {
            assertEquals("L'élément 'checkbox1' n'est pas une checkbox.", e.getMessage());
        }
    }

    public void test_notACheckBoxWithXpath() throws Exception {
        WebContext context = loadPage(wrapHtml("<input type='button' id='checkbox1'>"));
        try {
            assertCheckBox(context, null, "//input", "true");
            fail();
        }
        catch (WebException e) {
            assertEquals("L'élément '//input' n'est pas une checkbox.", e.getMessage());
        }
    }


    private void assertCheckBox(WebContext context, String id, String xpath, String expectedState) throws IOException {
        AssertCheckBox checkBox = new AssertCheckBox();
        checkBox.setId(id);
        checkBox.setXpath(xpath);
        checkBox.setChecked(expectedState);
        checkBox.proceed(context);
    }
}
