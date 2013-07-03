package net.codjo.test.release.task.web;
import java.io.IOException;
/**
 *
 */
public interface WebStep {
    final int WAIT_FOR_JAVASCRIPT = 100;


    public void proceed(WebContext context) throws IOException, WebException;
}
