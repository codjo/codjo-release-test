/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.event.MouseEvent;

/**
 * Gestion du tag <code>click</code>. Ce tag permet de simuler un clic souris sur un composant graphique.
 */
public class ClickStep extends AbstractButtonClickStep {

    @Override
    protected int getMouseModifiers() {
        return getModifierFromName(getModifier()) | MouseEvent.BUTTON1_MASK;
    }
}
