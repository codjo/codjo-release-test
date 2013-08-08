package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import net.codjo.test.release.task.Util;
import net.codjo.test.release.task.util.TestLocation;
import org.apache.tools.ant.Project;
import org.mockito.Mockito;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
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


    public void test_ioException() throws Exception {
        Click mockedClickStep = Mockito.mock(Click.class);

        WebContext context = Mockito.mock(WebContext.class);
        final TestLocation testLocation = new TestLocation();
        Mockito.when(context.getTestLocation()).thenReturn(testLocation);

        Mockito.doThrow(new IOException("Mocked IOException")).when(mockedClickStep).proceed(Mockito.<WebContext>any());

        task.addClick(mockedClickStep);
        try {
            task.execute();
            fail();
        }
        catch (Exception e) {
            assertThat(e.getMessage(),
                       is("\n\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n"
                          + "Erreur web-test Step 1\n"
                          + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n"
                          + "Impossible d'ouvrir la page : Mocked IOException"));
        }
    }


    public void test_throwableButNotIOexception() throws Exception {
        WebContext context = mockWebcontext();
        configureTaskWithMockContext(context);

        Group mainGroup = new Group();
        mainGroup.setName("mainGroup");
        mainGroup.setEnabled(true);
        Click clickInMainGroup = Mockito.mock(Click.class);
        mainGroup.addClick(clickInMainGroup);

        Group subGroup = new Group();
        subGroup.setName("subGroup");
        AssertPage assertPage = Mockito.mock(AssertPage.class);
        subGroup.addAssertPage(assertPage);
        Click clickInSubGroup = Mockito.mock(Click.class);
        subGroup.addClick(clickInSubGroup);
        mainGroup.addGroup(subGroup);

        Mockito.doThrow(new IllegalArgumentException("Everything but not IOException"))
              .when(clickInSubGroup)
              .proceed(context);

        task.addGroup(mainGroup);
        try {
            task.execute();
            fail();
        }
        catch (Exception e) {
            assertThat(e.getMessage(),
                       is("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n"
                          + "Spool page: http://localhost/page\n"
                          + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n\n\n"
                          + "<html><head><title>Page title</title></head><body>Page content</body></html>\n\n"
                          + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n"
                          + "Erreur web-test Session 'firstSession' Step 2 du groupe 'mainGroup > subGroup' ("
                          + Util.computeClassName(clickInSubGroup.getClass()) + ")\n"
                          + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n"
                          + "Everything but not IOException"));
        }
    }


    public void test_throwableAndNotInAGroup() throws Exception {
        WebContext context = mockWebcontext();
        configureTaskWithMockContext(context);

        task.addClick(Mockito.mock(Click.class));

        Group mainGroup = new Group();
        mainGroup.setName("mainGroup");
        mainGroup.setEnabled(true);
        DragAndDrop clickInMainGroup = Mockito.mock(DragAndDrop.class);
        mainGroup.addDragAndDrop(clickInMainGroup);
        task.addGroup(mainGroup);

        AssertPage assertPage = Mockito.mock(AssertPage.class);
        task.addAssertPage(assertPage);

        task.addClick(Mockito.mock(Click.class));

        Mockito.doThrow(new IllegalArgumentException("Everything but not IOException"))
              .when(assertPage)
              .proceed(context);

        try {
            task.execute();
            fail();
        }
        catch (Exception e) {
            assertThat(e.getMessage(),
                       is("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n"
                          + "Spool page: http://localhost/page\n"
                          + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n\n\n"
                          + "<html><head><title>Page title</title></head><body>Page content</body></html>\n\n"
                          + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n"
                          + "Erreur web-test Session 'firstSession' Step 3\n"
                          + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n"
                          + "Everything but not IOException"));
        }
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


    private void configureTaskWithMockContext(WebContext context) {
        task.setSession("firstSession");
        Map<String, WebContext> references = new HashMap<String, WebContext>();
        references.put("firstSession", context);
        task.getProject().addReference("web.sessions", references);
    }


    private WebContext mockWebcontext() throws MalformedURLException, URISyntaxException {
        WebContext context = Mockito.mock(WebContext.class);
        final TestLocation testLocation = new TestLocation();
        Mockito.when(context.getTestLocation()).thenReturn(testLocation);

        HtmlPage page = Mockito.mock(HtmlPage.class);
        WebResponse response = Mockito.mock(WebResponse.class);
        Mockito.when(response.getContentAsString())
              .thenReturn("<html><head><title>Page title</title></head><body>Page content</body></html>");
        Mockito.when(response.getWebRequest()).thenReturn(new WebRequest(new URI("http://localhost/page").toURL()));
        Mockito.when(page.getWebResponse()).thenReturn(response);
        Mockito.when(context.getHtmlPage()).thenReturn(page);
        return context;
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
