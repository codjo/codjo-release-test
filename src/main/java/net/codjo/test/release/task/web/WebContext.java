package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.codjo.test.release.task.util.TestLocation;
import org.apache.tools.ant.Project;
/**
 *
 */
public class WebContext {
    private static final String TEST_LOCATION = "testLocation";
    private HtmlPage page;
    private HtmlForm form;
    private WebClient webClient;
    private final Project project;
    private List<String> collectedAlerts = new ArrayList<String>();
    private final int statuscode;
    private Map<Object,Object> objects = new HashMap<Object,Object>();


    public WebContext(Page page, WebClient webClient, Project project) {
        if (page instanceof HtmlPage) {
            this.page = (HtmlPage)page;
        }
        this.statuscode = page.getWebResponse().getStatusCode();
        this.webClient = webClient;
        this.project = project;
        webClient.setAlertHandler(new CollectingAlertHandler(collectedAlerts));
        putObject(TEST_LOCATION, new TestLocation());
    }


    public HtmlPage getHtmlPage() {
        return page;
    }


    public void setPage(String url) throws IOException {
        try {
            setPage((HtmlPage)webClient.getPage(url));
        }
        catch (UnknownHostException e) {
            throw new IOException("Impossible de se connecter a l'adresse : " + url);
        }
    }


    public void setPage(HtmlPage page) {
        this.page = page;
    }


    public void setForm(HtmlForm form) {
        this.form = form;
    }


    public HtmlForm getForm() {
        return form;
    }


    public WebClient getWebClient() {
        return webClient;
    }


    public boolean popAlert(String message) {
        return collectedAlerts.remove(message);
    }


    public List<String> getAlerts() {
        return Collections.unmodifiableList(collectedAlerts);
    }


    public String replaceProperties(String text) {
        if (project == null) {
            return text;
        }

        return project.replaceProperties(text);
    }


    public String getProperty(String name) {
        return project.getProperty(name);
    }


    public int getStatusCode() {
        return statuscode;
    }


    public TestLocation getTestLocation() {
        return (TestLocation)getObject(TEST_LOCATION);
    }


    private void putObject(Object key, Object value) {
        objects.put(key, value);
    }


    private Object getObject(Object key) {
        return objects.get(key);
    }
}
