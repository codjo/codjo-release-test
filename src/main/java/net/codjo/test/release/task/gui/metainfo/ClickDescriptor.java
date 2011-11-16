package net.codjo.test.release.task.gui.metainfo;
import net.codjo.test.release.task.gui.AbstractClickStep;
import java.awt.Component;
import junit.extensions.jfcunit.eventdata.MouseEventData;
/**
 *
 */
public interface ClickDescriptor {
    public Component getComponentToClick(Component comp, AbstractClickStep step);


    public void setReferencePointIfNeeded(MouseEventData eventData, Component component, AbstractClickStep step);
}
