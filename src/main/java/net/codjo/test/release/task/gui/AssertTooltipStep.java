package net.codjo.test.release.task.gui;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.Date;
import javax.swing.JComponent;
import javax.swing.JTable;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 *
 */
public class AssertTooltipStep extends AbstractMatchingStep {
    private String name;
    private int row;


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public int getRow() {
        return row;
    }


    public void setRow(int row) {
        this.row = row;
    }


    @Override
    protected void proceedOnce(TestContext context) {
        NamedComponentFinder finder = new NamedComponentFinder(JComponent.class, name);
        Component component = findOnlyOne(finder, context, 0);
        if (component == null) {
            throw new GuiFindException("Le composant '" + getName() + "' est introuvable.");
        }

        if (component instanceof JComponent) {
            JComponent jComponent = (JComponent)component;
            String actualValue = jComponent.getToolTipText();

            if (actualValue == null && jComponent instanceof JTable) {
                actualValue = getTableTooltip((JTable)jComponent);
            }

            if (actualValue == null) {
                if ((expected != null) && (expected.length() > 0)) {
                    throw new GuiAssertException("Le composant '" + getName() + "' n'a pas de tooltip.");
                }
            }
            else {
                assertExpected(actualValue);
            }
        }
        else {
            throw new GuiAssertException(
                  "Le composant '" + component.getName() + "' doit être un JComponent.");
        }
    }


    private String getTableTooltip(JTable jTable) {
        MouseEvent mouseEvent = new MouseEvent(jTable,
                                               (int)Math.random(),
                                               new Date().getTime(),
                                               0,
                                               jTable.getCellRect(getRow(), 0, false).x,
                                               jTable.getCellRect(getRow(), 0, false).y,
                                               0,
                                               false);
        return jTable.getToolTipText(mouseEvent);
    }


    @Override
    protected String getComponentName() {
        return "Component '" + getName() + "'";
    }
}
