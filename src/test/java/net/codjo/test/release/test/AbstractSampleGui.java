/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.test;
import javax.swing.JFrame;
/**
 * 
 */
public abstract class AbstractSampleGui extends JFrame {
    protected AbstractSampleGui(String title) {
        super(title);
        buildGui();
        setSize(1200, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    protected abstract void buildGui();
}
