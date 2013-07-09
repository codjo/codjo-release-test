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
        click("myButton", (String)null, context);
        HtmlInput destination = (HtmlInput)context.getHtmlPage().getHtmlElementById("destination");
        assertEquals("Luke!", destination.getValueAttribute());
    }


    public void test_idNotFound() throws Exception {
        WebContext context = loadPage(wrapHtml("<input id='myButton' type='button' value='Edit'/>"));
        try {
            click("unknownId", (String)null, context);
            fail();
        }
        catch (WebException e) {
            assertEquals("Aucun élément trouvé avec l'identifiant: unknownId", e.getMessage());
        }
    }


    public void test_idDoesNotReferToAButtonInput() throws Exception {
        WebContext context = loadPage(wrapHtml("<a href='target.html' id='link'>go!</a>"));
        try {
            click("link", (String)null, context);
            fail();
        }
        catch (WebException e) {
            assertEquals("L'élément 'link' n'est pas de la forme <input type='button' ...>", e.getMessage());
        }
    }


    public void test_clickButtonwithXpath() throws Exception {
        WebContext context =
              loadPage(wrapHtml("<input type='text' id='source' value='Luke!'>"
                                + "<input type='text' id='destination'>"
                                + "<input id='myButton' type='button' "
                                + "onClick='document.getElementById(\"destination\").value=document.getElementById(\"source\").value' "
                                + "value='Edit'/>"));
        click(null, "//input[@id='myButton']", context);
        HtmlInput destination = (HtmlInput)context.getHtmlPage().getHtmlElementById("destination");
        assertEquals("Luke!", destination.getValueAttribute());
    }


    public void test_xpathNotFound() throws Exception {
        WebContext context = loadPage(wrapHtml("<input id='myButton' type='button' value='Edit'/>"));
        try {
            click(null, "//unknown", context);
            fail();
        }
        catch (WebException e) {
            assertEquals("Aucun élément trouvé avec l'expression xpath: //unknown", e.getMessage());
        }
    }


    public void test_xPathDoesNotReferToAButtonInput() throws Exception {
        WebContext context = loadPage(wrapHtml("<a href='target.html' id='link'>go!</a>"));
        try {
            click(null, "//a", context);
            fail();
        }
        catch (WebException e) {
            assertEquals("L'élément '//a' n'est pas de la forme <input type='button' ...>", e.getMessage());
        }
    }

    public void test_clickButtonwithCssClass() throws Exception {
        WebContext context =
              loadPage(wrapHtml("<input type='text' id='source' value='Luke!'>"
                                + "<input type='text' id='destination' class='classOne classTwo'>"
                                + "<input id='myButton' type='button' class='classTwo classThree' "
                                + "onClick='document.getElementById(\"destination\").value=document.getElementById(\"source\").value' "
                                + "value='Edit'/>"));
        click("classTwo", 2, context);
        HtmlInput destination = (HtmlInput)context.getHtmlPage().getHtmlElementById("destination");
        assertEquals("Luke!", destination.getValueAttribute());
    }


    private void click(String id, String xpath, WebContext context) throws IOException {
        ClickButton step = new ClickButton();
        step.setId(id);
        step.setXpath(xpath);
        step.proceed(context);
    }


    private void click(String cssClass, Integer index, WebContext context) throws IOException {
        ClickButton step = new ClickButton();
        step.setCssClass(cssClass);
        step.setIndex(index);
        step.proceed(context);
    }
}
