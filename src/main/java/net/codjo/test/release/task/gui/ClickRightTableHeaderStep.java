package net.codjo.test.release.task.gui;

import java.awt.event.InputEvent;

public class ClickRightTableHeaderStep extends ClickButtonTableHeaderStep {

    @Override
    protected int getButtonMask() {
        return InputEvent.BUTTON3_MASK;
    }
}
