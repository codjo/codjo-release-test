package net.codjo.test.release.task.web;
/**
 *
 */
public class AssertLinkTest extends WebStepTestCase {

    public void test_no_Label_No_Text() throws Exception {
        assertLinkFailure("", null, "Vous devez spécifier au moins le texte ou target");
    }


    public void test_ok_Label() throws Exception {
        assertLink("lien", null);
    }


    public void test_nok_Label() throws Exception {
        assertLinkFailure("autre lien", null, "Aucun lien trouvé avec le texte : 'autre lien' et la cible : ''");
    }


    public void test_ok_Target() throws Exception {
        assertLink("", "target.html");
    }


    public void test_nok_Target() throws Exception {
        assertLinkFailure("", "404.html", "Aucun lien trouvé avec le texte : '' et la cible : '404.html'");
    }


    public void test_ok_Label_ok_Target() throws Exception {
        assertLink("lien", "target.html");
    }


    public void test_ok_Label_Nok_Target() throws Exception {
        assertLinkFailure("lien", "404.html",
                          "Le lien 'lien' n'a pas la bonne cible.\nAttendu : '404.html'\nObtenu : 'target.html'");
    }


    public void test_ok_notPresent() throws Exception {
        assertLink("lien", "anothertarget.html", "<a href=\"target.html\">lien</a>", false);
    }


    public void test_ok_notPresent_failure() throws Exception {
        assertLinkFailure("lien",
                          "target.html",
                          "<a href=\"target.html\">lien</a>",
                          false,
                          "Lien trouvé avec la cible : 'target.html' et le texte : 'lien'");
    }


    public void test_ok_noText_notPresent() throws Exception {
        assertLink("", "anothertarget.html", "<a href=\"target.html\">lien</a>", false);
    }


    public void test_ok_noText_notPresent_failure() throws Exception {
        assertLinkFailure("",
                          "target.html",
                          "<a href=\"target.html\">lien</a>",
                          false,
                          "Lien trouvé avec la cible : 'target.html'");
    }


    public void test_noTarget_notPresent() throws Exception {
        assertLink("anotherlien", null, "<a href=\"target.html\">lien</a>", false);
    }


    public void test_noTarget_notPresent_failure() throws Exception {
        assertLinkFailure("lien",
                          null,
                          "<a href=\"target.html\">lien</a>",
                          false,
                          "Lien trouvé avec le texte : 'lien'");
    }


    private void assertLink(String text, String target) throws Exception {
        assertLink(text, target, "<a href=\"target.html\">lien</a>", true);
    }


    private void assertLink(String text, String target, String html, boolean present) throws Exception {
        WebContext context = loadPage(wrapHtml("titre", html));
        AssertLink step = new AssertLink();
        step.addText(text);
        step.setTarget(target);
        step.setPresent(present);
        step.proceed(context);
    }


    private void assertLinkFailure(String text, String target, String expectedError) {
        assertLinkFailure(text, target, "<a href=\"target.html\">lien</a>", true, expectedError);
    }


    private void assertLinkFailure(String text,
                                   String target,
                                   String html,
                                   boolean present,
                                   String expectedError) {
        try {
            assertLink(text, target, html, present);
            fail();
        }
        catch (Exception e) {
            assertEquals(expectedError, e.getLocalizedMessage());
        }
    }
}
