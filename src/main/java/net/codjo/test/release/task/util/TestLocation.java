/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.util;
import net.codjo.test.release.task.ReleaseTestStep;
import net.codjo.test.release.task.Util;
/**
 *
 */
public class TestLocation {
    private String groupName;
    private ReleaseTestStep step;
    private int stepNumber;


    public String getGroupName() {
        return groupName;
    }


    public void setGroupName(String groupName) {
        this.groupName = groupName;
        stepNumber = 0;
    }


    public ReleaseTestStep getStep() {
        return step;
    }


    public void setStep(ReleaseTestStep step) {
        this.step = step;
        stepNumber++;
    }


    public int getStepNumber() {
        return stepNumber;
    }


    public String getLocationMessage() {
        if (groupName == null || step == null) {
            return "Localisation impossible";
        }
        return "Step " + stepNumber + " du groupe '" + groupName + "' ("
               + Util.computeClassName(step.getClass()) + ")";
    }
}
