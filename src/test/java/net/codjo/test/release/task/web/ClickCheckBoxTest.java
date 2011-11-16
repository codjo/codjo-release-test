package net.codjo.test.release.task.web;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
/**
 *
 */
public class ClickCheckBoxTest extends WebStepTestCase {

    public void test_checkbox() throws Exception {
        WebContext context = loadPage(wrapHtml("<input type='checkbox' id='boite'/>"));
        assertEquals("unchecked", context.getHtmlPage().asText());

        click("boite", context);
        assertEquals("checked", context.getHtmlPage().asText());
    }


    public void test_checkboxNotFound() throws Exception {
        WebContext context = loadPage(wrapHtml("<input type='checkbox' id='boiboite'/>"));
        try {
            click("boite", context);
            fail();
        }
        catch (BuildException e) {
            assertEquals("L'élément 'boite' est introuvable.", e.getMessage());
        }
    }


    public void test_notACheckbox() throws Exception {
        WebContext context = loadPage(wrapHtml("<input type='text' id='boite'/>"));
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
        step.setId(name);
        step.proceed(context);
    }
}
