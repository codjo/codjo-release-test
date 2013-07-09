package net.codjo.test.release.task.web;

import java.io.IOException;
import junit.framework.Assert;
public class ClickTest extends WebStepTestCase {

    public void test_clickButton() throws Exception {
        WebContext context =
              loadPage(wrapHtml("<div id='myDiv'onClick='alert(\"toto\");' />"));
        click("myDiv", null, null, null, context);
        Assert.assertEquals(context.getAlerts().get(0), "toto");
    }


    public void test_idNotFound() throws Exception {
        WebContext context = loadPage(wrapHtml("<div id='myDiv'/>"));
        try {
            click("unknownId", null, null, null, context);
            fail();
        }
        catch (WebException e) {
            assertEquals("Aucun élément trouvé avec l'identifiant: unknownId", e.getMessage());
        }
    }


    public void test_noErrorIfNoOnclickMethod() throws Exception {
        WebContext context = loadPage(wrapHtml("<div id='myDiv'/>"));
        click("myDiv", null, null, null, context);
        Assert.assertEquals(context.getAlerts().size(), 0);
    }


    public void test_clickButtonwithXpath() throws Exception {
        WebContext context =
              loadPage(wrapHtml("<div id='div1'/>"
                                + "<div id='div2'/>"
                                + "<div id='div3' onClick='alert(\"toto\");' />"));
        click(null, "(//div)[3]", null, null, context);
        Assert.assertEquals(context.getAlerts().get(0), "toto");
    }


    public void test_xpathNotFound() throws Exception {
        WebContext context = loadPage(wrapHtml("<div id='div1'/>"
                                               + "<div id='div2'/>"
                                               + "<div id='div3' onClick='alert(\"toto\");' />"));
        try {
            click(null, "//unknown", null, null, context);
            fail();
        }
        catch (WebException e) {
            assertEquals("Aucun élément trouvé avec l'expression xpath: //unknown", e.getMessage());
        }
    }


    public void test_clickButtonwithCssClass() throws Exception {
        WebContext context =
              loadPage(wrapHtml("<div id='div1' class='classOne classeThree'/>"
                                + "<div id='div2' class='classOne classTwo'/>"
                                + "<div id='div3' onClick='alert(\"toto\");' class='classOne classFour'/>"));
        click(null, null, "classFour", null, context);
        Assert.assertEquals(context.getAlerts().get(0), "toto");
    }


    public void test_clickButtonwithCssClassAndIndex() throws Exception {
        WebContext context =
              loadPage(wrapHtml("<div id='div1' class='classOne classeThree'/>"
                                + "<div id='div2' class='classOne classTwo'/>"
                                + "<div id='div3' onClick='alert(\"toto\");' class='classOne classTwo classeThree'/>"));
        click(null, null, "classTwo", 2, context);
        Assert.assertEquals(context.getAlerts().get(0), "toto");
    }


    public void test_clickButtonwithCssClassAndAmbiguity() throws Exception {
        WebContext context =
              loadPage(wrapHtml("<div id='div1' class='classOne classeThree'/>"
                                + "<div id='div2' class='classOne classTwo'/>"
                                + "<div id='div3' onClick='alert(\"toto\");' class='classOne classFour'/>"));
        try {
            click(null, null, "classOne", null, context);
            fail();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void click(String id, String xpath, String cssClass, Integer index, WebContext context) throws IOException {
        Click step = new Click();
        step.setId(id);
        step.setXpath(xpath);
        step.setCssClass(cssClass);
        step.setIndex(index);
        step.proceed(context);
    }
}
