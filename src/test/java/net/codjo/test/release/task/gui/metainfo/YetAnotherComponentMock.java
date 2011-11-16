package net.codjo.test.release.task.gui.metainfo;
/**
 *
 */
public class YetAnotherComponentMock extends MyComponentMock {
    @Override
    public void specificMethod(String valeur) {
        this.setText("YetAnotherComponentMock.specificMethod : " + valeur);
    }
}
