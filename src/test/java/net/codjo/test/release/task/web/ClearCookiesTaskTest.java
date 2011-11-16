package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.apache.tools.ant.Project;
import org.junit.Test;
/**
 *
 */
public class ClearCookiesTaskTest extends WebStepTestCase {
    private ClearCookiesTask clearCookieTask;
    private WebTask webTask = new WebTask();


    @Override
    protected void setUp() throws Exception {
        clearCookieTask = new ClearCookiesTask();
        Project antProject = new Project();
        clearCookieTask.setProject(antProject);
        addPage("page.html", wrapHtml("Page title", "Page content"));

        webTask.setProject(antProject);
        webTask.open();
        webTask.setUrl(getServer().getUrl("page.html"));
    }


    @Override
    protected void tearDown() throws Exception {
        webTask.close();
        super.tearDown();
    }


    @Test
    public void test_clearCookieTask() throws Exception {
        AssertPageMock stepMock = new AssertPageMock();
        webTask.addAssertPage(stepMock);
        
        webTask.setSession("sessionName");
        webTask.execute();
        WebContext webContext1 = stepMock.getContext();
        webContext1.getWebClient().getCookieManager().addCookie(new Cookie("toto", "tata"));

        webTask.setSession("anotherSessionWithCookies");
        webTask.execute();
        WebContext webContext2 = stepMock.getContext();
        assertNotSame("Le contexte doit être différent si les sessions sont différentes", webContext1,
                      webContext2);
        assertEquals("La liste des cookies doit etre maintenue entre deux sessions differentes",
                     "tata", webContext2.getWebClient().getCookieManager().getCookie("toto").getValue());

        clearCookieTask.execute();

        webTask.setSession("anotherSessionWithoutCookies");
        webTask.execute();
        WebContext webContext3 = stepMock.getContext();
        assertTrue("La liste des cookies doit ne doit pas etre maintenue entre deux sessions differentes",
                   webContext3.getWebClient().getCookieManager().getCookies().isEmpty());
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
