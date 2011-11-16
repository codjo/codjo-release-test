package net.codjo.test.release.task.gui.metainfo;

import net.codjo.test.common.LogString;
import net.codjo.test.release.task.gui.AssertTableStep;
import javax.swing.JTable;

public class MyTableMockTestBehavior implements AssertTableDescriptor {
    static public LogString LOG;


    public void assertTable(JTable table, AssertTableStep step) {
        LOG.call(getClass().getSimpleName() + ".assertTable", table, step);
    }
}
