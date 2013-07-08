package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.util.Cookie;
import java.util.Map;
import org.apache.tools.ant.Project;
/**
 *
 */
public class WebTaskTest extends WebStepTestCase {
    private WebTask task;


    public void test_connection() throws Exception {
        task.setSession("session");
        task.open();
        task.execute();
    }


    public void test_noUrl() throws Exception {
        task.setSession("session");
        task.setUrl(null);
        try {
            task.execute();
            fail();
        }
        catch (WebException e) {
            assertEquals(WebTask.MISSING_URL, e.getLocalizedMessage());
        }
    }


    public void test_noSession() throws Exception {
        AssertPageMock stepMock = new AssertPageMock();
        task.addAssertPage(stepMock);
        task.execute();
        WebContext webContext1 = stepMock.getContext();
        task.execute();
        WebContext webContext2 = stepMock.getContext();
        assertNotSame("Les contextes doivent être différents si on ne précise pas la session", webContext1,
                      webContext2);
    }


    public void test_differentSession() throws Exception {
        AssertPageMock stepMock = new AssertPageMock();
        task.addAssertPage(stepMock);
        task.setSession("sessionName");
        task.execute();
        WebContext webContext1 = stepMock.getContext();
        webContext1.getWebClient().getCookieManager().addCookie(new Cookie("toto", "tata"));
        task.setSession("anotherSession");
        task.execute();
        WebContext webContext2 = stepMock.getContext();
        assertNotSame("Le contexte doit être différent si les sessions sont différentes", webContext1,
                      webContext2);
        assertEquals(1, webContext2.getWebClient().getCookieManager().getCookies().size());
    }


    public void test_differentSessionWithCookiePersistence() throws Exception {
        AssertPageMock stepMock = new AssertPageMock();
        task.addAssertPage(stepMock);
        task.setSession("sessionNameWithCookies");
        task.execute();
        WebContext webContext1 = stepMock.getContext();
        webContext1.getWebClient().getCookieManager().addCookie(new Cookie("toto", "tata"));

        task.setSession("anotherSessionWithCookies");
        task.execute();
        WebContext webContext2 = stepMock.getContext();
        assertNotSame("Le contexte doit être différent si les sessions sont différentes", webContext1,
                      webContext2);
        assertEquals("La liste des cookies doit etre maintenue entre deux sessions differentes",
                     "tata", webContext2.getWebClient().getCookieManager().getCookie("toto").getValue());
    }


    public void test_sessionReuse() throws Exception {
        AssertPageMock stepMock = new AssertPageMock();
        task.addAssertPage(stepMock);
        task.setSession("sessionName");
        task.execute();
        WebContext webContext1 = stepMock.getContext();

        task.execute();
        WebContext webContext2 = stepMock.getContext();
        assertSame("Le contexte doit être conservé si on précise la session", webContext1, webContext2);
    }


    public void test_noURLDontFail() throws Exception {
        task.setFailOnError(false);
        task.setUrl(getServer().getUrl("noPage.html"));

        task.execute();
    }


    public void test_requestHeader() throws Exception {
        AssertPageMock stepMock = new AssertPageMock();
        task.addAssertPage(stepMock);
        task.setRequestHeader("header1=value1;header2=2");

        task.execute();

        Map<String, String> headers = getServer().getHeaders();
        assertTrue(2 < headers.size());
        assertEquals("value1", headers.get("header1"));
        assertEquals("2", headers.get("header2"));
    }


    public void test_allowAllCertificates() throws Exception {
        //NB : test light des accesseurs : TU avec jetty en SSL à implementer
        AssertPageMock stepMock = new AssertPageMock();
        task.addAssertPage(stepMock);
        assertTrue(task.isAllowAllCertificates());
        task.execute();

        task.setAllowAllCertificates(false);
        assertFalse(task.isAllowAllCertificates());
        task.execute();
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        task = new WebTask();
        task.setProject(new Project());
        task.open();
        addPage("page.html", wrapHtml("Page title", "Page content"));
        task.setUrl(getServer().getUrl("page.html"));
    }


    @Override
    protected void tearDown() throws Exception {
        task.close();
        super.tearDown();
    }


    private static class AssertPageMock extends AssertPage {
        private WebContext myContext;


        @Override
        public void proceed(WebContext context) {
            myContext = context;
        }


        public WebContext getContext() {
            return myContext;
        }
    }
}
