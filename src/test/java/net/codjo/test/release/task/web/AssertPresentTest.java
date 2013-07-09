package net.codjo.test.release.task.web;
import org.junit.Test;
/**
 *
 */
public class AssertPresentTest extends WebStepTestCase {

    @Test
    public void test_checkParameters() throws Exception {
        WebContext context = loadPage(wrapHtml("title", "<br>"));
        AssertPresent step = new AssertPresent();
        step.setXpath("//span");
        step.setId("container");

        try {
            step.proceed(context);
            fail();
        }
        catch (WebException e) {
            assertEquals("Les champs 'id' et 'xpath' ne doivent pas être utilisés en même temps", e.getMessage());
        }
    }


    public void test_ok() throws Exception {
        checkPage("<span id='container'/>", "container", true);
    }


    public void test_error() throws Exception {
        try {
            checkPage("<span id='container'/>", "unknown", true);
            fail();
        }
        catch (Exception e) {
            assertEquals("L'element 'unknown' n'est pas present dans la page", e.getMessage());
        }
    }


    public void test_notPresent() throws Exception {
        checkPage("<span id='container' present='false'/>", "unknown", false);
    }


    public void test_notPresent_error() throws Exception {
        try {
            checkPage("<span id='container' present='false'/>", "container", false);
            fail();
        }
        catch (Exception e) {
            assertEquals("L'element 'container' est present dans la page", e.getMessage());
        }
    }


    public void test_ok_withXpath() throws Exception {
        checkPageWithXpath("<span id='container'/>", "//span[@id='container']", true);
    }

    public void test_ok_withCssClass() throws Exception {
        checkPageWithCssClass("<span class='class1'/><span class='class1'/>", "class1",2, true);
    }


    public void test_foundButAmbigous() {
        try {
            checkPageWithXpath("<span id='container'/><span id='container_two'/>", "//span", true);
            fail();
        }
        catch (Exception e) {
            assertEquals("Ambiguité, plusieurs éléments ont été trouvé avec l'expression xpath:'//span'\n"
                         + "\tobject : HtmlSpan[<span id=\"container\">]\n"
                         + "\tobject : HtmlSpan[<span id=\"container_two\">]\n", e.getMessage());
        }
    }


    public void test_error_withXpath() throws Exception {
        try {
            checkPageWithXpath("<span id='container'/>", "//unknown", true);
            fail();
        }
        catch (Exception e) {
            assertEquals("L'element '//unknown' n'est pas present dans la page", e.getMessage());
        }
    }


    public void test_notPresent_withXpath() throws Exception {
        checkPageWithXpath("<span id='container' present='false'/>", "unknown", false);
    }


    public void test_notPresent_error_withXpath() throws Exception {
        try {
            checkPageWithXpath("<span id='container' present='false'/>", "//span[@id='container']", false);
            fail();
        }
        catch (Exception e) {
            assertEquals("L'element '//span[@id='container']' est present dans la page", e.getMessage());
        }
    }


    private void checkPage(String content, String id, boolean isPresent) throws Exception {
        WebContext context = loadPage(wrapHtml("title", content));
        AssertPresent step = new AssertPresent();
        step.setId(id);
        step.setPresent(isPresent);
        step.proceed(context);
    }


    private void checkPageWithXpath(String content, String xpath, boolean isPresent) throws Exception {
        WebContext context = loadPage(wrapHtml("title", content));
        AssertPresent step = new AssertPresent();
        step.setXpath(xpath);
        step.setPresent(isPresent);
        step.proceed(context);
    }


    private void checkPageWithCssClass(String content, String cssClass, Integer index, boolean isPresent)
          throws Exception {
        WebContext context = loadPage(wrapHtml("title", content));
        AssertPresent step = new AssertPresent();
        step.setCssClass(cssClass);
        step.setIndex(index);
        step.setPresent(isPresent);
        step.proceed(context);
    }
}
