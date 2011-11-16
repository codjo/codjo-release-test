package net.codjo.test.release.test.standard;
import net.codjo.test.release.WaitingPanelMock;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JWindow;
import javax.swing.RootPaneContainer;
/**
 * Ihm d'essai permettant de tester les 'AssertProgressDisplay'.
 */
public class AssertProgressDisplayGui extends JFrame {

    public AssertProgressDisplayGui() {
        setLayout(new FlowLayout());
        addButton(this, "Lancer JDialog", new JDialogActionListener());
        addButton(this, "Lancer JFrame", new JFrameActionListener());
        addButton(this, "Lancer JWindow", new JWindowActionListener());
        addButton(this, "Lancer JInternalFrame", new JInternalFrameActionListener());
        pack();
    }


    public static void main(String[] args) {
        JFrame frame = new AssertProgressDisplayGui();
        frame.setVisible(true);
    }


    private void addButton(Container containter, String label, ActionListener listener) {
        JButton jDialogButton = new JButton(label);
        jDialogButton.addActionListener(listener);
        containter.add(jDialogButton);
    }


    private void showJDialog() {
        JDialog jDialog = new JDialog(this, "JDialog avec WaitingPanel");
        showWindow(jDialog);
    }


    private void showJFrame() {
        JFrame jFrame = new JFrame("JFrame avec WaitingPanel");
        showWindow(jFrame);
    }


    private void showJWindow() {
        JWindow jWindow = new JWindow(this);
        showWindow(jWindow);
    }


    private void showJInternalFrame() {
        JFrame jFrame = new JFrame("JFrame contenant une JInternalFrame avec WaitingPanel");
        JDesktopPane desktopPane = new JDesktopPane();
        jFrame.setContentPane(desktopPane);

        JInternalFrame jInternalFrame = new JInternalFrame("JInternalFrame avec WaitingPanel");
        desktopPane.add(jInternalFrame);

        WaitingPanelMock glassPane = new WaitingPanelMock();
        jInternalFrame.setGlassPane(glassPane);
        glassPane.setVisible(false);
        addButton(jInternalFrame, "Show WaitingPanel", new WaitingPanelActionListener(glassPane));

        jInternalFrame.pack();
        jInternalFrame.setVisible(true);
        jFrame.setSize(200, 200);
        jFrame.setVisible(true);
    }


    private <T extends Window & RootPaneContainer> void showWindow(T rootPaneWindow) {
        WaitingPanelMock glassPane = new WaitingPanelMock();
        rootPaneWindow.setGlassPane(glassPane);
        addButton(rootPaneWindow, "Show WaitingPanel", new WaitingPanelActionListener(glassPane));
        rootPaneWindow.pack();
        rootPaneWindow.setVisible(true);
    }


    private class JInternalFrameActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            showJInternalFrame();
        }
    }
    private class JWindowActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            showJWindow();
        }
    }
    private class JFrameActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            showJFrame();
        }
    }
    private class JDialogActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            showJDialog();
        }
    }
    private static class WaitingPanelActionListener implements ActionListener {
        private final WaitingPanelMock glassPane;


        private WaitingPanelActionListener(WaitingPanelMock glassPane) {
            this.glassPane = glassPane;
        }


        public void actionPerformed(ActionEvent event) {
            glassPane.exec();
        }
    }
}
