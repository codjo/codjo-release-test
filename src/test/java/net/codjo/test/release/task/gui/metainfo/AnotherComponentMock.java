package net.codjo.test.release.task.gui.metainfo;
/**
 *
 */
public class AnotherComponentMock extends MyComponentMock{
        @Override
        public void specificMethod(String valeur) {
            this.setText("AnotherComponentMock.specificMethod : "+ valeur);
        }
}
