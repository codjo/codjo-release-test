package net.codjo.test.release.task.web;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
/**
 *
 */
public class ClickLinkTest extends WebStepTestCase {
    public void test_clickWithText() throws Exception {
        addPage("target.html", wrapHtml("Target title", "Target content"));
        WebContext context = loadPage(wrapHtml("<a href='target.html'>go!</a>"));
        click("go!", null, context);
        assertEquals("Target title", context.getHtmlPage().getTitleText());
    }


    public void test_clickWithId() throws Exception {
        addPage("target.html", wrapHtml("Target title", "Target content"));
        WebContext context = loadPage(wrapHtml("<a href='target.html' id='link'>go!</a>"));
        click(null, "link", context);
        assertEquals("Target title", context.getHtmlPage().getTitleText());
    }


    public void test_idAndTextSet() throws Exception {
        WebContext context = loadPage(wrapHtml("<a href='target.html'>go!</a>"));
        try {
            click("a", "b", context);
            fail();
        }
        catch (BuildException e) {
            assertEquals("Les champs 'text' et 'id' ne doivent pas être utilisés en même temps",
                         e.getMessage());
        }
    }


    public void test_clickWithTextWithinASpan() throws Exception {
        addPage("target.html", wrapHtml("Target title", "Target content"));
        WebContext context = loadPage(wrapHtml("<a href='target.html' id='link'><span>go!</span></a>"));
        click("go!", null, context);
        assertEquals("Target title", context.getHtmlPage().getTitleText());
    }


    public void test_idAndTextNotSet() throws Exception {
        WebContext context = loadPage(wrapHtml("<a href='target.html'>go!</a>"));
        try {
            click(null, null, context);
            fail();
        }
        catch (BuildException e) {
            assertEquals("Le champ 'text' ou 'id' doit être spécifié", e.getMessage());
        }
    }


    public void test_textNotFound() throws Exception {
        WebContext context = loadPage(wrapHtml("<a href='target.html'>go!</a>"));
        try {
            click("unknown text", null, context);
            fail();
        }
        catch (BuildException e) {
            assertEquals("Aucun lien trouvé avec le texte: unknown text", e.getMessage());
        }
    }


    public void test_idNotFound() throws Exception {
        WebContext context = loadPage(wrapHtml("<a href='target.html'>go!</a>"));
        try {
            click(null, "unknown", context);
            fail();
        }
        catch (BuildException e) {
            assertEquals("Aucun lien trouvé avec l'identifiant: unknown", e.getMessage());
        }
    }


    public void test_idDoesNotReferToAnAnchor() throws Exception {
        WebContext context = loadPage(wrapHtml("<table id='blah'></table>"));
        try {
            click(null, "blah", context);
            fail();
        }
        catch (BuildException e) {
            assertEquals("L'élément 'blah' n'est pas un lien <a ...>", e.getMessage());
        }
    }


    public void test_unknownLinkTarget() throws Exception {
        WebContext context = loadPage(wrapHtml("<a href='unknown.html'>go!</a>"));
        try {
            click("go!", null, context);
            fail();
        }
        catch (BuildException e) {
            assertEquals(
                  "Erreur lors du click sur le lien 'go!' : 404 Not Found for http://localhost:8181/unknown.html",
                  e.getMessage());
        }
    }


    public void test_withProperties() throws Exception {
        project.setProperty("linkName", "Nom du lien");
        addPage("target.html", wrapHtml("Target title", "Target content"));
        WebContext context = loadPage(wrapHtml("<a href='target.html'>Nom du lien</a>"));
        click("${linkName}", null, context);
        assertEquals("Target title", context.getHtmlPage().getTitleText());
    }


    private void click(String text, String id, WebContext context) throws IOException {
        ClickLink step = new ClickLink();
        step.setText(text);
        step.setId(id);
        step.proceed(context);
    }
}
