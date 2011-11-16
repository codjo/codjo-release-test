package net.codjo.test.release.task.web;
import java.io.IOException;
/**
 *
 */
public interface WebStep {
    public void proceed(WebContext context) throws IOException, WebException;
}
