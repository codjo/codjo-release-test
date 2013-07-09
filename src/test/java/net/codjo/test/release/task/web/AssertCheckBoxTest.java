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
        assertCheckBox(context, "checkbox1", null, null, null, "true");
        assertCheckBox(context, "checkbox2", null, null, null, "false");
    }


    public void test_defaultCaseWithXpath() throws Exception {
        WebContext context = loadPage(wrapHtml(
              "  <input type='checkbox' id='checkbox1' checked>"
              + "  <input type='checkbox' id='checkbox2'>"));
        assertCheckBox(context, null, "//input[@id='checkbox1']", null, null, "true");
        assertCheckBox(context, null, "//input[@id='checkbox2']", null, null, "false");
    }


    public void test_defaultCaseWithCssClass() throws Exception {
        WebContext context = loadPage(wrapHtml(
              "  <input type='checkbox' class='class1' checked>"
              + "  <input type='checkbox' class='class1 class2'>"));
        assertCheckBox(context, null, null, "class1", 1, "true");
        assertCheckBox(context, null,  null,"class2", null, "false");
    }


    public void test_errorMessage() throws Exception {
        WebContext context = loadPage(wrapHtml("<input type='checkbox' id='checkbox1' checked>"));
        try {
            assertCheckBox(context, "checkbox1", null, null, null, "false");
            fail();
        }
        catch (AssertionFailedError e) {
            assertEquals("Etat invalide pour la checkBox 'checkbox1' expected:<false> but was:<true>", e.getMessage());
        }
    }


    public void test_checkBoxNotFound() throws Exception {
        WebContext context = loadPage(wrapHtml("<input type='checkbox' id='checkbox1'>"));
        try {
            assertCheckBox(context, "unknown", null, null, null, "true");
            fail();
        }
        catch (WebException e) {
            assertEquals("L'élément 'unknown' est introuvable.", e.getMessage());
        }
    }


    public void test_checkBoxNotFoundWithXpath() throws Exception {
        WebContext context = loadPage(wrapHtml("<input type='checkbox' id='checkbox1'>"));
        try {
            assertCheckBox(context, null, "//unknown", null, null, "true");
            fail();
        }
        catch (WebException e) {
            assertEquals("L'élément '//unknown' est introuvable.", e.getMessage());
        }
    }


    public void test_notACheckBox() throws Exception {
        WebContext context = loadPage(wrapHtml("<input type='button' id='checkbox1'>"));
        try {
            assertCheckBox(context, "checkbox1", null, null, null, "true");
            fail();
        }
        catch (WebException e) {
            assertEquals("L'élément 'checkbox1' n'est pas une checkbox.", e.getMessage());
        }
    }


    public void test_notACheckBoxWithXpath() throws Exception {
        WebContext context = loadPage(wrapHtml("<input type='button' id='checkbox1'>"));
        try {
            assertCheckBox(context, null, "//input", null, null, "true");
            fail();
        }
        catch (WebException e) {
            assertEquals("L'élément '//input' n'est pas une checkbox.", e.getMessage());
        }
    }


    public void test_notACheckBoxWithCssClass() throws Exception {
        WebContext context = loadPage(wrapHtml("<input type='button' id='checkbox1' class='classOne'>"));
        try {
            assertCheckBox(context, null, null, "classOne", null, "true");
            fail();
        }
        catch (WebException e) {
            assertEquals("L'élément '//*[contains(@class, 'classOne')]' n'est pas une checkbox.", e.getMessage());
        }
    }


    private void assertCheckBox(WebContext context,
                                String id,
                                String xpath,
                                String cssClass,
                                Integer index,
                                String expectedState) throws IOException {
        AssertCheckBox checkBox = new AssertCheckBox();
        checkBox.setId(id);
        checkBox.setXpath(xpath);
        checkBox.setCssClass(cssClass);
        checkBox.setIndex(index);
        checkBox.setChecked(expectedState);
        checkBox.proceed(context);
    }
}
