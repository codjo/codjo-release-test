package net.codjo.test.release.task.gui.metainfo;

import net.codjo.test.release.task.gui.AssertTableStep;
import net.codjo.test.release.task.gui.GuiAssertException;
import javax.swing.JTable;

public interface AssertTableDescriptor {
    void assertTable(JTable table, AssertTableStep step) throws GuiAssertException;
}
