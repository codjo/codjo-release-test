package net.codjo.test.release.task.gui;

import java.awt.event.MouseEvent;

public class ClickMiddleStep extends AbstractButtonClickStep {
    @Override
    protected int getMouseModifiers() {
        return MouseEvent.BUTTON2_MASK;
    }
}
