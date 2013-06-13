package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.WebClient;
import junit.framework.TestCase;
import net.codjo.test.release.test.web.DummyWebServer;
import org.apache.tools.ant.Project;
/**
 *
 */
public abstract class WebStepTestCase extends TestCase {
    private DummyWebServer server;
    protected Project project = new Project();


    protected void addPage(String path, String content) throws Exception {
        getServer().addPage(path, content);
    }


    protected WebContext loadPage(String content) throws Exception {
        server = getServer();
        String path = getName() + ".html";
        server.addPage(path, content);
        //TODO - Beware this WebClient is Internet Explorer 7 by default see WebTask.getWebContext()
        WebClient webClient = new WebClient();
        String url = server.getUrl(path);
        return new WebContext(webClient.getPage(url), webClient, project);
    }


    protected DummyWebServer getServer() throws Exception {
        if (server == null) {
            server = new DummyWebServer();
            server.start();
        }
        return server;
    }


    protected String wrapHtml(String content) {
        return wrapHtml("", content);
    }


    protected String wrapHtml(String title, String content) {
        return "<html>"
               + "<head>"
               + "  <title>" + title + "</title>"
               + "</head>"
               + "<body>"
               + content
               + "</body>"
               + "</html>";
    }


    @Override
    protected void tearDown() throws Exception {
        if (server != null) {
            server.stop();
        }
        super.tearDown();
    }


    protected String getUrl(String path) {
        return server.getUrl(path);
    }
}
