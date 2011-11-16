/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public abstract class StepList extends AbstractGuiStep {
    protected String name;
    private List<GuiStep> stepList = new LinkedList<GuiStep>();


    protected void runBeforeStepProceed(TestContext context, GuiStep step) {
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void proceed(TestContext context) {
        for (GuiStep step : stepList) {
            runBeforeStepProceed(context, step);
            step.proceed(context);
        }
    }


    protected void addStep(GuiStep step) {
        stepList.add(step);
    }


    protected List<GuiStep> getStepList() {
        return stepList;
    }
}
