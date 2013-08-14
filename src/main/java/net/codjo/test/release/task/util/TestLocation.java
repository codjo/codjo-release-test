/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.util;
import java.util.LinkedList;
import net.codjo.test.release.task.ReleaseTestStep;
import net.codjo.test.release.task.Util;
/**
 *
 */
public class TestLocation {
    private String groupName;
    private ReleaseTestStep step;
    private int stepNumber;
    private LinkedList<String> path = new LinkedList<String>();


    public String getGroupName() {
        return groupName;
    }


    public void setGroupName(String groupName) {
        this.groupName = groupName;
        stepNumber = 0;
        path.addLast(groupName);
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
        StringBuilder tempBuffer = new StringBuilder("");
        if (path.isEmpty()) {
            tempBuffer.append(groupName);
        }
        else {
            formatLinkedList(tempBuffer);
        }
        return "Step " + stepNumber + " du groupe '" + tempBuffer.toString() + "' ("
               + Util.computeClassName(step.getClass()) + ")";
    }


    public void resetGroupName() {
        if (!path.isEmpty() && groupName != null) {
            path.removeLast();
            if (path.isEmpty()) {
                groupName = null;
            }
        }
    }


    private void formatLinkedList(StringBuilder tempBuffer) {
        final String separator = " > ";
        for (String groups : path) {
            tempBuffer.append(groups).append(separator);
        }
        tempBuffer.delete(tempBuffer.length() - separator.length(), tempBuffer.length());
    }
}
