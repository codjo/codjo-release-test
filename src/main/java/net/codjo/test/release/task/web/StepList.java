package net.codjo.test.release.task.web;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
/**
 * TODO very similar to StepList in gui package
 */
public class StepList implements WebStep {
    protected String name;
    private List<WebStep> stepList = new LinkedList<WebStep>();


    protected void runBeforeStepProceed(WebContext context, WebStep step) {
    }

    protected void runAfterStepProceed(WebContext context, String groupName) {
    }


    public void proceed(WebContext context) throws IOException, WebException {
        for (WebStep step : stepList) {
            runBeforeStepProceed(context, step);
            step.proceed(context);
        }
        runAfterStepProceed(context, name);
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    protected void addStep(WebStep step) {
        stepList.add(step);
    }


    protected List<WebStep> getStepList() {
        return stepList;
    }
}
