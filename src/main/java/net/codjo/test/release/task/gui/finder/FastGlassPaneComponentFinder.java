package net.codjo.test.release.task.gui.finder;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.RootPaneContainer;
import junit.extensions.jfcunit.WindowMonitor;
/**
 * Permet de trouver le premier glassPane nommé et visible. Plus rapide que #GlassPaneComponentFinder (environ
 * 10 fois). Contraintes : <ul> <li> ne fonctionne pas pour les JApplet</li> <li> suppose qu'un JDesktopPane
 * est ContentPane ou enfant direct d'une JFrame</li> </ul>
 */
public class FastGlassPaneComponentFinder extends GlassPaneComponentFinder {

    public FastGlassPaneComponentFinder(String name) {
        super(name, false);
    }


    public Component findOnlyOne() {
        List<Component> glassPanes = findAllGlassPanes();
        Component found = findIt(glassPanes);
        if (found == null) {
            Component component = find();
            if (component != null && component.isVisible()) {
                RootPaneContainer container = (RootPaneContainer)component;
                found = container.getGlassPane();
            }
        }
        return found;
    }


    private Component findIt(List<Component> glassPanes) {
        for (Component glassPane : glassPanes) {
            if (name.equals(glassPane.getName())) {
                return glassPane;
            }
        }
        return null;
    }


    private List<Component> findAllGlassPanes() {
        List<Component> glassPanes = new ArrayList<Component>();
        Window[] windows = WindowMonitor.getWindows();
        for (Window window : windows) {
            if (!window.isVisible()) {
                continue;
            }
            if (window instanceof JFrame) {
                JFrame jFrame = (JFrame)window;
                glassPanes.add(jFrame.getGlassPane());
                Container contentPane = jFrame.getContentPane();
                if (contentPane instanceof JDesktopPane) {
                    findInJDesktopPane(glassPanes, (JDesktopPane)contentPane);
                }
                else {
                    Component[] components = contentPane.getComponents();
                    for (Component component : components) {
                        if (component instanceof JDesktopPane) {
                            findInJDesktopPane(glassPanes, (JDesktopPane)component);
                        }
                    }
                }
            }
            else if (window instanceof RootPaneContainer) {
                glassPanes.add(((RootPaneContainer)window).getGlassPane());
            }
        }
        return glassPanes;
    }


    private void findInJDesktopPane(List<Component> glassPanes, JDesktopPane jDesktopPane) {
        glassPanes.add(jDesktopPane);
        for (JInternalFrame internalFrame : jDesktopPane.getAllFrames()) {
            if (internalFrame.isVisible()) {
                glassPanes.add(internalFrame.getGlassPane());
            }
        }
    }
}
