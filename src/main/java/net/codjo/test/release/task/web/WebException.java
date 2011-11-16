package net.codjo.test.release.task.web;
import org.apache.tools.ant.BuildException;
/**
 * Exception levée lorsqu'une erreur survient lors d'un test web.
 */
public class WebException extends BuildException {
    public WebException(String message) {
        super(message);
    }


    public WebException(String message, Throwable cause) {
        super(message, cause);
    }
}
