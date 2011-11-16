package net.codjo.test.release.task.gui.metainfo;

import net.codjo.test.common.LogString;
import net.codjo.test.release.task.gui.SetValueStep;
import java.awt.Component;

public class MyComponentMockTestBehavior implements SetValueDescriptor {
    static public LogString LOG;


    public void setValue(Component comp, SetValueStep step) {
        LOG.call("MyComponentMockTestBehavior.setValue", comp, step);
        ((MyComponentMock)comp).specificMethod(step.getValue());
    }
}