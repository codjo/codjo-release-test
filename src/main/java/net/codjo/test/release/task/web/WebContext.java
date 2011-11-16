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
import java.util.List;
import org.apache.tools.ant.Project;
/**
 *
 */
public class WebContext {
    private HtmlPage page;
    private HtmlForm form;
    private WebClient webClient;
    private final Project project;
    private List<String> collectedAlerts = new ArrayList<String>();
    private final int statuscode;


    public WebContext(Page page, WebClient webClient, Project project) {
        if (page instanceof HtmlPage) {
            this.page = (HtmlPage)page;
        }
        this.statuscode = page.getWebResponse().getStatusCode();
        this.webClient = webClient;
        this.project = project;
        webClient.setAlertHandler(new CollectingAlertHandler(collectedAlerts));
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
}
