package net.codjo.test.release.task.gui;

public class SleepStep extends AbstractGuiStep {
    private Integer duration = 0;


    public Integer getDuration() {
        return duration;
    }


    public void setDuration(Integer duration) {
        this.duration = duration;
    }


    public void proceed(TestContext context) {
        try {
            Thread.sleep(duration);
        }
        catch (InterruptedException e) {
            ; // Pas grave
        }
    }
}
