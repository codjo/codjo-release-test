package net.codjo.test.release.task.gui.metainfo;
import javax.swing.JTextField;
/**
 *
 */
public class MyComponentMock extends JTextField {
    public void specificMethod(String valeur) {
        this.setText(valeur);
    }


    @Override
    public String toString() {
        return getClass().getName();
    }
}