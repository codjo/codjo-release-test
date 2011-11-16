package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.util.List;
/**
 *
 */
public class AssertImage implements WebStep {
    private String src;


    public String getSrc() {
        return src;
    }


    public void setSrc(String src) {
        this.src = src;
    }


    public void proceed(WebContext context) throws IOException, WebException {
        HtmlPage page = context.getHtmlPage();

        @SuppressWarnings({"unchecked"})
        List<HtmlImage> images = (List<HtmlImage>)page.getByXPath("//img");
        for (HtmlImage image : images) {
            if (image.getSrcAttribute().equals(src)) {
                return;
            }
        }
        throw new WebException("Image '" + src + "' non présente "
                               + "dans la page '" + page.getTitleText() + "' "
                               + "(" + page.getFullyQualifiedUrl("") + ")");
    }
}
