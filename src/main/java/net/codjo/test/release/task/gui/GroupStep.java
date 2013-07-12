/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;

/**
 * Représente un groupe de step de test.
 */
public class GroupStep extends StepList {
    private boolean enabled = true;


    @Override
    protected void runBeforeStepProceed(TestContext context, GuiStep step) {
        context.getTestLocation().setStep(step);
    }


    @Override
    protected void runAfterStepProceed(TestContext context, String groupName) {
        context.getTestLocation().resetGroupName();
    }


    @Override
    public void proceed(TestContext context) {
        context.getTestLocation().setGroupName(getName());
        if (enabled) {
            super.proceed(context);
        }
    }


    public void addClick(ClickStep step) {
        addStep(step);
    }


    public void addClickMiddle(ClickMiddleStep step) {
        addStep(step);
    }


    public void addAssertFrame(AssertFrameStep step) {
        addStep(step);
    }


    public void addGroup(GroupStep step) {
        addStep(step);
    }


    public void addAssertValue(AssertValueStep step) {
        addStep(step);
    }


    public void addAssertComponentImage(AssertComponentImageStep step) {
        addStep(step);
    }


    public void addSetValue(SetValueStep step) {
        addStep(step);
    }


    public void addAssertTable(AssertTableStep step) {
        addStep(step);
    }


    public void addAssertTableExcel(AssertTableExcelStep step) {
        addStep(step);
    }


    public void addCloseFrame(CloseFrameStep step) {
        addStep(step);
    }


    public void addPause(PauseStep step) {
        addStep(step);
    }


    public void addSelect(SelectStep step) {
        addStep(step);
    }


    public void addAssertSelected(AssertSelectedStep step) {
        addStep(step);
    }


    public void addSleep(SleepStep step) {
        addStep(step);
    }


    public void addAssertListSize(AssertListSizeStep step) {
        addStep(step);
    }


    public void addAssertList(AssertListStep step) {
        addStep(step);
    }


    public void addAssertTree(AssertTreeStep step) {
        addStep(step);
    }


    public void addSelectTab(SelectTabStep step) {
        addStep(step);
    }


    public void addAssertEditable(AssertEditableStep step) {
        addStep(step);
    }


    public void addAssertEnabled(AssertEnabledStep step) {
        addStep(step);
    }


    public void addAssertMenu(AssertMenuStep step) {
        addStep(step);
    }


    public void addEditCell(EditCellStep step) {
        addStep(step);
    }


    public void addAssertVisible(AssertVisibleStep step) {
        addStep(step);
    }


    public void addAssertProgressDisplay(AssertProgressDisplayStep step) {
        int listSize = getStepList().size();
        if (listSize == 0) {
            throw new GuiConfigurationException(
                  "La balise 'assertProgressDisplay' ne doit pas être la première d'un groupe");
        }
        getStepList().add(listSize - 1, step.getPreparationStep());
        addStep(step);
    }


    public void addClickRight(ClickRightStep step) {
        addStep(step);
    }


    public void addClickRightTableHeader(ClickRightTableHeaderStep step) {
        addStep(step);
    }


    public void addClickMiddleTableHeader(ClickMiddleTableHeaderStep step) {
        addStep(step);
    }


    public void addAssertContains(AssertContainsStep step) {
        addStep(step);
    }


    public void addClickTableHeader(ClickTableHeaderStep step) {
        addStep(step);
    }


    public void addScrollToVisible(ScrollToVisibleStep step) {
        addStep(step);
    }


    public void addExpandAllTree(ExpandAllTreeStep step) {
        addStep(step);
    }


    public void addPressKey(PressKeyStep step) {
        addStep(step);
    }


    public void addAssertTitleBorder(AssertTitleBorderStep step) {
        addStep(step);
    }


    public void addAssertTab(AssertTabStep step) {
        addStep(step);
    }


    public void addAssertTabCount(AssertTabCountStep step) {
        addStep(step);
    }


    public void addAssertTooltip(AssertTooltipStep step) {
        addStep(step);
    }


    public void addSetCalendar(SetCalendarStep step) {
        addStep(step);
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public boolean isEnabled() {
        return enabled;
    }
}
