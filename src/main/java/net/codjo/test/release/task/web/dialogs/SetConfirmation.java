package net.codjo.test.release.task.web.dialogs;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebException;
import net.codjo.test.release.task.web.WebStep;
import com.gargoylesoftware.htmlunit.ConfirmHandler;
import com.gargoylesoftware.htmlunit.Page;
import java.io.IOException;
/**
 *
 */
public class SetConfirmation implements WebStep {
    private Boolean accept = Boolean.FALSE;


    public void proceed(WebContext context) throws IOException, WebException {
        context.getWebClient().setConfirmHandler(new ConfirmHandler() {
            public boolean handleConfirm(final Page page, final String message) {
                return accept;
            }
        });
    }


    public void setAccept(Boolean accept) {
        this.accept = accept;
    }
}
