package net.codjo.test.release.task.web;
import java.io.IOException;
/**
 *
 */
public class Wait implements WebStep {
    private Integer duration = 0;


    public void setDuration(Integer duration) {
        this.duration = duration;
    }


    public void proceed(WebContext context) throws IOException, WebException {
        try {
            Thread.sleep(duration);
        }
        catch (InterruptedException e) {
            ;
        }
    }
}
