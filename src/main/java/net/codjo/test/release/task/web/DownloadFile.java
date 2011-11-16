package net.codjo.test.release.task.web;
import net.codjo.test.release.task.AgfTask;
import net.codjo.util.file.FileUtil;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.File;
import java.io.IOException;
/**
 *
 */
public class DownloadFile implements WebStep {
    private String link;
    private String target;


    public void setLink(String link) {
        this.link = link;
    }


    public void setTarget(String target) {
        this.target = target;
    }


    public void proceed(WebContext context) throws IOException {
        if (target == null) {
            throw new WebException("L'attribut 'target' doit être spécifié");
        }
        HtmlAnchor anchor = findAnchor(context);
        try {
            String content = anchor.<Page>click().getWebResponse().getContentAsString();
            File targetFile = new File(context.getProperty(AgfTask.BROADCAST_LOCAL_DIR), target);
            FileUtil.saveContent(targetFile, content);
        }
        catch (FailingHttpStatusCodeException e) {
            throw new WebException("Erreur lors du click sur le lien '" + link + "' : " + e.getMessage());
        }
    }


    private HtmlAnchor findAnchor(WebContext context) {
        HtmlPage page = context.getHtmlPage();
        if (link != null) {
            link = context.replaceProperties(link);
            try {
                return page.getAnchorByText(link);
            }
            catch (ElementNotFoundException e) {
                throw new WebException("Aucun lien trouvé avec le texte: " + link);
            }
        }
        throw new WebException("L'attribut 'link' doit être spécifié");
    }
}
