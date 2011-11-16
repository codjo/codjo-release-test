package net.codjo.test.release.task.web;
import java.io.IOException;
/**
 * Ouvre une nouvelle URL dans la fenêtre de navigateur courante.
 */
public class GotoPage implements WebStep {
    private String url;


    public void proceed(WebContext context) throws IOException {
        context.setPage(url);
    }


    public void setUrl(String url) {
        this.url = url;
    }
}
