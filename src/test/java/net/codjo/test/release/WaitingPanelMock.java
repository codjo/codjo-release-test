package net.codjo.test.release;
import java.awt.Color;
import javax.swing.JPanel;
/**
 *
 */
public class WaitingPanelMock extends JPanel {
    public static final String WAITING_PANEL_NAME = "waitingPanel";


    public WaitingPanelMock() {
        setVisible(false);
        setOpaque(true);
        setBackground(Color.RED);
        setName(WAITING_PANEL_NAME);
    }


    public void exec() {

        new Thread() {

            @Override
            public void run() {
                setVisible(true);
                try {
                    Thread.sleep(50);
                }
                catch (InterruptedException e) {
                    //
                }
                setVisible(false);
            }
        }.start();
    }
}
