package net.codjo.test.release.task.web;
import java.io.IOException;
import net.codjo.test.release.task.web.dialogs.AssertAlert;
import net.codjo.test.release.task.web.dialogs.SetConfirmation;
import net.codjo.test.release.task.web.form.EditForm;
/**
 *
 */
public class Group extends StepList {
    private boolean enabled = true;


    @Override
    protected void runBeforeStepProceed(WebContext context, WebStep step) {
        context.getTestLocation().setStep(step);
    }


    @Override
    protected void runAfterStepProceed(WebContext context, String groupName) {
        context.getTestLocation().resetGroupName();
    }


    @Override
    public void proceed(WebContext context) throws IOException, WebException {
        context.getTestLocation().setGroupName(getName());
        if (enabled) {
            super.proceed(context);
        }
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public boolean isEnabled() {
        return enabled;
    }


    public void addEditForm(EditForm step) {
        addStep(step);
    }


    public void addAssertPage(AssertPage step) {
        addStep(step);
    }


    public void addAssertTable(AssertTable step) {
        addStep(step);
    }


    public void addAssertLink(AssertLink step) {
        addStep(step);
    }


    public void addClickLink(ClickLink step) {
        addStep(step);
    }


    public void addClickButton(ClickButton step) {
        addStep(step);
    }


    public void addClick(Click step) {
        addStep(step);
    }


    public void addDragAndDrop(DragAndDrop step) {
        addStep(step);
    }


    public void addClickCheckBox(ClickCheckBox step) {
        addStep(step);
    }


    public void addAssertText(AssertText step) {
        addStep(step);
    }


    public void addAssertPresent(AssertPresent step) {
        addStep(step);
    }


    public void addAssertImage(AssertImage step) {
        addStep(step);
    }


    public void addRefresh(Refresh step) {
        addStep(step);
    }


    public void addWait(Wait step) {
        addStep(step);
    }


    public void addGotoPage(GotoPage step) {
        addStep(step);
    }


    public void addAssertCheckBox(AssertCheckBox step) {
        addStep(step);
    }


    public void addAssertAlert(AssertAlert step) {
        addStep(step);
    }


    public void addSetConfirmation(SetConfirmation step) {
        addStep(step);
    }


    public void addDownloadFile(DownloadFile step) {
        addStep(step);
    }


    public void addGroup(Group step) {
        addStep(step);
    }
}
