package net.codjo.test.release.task.web;
import net.codjo.test.release.task.AgfTask;
import com.gargoylesoftware.htmlunit.CookieManager;
/**
 *
 */
public class ClearCookiesTask extends AgfTask {
    public static final String COOKIE_MANAGER = "cookieManager";


    @Override
    public void execute() {
        getProject().getReferences().remove(COOKIE_MANAGER);
        getProject().addReference(COOKIE_MANAGER, new CookieManager());
    }
}
