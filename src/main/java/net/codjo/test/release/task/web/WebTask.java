package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.codjo.test.release.task.AgfTask;
import net.codjo.test.release.task.Resource;
import net.codjo.test.release.task.web.dialogs.AssertAlert;
import net.codjo.test.release.task.web.dialogs.SetConfirmation;
import net.codjo.test.release.task.web.form.EditForm;
/**
 *
 */
@SuppressWarnings({"OverlyCoupledClass"})
public class WebTask extends AgfTask implements Resource {
    public static final String COOKIE_MANAGER = "cookieManager";
    private String session;
    private boolean failOnError = true;
    private List<WebStep> steps = new ArrayList<WebStep>();
    private static final String WEB_SESSIONS = "web.sessions";
    private String url;
    private boolean javascript = true;
    static final String MISSING_URL
          = "L'attribut 'url' est obligatoire pour le premier web-test d'une session.";
    static final String MISSING_SESSION = "L'attribut 'session' est obligatoire.";
    static final String CANNOT_OPEN_PAGE = "Impossible d'ouvrir la page : ";
    static final String CANNOT_IGNORE_SSL_SECURE = "Impossible de positionner la propriété allowAllCertificates ";
    private String requestHeader;
    private boolean allowAllCertificates = true;


    @Override
    public void execute() {
        int stepNum = 0;
        WebContext context = getWebContext();
        try {
            for (stepNum = 0; stepNum < steps.size(); stepNum++) {
                steps.get(stepNum).proceed(context);
            }
        }
        catch (IOException e) {
            throw new WebException(
                  "Erreur web-test '" + getSession() + "' step " + (stepNum + 1) + " :\n" + CANNOT_OPEN_PAGE
                  + e.getMessage(), e);
        }
        catch (Throwable t) {
            throw new WebException(
                  "Erreur web-test '" + getSession() + "' step " + (stepNum + 1) + " :\n" + t.getMessage()
                  + "\n" + getPageDescription(context),
                  t);
        }
    }


    private String getPageDescription(WebContext context) {
        if (context == null) {
            return "";
        }

        Page page = context.getHtmlPage();
        if (page == null) {
            return "(aucune page)";
        }

        WebResponse response = page.getWebResponse();
        return response.getWebRequest().getUrl() + "\n\n" + response.getContentAsString();
    }


    private WebContext getWebContext() {
        WebContext context = session == null ? null : getSessions().get(session);
        if (context == null) {

            // En attente d'une correction sur la gestion de Microsoft.XmlDom, certainement
            // pour htmlunit 1.14
            BrowserVersion.setDefault(BrowserVersion.FIREFOX_2);

            WebClient client = new WebClient();
            // experimental - avoids user-specified timeouts but does not work with all AJAX requests
            client.setAjaxController(new NicelyResynchronizingAjaxController());
            client.setActiveXObjectMap(null);
            client.setJavaScriptEnabled(javascript);
            client.setThrowExceptionOnFailingStatusCode(failOnError);
            client.setCookieManager(getCookieManager());
            try {
                client.setUseInsecureSSL(isAllowAllCertificates());
            }
            catch (GeneralSecurityException e) {
                throw new WebException(CANNOT_IGNORE_SSL_SECURE + e.getMessage(), e);
            }
            buildHeader(client);

            if (url == null) {
                throw new WebException(MISSING_URL);
            }

            try {
                context = new WebContext(client.getPage(url), client, getProject());
            }
            catch (IOException e) {
                throw new WebException(CANNOT_OPEN_PAGE + e.getMessage(), e);
            }

            getSessions().put(session, context);
        }
        return context;
    }


    private void buildHeader(WebClient client) {
        if (requestHeader != null && requestHeader.length() > 0) {
            for (String headerString : requestHeader.split(";")) {
                String[] header = headerString.split("=");
                client.addRequestHeader(header[0], header[1]);
            }
        }
    }


    public void open() {
        getProject().addReference(WEB_SESSIONS, new HashMap<String, WebClient>());
        getProject().addReference(COOKIE_MANAGER, new CookieManager());
    }


    public void close() {
        getProject().getReferences().remove(WEB_SESSIONS);
        getProject().getReferences().remove(COOKIE_MANAGER);
    }


    private Map<String, WebContext> getSessions() {
        //noinspection unchecked
        return ((Map<String, WebContext>)getProject().getReference(WEB_SESSIONS));
    }


    public void addEditForm(EditForm step) {
        steps.add(step);
    }


    public void addAssertPage(AssertPage step) {
        steps.add(step);
    }


    public void addAssertTable(AssertTable step) {
        steps.add(step);
    }


    public void addAssertLink(AssertLink step) {
        steps.add(step);
    }


    public void addClickLink(ClickLink step) {
        steps.add(step);
    }


    public void addClickButton(ClickButton step) {
        steps.add(step);
    }


    public void addClick(Click step) {
        steps.add(step);
    }


    public void addClickCheckBox(ClickCheckBox step) {
        steps.add(step);
    }


    public void addAssertText(AssertText step) {
        steps.add(step);
    }


    public void addAssertPresent(AssertPresent step) {
        steps.add(step);
    }


    public void addAssertImage(AssertImage step) {
        steps.add(step);
    }


    public void addRefresh(Refresh step) {
        steps.add(step);
    }


    public void addWait(Wait step) {
        steps.add(step);
    }


    public void addGotoPage(GotoPage step) {
        steps.add(step);
    }


    public void addAssertCheckBox(AssertCheckBox step) {
        steps.add(step);
    }


    public void addAssertAlert(AssertAlert step) {
        steps.add(step);
    }


    public void addSetConfirmation(SetConfirmation step) {
        steps.add(step);
    }


    public void addDownloadFile(DownloadFile step) {
        steps.add(step);
    }


    public String getSession() {
        return session;
    }


    public void setSession(String session) {
        this.session = session;
    }


    public boolean isFailOnError() {
        return failOnError;
    }


    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }


    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public String getRequestHeader() {
        return requestHeader;
    }


    public void setRequestHeader(String requestHeader) {
        this.requestHeader = requestHeader;
    }


    public void setJavascript(boolean javascript) {
        this.javascript = javascript;
    }


    private CookieManager getCookieManager() {
        return (CookieManager)getProject().getReference(COOKIE_MANAGER);
    }


    public boolean isAllowAllCertificates() {
        return allowAllCertificates;
    }


    public void setAllowAllCertificates(boolean allowAllCertificates) {
        this.allowAllCertificates = allowAllCertificates;
    }
}
