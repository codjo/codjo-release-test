package net.codjo.test.release.task.web;
import java.io.IOException;
import net.codjo.test.release.task.ReleaseTestStep;
/**
 *
 */
public interface WebStep extends ReleaseTestStep {
    final int WAIT_FOR_JAVASCRIPT = 100;


    public void proceed(WebContext context) throws IOException, WebException;
}
