package net.codjo.test.release.test.standard;
import javax.swing.JFrame;
public class ExitingGui extends JFrame {

    public ExitingGui() {
        super("ExitingGui");
        buildGui();
        setSize(1200, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }


    private void buildGui() {
        System.exit(1);
    }


    public static void main(String[] args) {
        JFrame frame = new ExitingGui();
        frame.setVisible(true);
    }
}
