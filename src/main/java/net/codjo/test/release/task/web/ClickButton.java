package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import net.codjo.test.release.task.web.finder.ResultHandler;
/**
 *
 */
public class ClickButton extends Click {

    @Override
    protected ResultHandler buildResultHandler() {
        return new ClickButtonResultHandler();
    }


    private class ClickButtonResultHandler extends DefaultResultHandler {
        @Override
        public void handleElementFound(HtmlElement element, String key) throws WebException {
            if (!(element instanceof HtmlButtonInput)) {
                throw new WebException("L'élément '" + key + "' n'est pas de la forme <input type='button' ...>");
            }
        }
    }
}
