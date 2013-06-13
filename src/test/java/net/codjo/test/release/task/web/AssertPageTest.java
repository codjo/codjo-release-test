package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.WebClient;
/**
 *
 */
public class AssertPageTest extends WebStepTestCase {
    public void test_ok() throws Exception {
        checkPage("titre", "titre");
    }


    public void test_error() throws Exception {
        try {
            checkPage("titre", "other");
            fail();
        }
        catch (Exception e) {
            assertEquals("Page inattendue : other obtenu : titre", e.getMessage());
        }
    }


    public void test_errorCode() throws Exception {
        String url = getServer().getUrl("noPage.html");
        //TODO - Beware this WebClient is Internet Explorer 7 by default see WebTask.getWebContext()
        WebClient webClient = new WebClient();
        webClient.setThrowExceptionOnFailingStatusCode(false);
        WebContext context = new WebContext(webClient.getPage(url), webClient, project);

        AssertPage step = new AssertPage();
        step.setStatusCode("404");
        step.proceed(context);
    }


    public void test_errorCode_fail() throws Exception {
        String url = getServer().getUrl("noPage.html");
        //TODO - Beware this WebClient is Internet Explorer 7 by default see WebTask.getWebContext()
        WebClient webClient = new WebClient();
        webClient.setThrowExceptionOnFailingStatusCode(false);
        WebContext context = new WebContext(webClient.getPage(url), webClient, project);

        AssertPage step = new AssertPage();
        step.setStatusCode("505");

        try {
            step.proceed(context);
            fail();
        }
        catch (Exception e) {
            assertEquals("Code d'erreur inattendu : 505 obtenu : 404", e.getMessage());
        }
    }


    private void checkPage(String actualTitle, String expectedTitle) throws Exception {
        WebContext context = loadPage(wrapHtml(actualTitle, "contenu"));
        AssertPage step = new AssertPage();
        step.setTitle(expectedTitle);
        step.proceed(context);
    }
}
