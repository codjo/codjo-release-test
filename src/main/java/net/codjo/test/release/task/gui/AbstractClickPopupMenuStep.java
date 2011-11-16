package net.codjo.test.release.task.gui;
import java.awt.Component;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import junit.extensions.jfcunit.eventdata.AbstractMouseEventData;
import junit.extensions.jfcunit.eventdata.JMenuMouseEventData;
import junit.extensions.jfcunit.finder.JPopupMenuFinder;
import org.apache.log4j.Logger;

public abstract class AbstractClickPopupMenuStep extends StepList {
    protected static final int INITIAL_ROW_VALUE = -1;
    protected static final Logger LOG = Logger.getLogger(AbstractClickPopupMenuStep.class);
    protected String column = "0";
    protected String select;
    protected int finderTimeout = 2;
    protected int waitingNumber = 10;
    protected int disappearTryingNumber = 10;
    private boolean popupVisible = true;


    public String getColumn() {
        return column;
    }


    public void setColumn(String column) {
        this.column = column;
    }


    public String getSelect() {
        return select;
    }


    public void setSelect(String select) {
        this.select = select;
    }


    public boolean isPopupVisible() {
        return popupVisible;
    }


    public void setPopupVisible(boolean popupVisible) {
        this.popupVisible = popupVisible;
    }


    public int getFinderTimeout() {
        return finderTimeout;
    }


    protected void setFinderTimeout(int finderTimeout) {
        this.finderTimeout = finderTimeout;
    }


    protected void setWaitingNumber(int waitingNumber) {
        this.waitingNumber = waitingNumber;
    }


    public int getWaitingNumber() {
        return waitingNumber;
    }


    protected void setDisappearTryingNumber(int disappearTryingNumber) {
        this.disappearTryingNumber = disappearTryingNumber;
    }


    public void addSelect(SelectStep step) {
        addStep(step);
    }


    public void addAssertListSize(AssertListSizeStep step) {
        addStep(step);
    }


    public void addAssertList(AssertListStep step) {
        addStep(step);
    }


    public void addAssertEnabled(AssertEnabledStep step) {
        addStep(step);
    }


    public void addPause(PauseStep step) {
        addStep(step);
    }


    protected void selectItemMenu(TestContext context, Component popup, JPopupMenuFinder popupFinder) {
        if (getSelect() == null) {
            context.setCurrentComponent(popup);
            super.proceed(context);
            context.setCurrentComponent(null);
            popup.setVisible(false);
        }
        else {
            StringTokenizer tok = new StringTokenizer(select, ":");
            String[] labels = new String[tok.countTokens()];

            for (int i = 0; tok.hasMoreTokens(); i++) {
                labels[i] = tok.nextToken();
            }

            int[] indexes = ClickStep.getPathIndexes((JPopupMenu)popup, labels);
            JMenuMouseEventData menuEventData = new JMenuMouseEventData(context.getTestCase(),
                                                                        (JComponent)popup, indexes);
            menuEventData.setSleepTime(getTimeout() * waitingNumber);
            context.getHelper().enterClickAndLeave(menuEventData);

            if (popup.isVisible()) {
                waitForPopupDisappear(popup, context, menuEventData, popupFinder);
            }
        }
    }


    protected void showPopupAndSelectItem(Component component,
                                          TestContext context,
                                          AbstractMouseEventData eventData) {
        context.getHelper().enterClickAndLeave(eventData);

        JPopupMenuFinder popupFinder = new JPopupMenuFinder(component);
        Component popup = findOnlyOne(popupFinder, context, getFinderTimeout());

        if (popup == null) {
            if (isPopupVisible()) {
                throw new GuiFindException("Le menu contextuel associé au composant '" + getName()
                                           + "' est introuvable");
            }
            else {
                return;
            }
        }

        if (!isPopupVisible() && popup.isShowing()) {
            throw new GuiAssertException(
                  "Le menu contextuel est visible alors que l'attribut 'popup' est initialisé a 'false'");
        }

        selectItemMenu(context, popup, popupFinder);
    }


    private void waitForPopupDisappear(Component popup,
                                       TestContext context,
                                       JMenuMouseEventData menuEventData,
                                       JPopupMenuFinder popupFinder) {
        int index = 0;
        while (index < disappearTryingNumber && popup != null) {
            LOG.debug("La tentative numéro " + index + " de l'assert a échoué.");
            context.getHelper().enterClickAndLeave(menuEventData);
            popup = findOnlyOne(popupFinder, context, finderTimeout);
            index++;
        }

        if (popup != null) {
            throw new GuiActionException("Le menu contextuel associé au composant " + getName()
                                         + " est toujours présent");
        }
    }
}
