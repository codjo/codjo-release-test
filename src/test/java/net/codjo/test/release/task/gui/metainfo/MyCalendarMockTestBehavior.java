package net.codjo.test.release.task.gui.metainfo;

import net.codjo.test.common.LogString;
import net.codjo.test.release.task.gui.SetCalendarStep;
import java.awt.Component;

public class MyCalendarMockTestBehavior implements SetCalendarDescriptor {
    static public LogString LOG;


    public void setCalendar(Component comp, SetCalendarStep step) {
        LOG.call("MyCalendarMockTestBehavior.setCalendarValue", comp, step);
    }
}