package net.codjo.test.release.task.web;

import java.io.IOException;
import junit.framework.Assert;
public class ClickTest extends WebStepTestCase {

    public void test_clickButton() throws Exception {
        WebContext context =
              loadPage(wrapHtml("<div id='myDiv'onClick='alert(\"toto\");' />"));
        click("myDiv", null, context);
        Assert.assertEquals(context.getAlerts().get(0), "toto");
    }


    public void test_idNotFound() throws Exception {
        WebContext context = loadPage(wrapHtml("<div id='myDiv'/>"));
        try {
            click("unknownId", null, context);
            fail();
        }
        catch (WebException e) {
            assertEquals("Aucun élément trouvé avec l'identifiant: unknownId", e.getMessage());
        }
    }


    public void test_noErrorIfNoOnclickMethod() throws Exception {
        WebContext context = loadPage(wrapHtml("<div id='myDiv'/>"));
        click("myDiv", null, context);
        Assert.assertEquals(context.getAlerts().size(), 0);
    }


    public void test_clickButtonwithXpath() throws Exception {
        WebContext context =
              loadPage(wrapHtml("<div id='div1'/>"
                                + "<div id='div2'/>"
                                + "<div id='div3' onClick='alert(\"toto\");' />"));
        click(null, "(//div)[3]", context);
        Assert.assertEquals(context.getAlerts().get(0), "toto");
    }


    public void test_xpathNotFound() throws Exception {
        WebContext context = loadPage(wrapHtml("<div id='div1'/>"
                                               + "<div id='div2'/>"
                                               + "<div id='div3' onClick='alert(\"toto\");' />"));
        try {
            click(null, "//unknown", context);
            fail();
        }
        catch (WebException e) {
            assertEquals("Aucun élément trouvé avec l'expression xpath: //unknown", e.getMessage());
        }
    }


    private void click(String id, String xpath, WebContext context) throws IOException {
        Click step = new Click();
        step.setId(id);
        step.setXpath(xpath);
        step.proceed(context);
    }
}
