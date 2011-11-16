package net.codjo.test.release.task.gui;

import java.awt.event.MouseEvent;


public class ClickMiddleStepTest extends AbstractClickStepTest {

    @Override
    protected AbstractButtonClickStep createClickStep() {
        return new ClickMiddleStep();
    }

    @Override
    protected boolean isCorrectClickEvent(MouseEvent event) {
        return (event.getModifiers() & MouseEvent.BUTTON2_MASK) != 0;
    }
}
