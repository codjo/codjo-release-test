package net.codjo.test.release.task.gui;
import net.codjo.test.common.LogString;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import junit.extensions.jfcunit.TestHelper;
import junit.extensions.jfcunit.JFCTestCase;
/**
 *
 */
public abstract class AbstractClickButtonStepTestCase extends JFCTestCase {
    protected JTable table;
    protected JPopupMenu popupMenu;
    protected PopupHelper popupHelper;
    protected LogString menuSelectionLog = new LogString();
    protected int buttonClicked = BUTTON_NOT_CLICKED;
    protected static final int BUTTON_NOT_CLICKED = -1;

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }// Attention : Bug jfcunit => si le menu contextuel dépasse visuellement de la frame,


    // jfcunit ne le voit pas, il faut alors augmenter la taille de la frame en conséquence.
    protected void showFrame(JComponent component) {
        JFrame frame = new JFrame("My frame");
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(600, 400));
        frame.setContentPane(panel);

        panel.add(component);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }


    protected void createTable() {
        table =
              new JTable(new Object[][]{
                    {"1A", "1B"},
                    {"2A", "2B"},
                    {"3A", "3B"}
              }, new Object[]{"titreA", "titreB"});
        table.setName("kingTable");

        popupMenu = new JPopupMenu();
        addMenu("menu1");
        addMenu("menu2");
        popupHelper = new PopupHelper();
        table.addMouseListener(popupHelper);
    }


    protected void addMenu(final String name) {
        popupMenu.add(new AbstractAction(name) {
            public void actionPerformed(ActionEvent event) {
                menuSelectionLog.info(name + " selected");
            }
        });
    }


    protected void addMenuItem(JMenu parentMenu, final String name) {
        parentMenu.add(new AbstractAction(name) {
            public void actionPerformed(ActionEvent event) {
                menuSelectionLog.info(name + " selected");
            }
        });
    }


    protected class PopupHelper extends MouseAdapter {
        private boolean mustShowPopup = true;
        private Point lastPointClicked;


        public void setMustShowPopup(boolean mustShowPopup) {
            this.mustShowPopup = mustShowPopup;
        }


        public Point getLastPointClicked() {
            return lastPointClicked;
        }


        @Override
        public void mouseReleased(MouseEvent event) {
            maybeShowPopup(event);
        }


        private void maybeShowPopup(MouseEvent event) {
            if (event.isPopupTrigger()) {
                if (mustShowPopup) {
                    popupMenu.show(event.getComponent(), event.getX(), event.getY());
                    buttonClicked = event.getButton();
                    lastPointClicked = new Point(event.getX(), event.getY());
                }
            }
            else {
                popupMenu.setVisible(false);
            }
        }
    }
}
