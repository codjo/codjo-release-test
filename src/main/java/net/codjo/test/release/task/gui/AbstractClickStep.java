package net.codjo.test.release.task.gui;
import net.codjo.test.release.task.gui.metainfo.ClickDescriptor;
import java.awt.Component;
import java.awt.event.InputEvent;
import junit.extensions.jfcunit.eventdata.MouseEventData;
import junit.extensions.jfcunit.finder.Finder;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public abstract class AbstractClickStep extends AbstractClickPopupMenuStep {
    private static final Logger LOGGER = Logger.getLogger(AbstractClickStep.class);
    private static final String CTRL_MODIFIER = "ctrl";
    private static final String SHIFT_MODIFIER = "shift";
    protected int count = 1;
    protected ClickDescriptor descriptor = null;
    protected String modifier;


    protected void proceedComponent(TestContext context,
                                    String searchCriteria,
                                    Finder... finders) {
        Component comp = findComponent(context, searchCriteria, finders);
        long begin = System.currentTimeMillis();
        initDescriptor(comp);

        while (!getComponentToClick(comp).isEnabled() && (System.currentTimeMillis() - begin
                                                          < getTimeout() * waitingNumber)) {
            try {
                LOGGER.debug(
                      "La tentative de l'assert a échoué. Mise en attente pour laisser le thread AWT travailler...");
                Thread.sleep(getWaitingNumber() / 2);
            }
            catch (InterruptedException e) {
                ;
            }
        }

        if (!getComponentToClick(comp).isEnabled()) {
            throw new GuiConfigurationException("Le composant '" + searchCriteria + "' est inactif.");
        }

        MouseEventData eventData = createMouseEventData(context, comp);
        setReferencePointIfNeeded(eventData, comp);

        eventData.setNumberOfClicks(count);
        eventData.setModifiers(this.getMouseModifiers());

        if ((getSelect() != null) || (getStepList().size() > 0)) {
            showPopupAndSelectItem(comp, context, eventData);
        }
        else {
            context.getHelper().enterClickAndLeave(eventData);
        }
    }


    protected MouseEventData createMouseEventData(TestContext context, Component comp) {
        ClickMouseEventData clickData = new ClickMouseEventData(context, comp);
        clickData.setModifiers(getMouseModifiers());
        return clickData;
    }

    abstract protected int getMouseModifiers();

    protected abstract void initDescriptor(Component comp);


    private Component findComponent(TestContext context, String searchCriteria, Finder... finders) {
        Component comp = null;
        for (int index = 0; index < finders.length && comp == null; index++) {
            Finder finder = finders[index];
            comp = findOnlyOne(finder, context);
        }
        if (comp == null) {
            throw new GuiFindException("Le composant '" + searchCriteria + "' est introuvable.");
        }
        return comp;
    }


    protected Component getComponentToClick(Component comp) {
        if (descriptor != null) {
            return descriptor.getComponentToClick(comp, this);
        }
        return comp;
    }


    protected void setReferencePointIfNeeded(MouseEventData eventData, Component comp) {
        if (descriptor != null) {
            descriptor.setReferencePointIfNeeded(eventData, comp, this);
        }
    }


    public int getCount() {
        return count;
    }


    public void setCount(int count) {
        this.count = count;
    }


    public String getModifier() {
        return modifier;
    }


    public void setModifier(String modifier) {
        this.modifier = modifier;
    }


    protected static int getModifierFromName(String modifierName) throws IllegalArgumentException {
        if (CTRL_MODIFIER.equals(modifierName)) {
            return InputEvent.CTRL_MASK;
        }
        else if (SHIFT_MODIFIER.equals(modifierName)) {
            return InputEvent.SHIFT_MASK;
        }
        else if (StringUtils.isEmpty(modifierName)) {
            return 0;
        }
        else {
            throw new IllegalArgumentException(java.lang.String.format("Pas de modifier standard correspondant à %s",
                                                      modifierName));
        }
    }
}
