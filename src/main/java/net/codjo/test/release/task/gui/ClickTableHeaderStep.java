/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import net.codjo.test.release.task.gui.metainfo.ClickTableHeaderDescriptor;
import net.codjo.test.release.task.gui.metainfo.Introspector;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import junit.extensions.jfcunit.eventdata.MouseEventData;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 *
 */
public class ClickTableHeaderStep extends AbstractClickStep {
    private boolean component;


    @Override
    public void proceed(TestContext context) {
        if (name != null) {
            proceedComponent(context, name, new NamedComponentFinder(JComponent.class, name));
        }
    }


    @Override
    protected MouseEventData createMouseEventData(TestContext context, Component comp) {
        return new MouseEventData(context.getTestCase(), comp);
    }

    @Override
    protected int getMouseModifiers() {
        return getModifierFromName(getModifier()) | MouseEvent.BUTTON1_MASK;
    }


    @Override
    protected void initDescriptor(Component comp) {
        TableCellRenderer renderer = getTableHeaderRenderer(comp);
        if (renderer != null) {
            descriptor = Introspector.getTestBehavior(getTableHeaderRenderer(comp).getClass(),
                                                      ClickTableHeaderDescriptor.class);
        }
    }


    public boolean componentIsPresent() {
        return component;
    }


    @Override
    protected void setReferencePointIfNeeded(MouseEventData eventData, Component comp) {
        if (descriptor != null) {
            descriptor.setReferencePointIfNeeded(eventData, comp, this);
        }
        else {
            if (JTable.class.isInstance(comp)) {
                JTable table = (JTable)comp;
                int columnNumber = TableTools.searchColumn(table, column);

                Rectangle cellRect = table.getTableHeader().getHeaderRect(columnNumber);

                cellRect.x = cellRect.x + cellRect.width / 2;
                cellRect.y = cellRect.y - cellRect.height / 2;

                eventData.setPosition(MouseEventData.CUSTOM);
                eventData.setReferencePoint(cellRect.getLocation());
            }
        }
    }


    public Component getTableHeaderComponent(Component comp) {
        if (component) {
            JTable table = (JTable)comp;
            int columnNumber = TableTools.searchColumn(table, column);
            TableTools.checkTableHeaderExists(table, columnNumber);
            TableColumn tableColumn = table.getColumnModel().getColumn(columnNumber);
            TableCellRenderer renderer = tableColumn.getHeaderRenderer();
            return renderer.getTableCellRendererComponent(table, "", false, false, 0, columnNumber);
        }
        return comp;
    }


    public TableCellRenderer getTableHeaderRenderer(Component comp) {
        JTable table = (JTable)comp;
        int columnNumber = TableTools.searchColumn(table, column);
        TableTools.checkTableHeaderExists(table, columnNumber);
        TableColumn tableColumn = table.getColumnModel().getColumn(columnNumber);
        return tableColumn.getHeaderRenderer();
    }


    public boolean getComponent() {
        return component;
    }


    public void setComponent(boolean component) {
        this.component = component;
    }
}
