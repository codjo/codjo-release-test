package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import net.codjo.test.release.task.web.finder.ResultHandler;
/**
 *
 */
public class ClickLink extends Click {

    @Override
    public void setText(String text) {
        super.setText(text);
    }


    @Override
    protected ResultHandler buildResultHandler() {
        return new ClickLinkResultHandler();
    }


    private class ClickLinkResultHandler extends DefaultResultHandler {
        @Override
        public void handleElementFound(HtmlElement element, String key) throws WebException {
            if (!(element instanceof HtmlAnchor)) {
                throw new WebException("L'élément '" + key + "' n'est pas un lien <a ...>");
            }
        }
    }
}
