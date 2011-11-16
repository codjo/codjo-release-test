package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import java.io.IOException;
/**
 *
 */
public class ClickButtonTest extends WebStepTestCase {

    public void test_clickButton() throws Exception {
        WebContext context =
              loadPage(wrapHtml("<input type='text' id='source' value='Luke!'>"
                                + "<input type='text' id='destination'>"
                                + "<input id='myButton' type='button' "
                                + "onClick='document.getElementById(\"destination\").value=document.getElementById(\"source\").value' "
                                + "value='Edit'/>"));
        click("myButton", context);
        HtmlInput destination = (HtmlInput)context.getHtmlPage().getHtmlElementById("destination");
        assertEquals("Luke!", destination.getValueAttribute());
    }


    public void test_idNotFound() throws Exception {
        WebContext context = loadPage(wrapHtml("<input id='myButton' type='button' value='Edit'/>"));
        try {
            click("unknownId", context);
            fail();
        }
        catch (WebException e) {
            assertEquals("Aucun élément trouvé avec l'identifiant: unknownId", e.getMessage());
        }
    }


    public void test_idDoesNotReferToAButtonInput() throws Exception {
        WebContext context = loadPage(wrapHtml("<a href='target.html' id='link'>go!</a>"));
        try {
            click("link", context);
            fail();
        }
        catch (WebException e) {
            assertEquals("L'élément 'link' n'est pas de la forme <input type='button' ...>", e.getMessage());
        }
    }


    private void click(String id, WebContext context) throws IOException {
        ClickButton step = new ClickButton();
        step.setId(id);
        step.proceed(context);
    }
}
