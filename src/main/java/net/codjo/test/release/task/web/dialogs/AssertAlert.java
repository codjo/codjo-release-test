package net.codjo.test.release.task.web.dialogs;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebException;
import net.codjo.test.release.task.web.WebStep;
import java.io.IOException;
import java.util.List;
import junit.framework.Assert;
/**
 *
 */
public class AssertAlert implements WebStep {
    private String message = "";


    public void proceed(WebContext context) throws IOException, WebException {
        if (!context.popAlert(message)) {
            String actualMessage = "L'alerte '" + message + "' n'a pas été levée";
            List<String> alerts = context.getAlerts();
            if (!alerts.isEmpty()) {
                actualMessage += " - alertes=  " + alerts;
            }
            Assert.fail(actualMessage);
        }
    }


    public void addText(String value) {
        this.message += value;
    }
}
