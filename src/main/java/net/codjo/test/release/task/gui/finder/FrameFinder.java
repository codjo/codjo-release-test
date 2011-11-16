package net.codjo.test.release.task.gui.finder;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JInternalFrame;
import junit.extensions.jfcunit.WindowMonitor;
import junit.extensions.jfcunit.finder.AbstractWindowFinder;
/**
 *
 */

public class FrameFinder extends AbstractWindowFinder {
    public FrameFinder(final String title) {
        super(title);
    }


    @Override
    public boolean testComponent(final Component comp) {
        if (comp instanceof JInternalFrame) {
            return isValidForProcessing(comp, JInternalFrame.class)
                   && checkIfTitleMatches(((JInternalFrame)comp).getTitle());
        }
        else if (comp instanceof Frame) {
            return isValidForProcessing(comp, Frame.class)
                   && checkIfTitleMatches(((Frame)comp).getTitle());
        }
        else if (comp instanceof Dialog) {
            return isValidForProcessing(comp, Dialog.class)
                   && checkIfTitleMatches(((Dialog)comp).getTitle());
        }
        else {
            return false;
        }
    }


    @Override
    public List findAll(final Container[] owners) {
        List retSet = new ArrayList();

        if (owners == null) {
            retSet = getWindows(retSet, WindowMonitor.getWindows(), this);
        }
        else {
            retSet = getWindows(retSet, owners, this);
        }
        return retSet;
    }


    /**
     * Recherche Soit une JInternalFrame, une Frame, ou un Dialogue. Ce code est copié de JFCUnit (Ca marche
     * mais c'est pas la grosse maitrise).
     *
     * @param conts la restriction de container
     * @param index L'index de recherche
     *
     * @return Le composant
     */
    @Override
    protected Component find(final Container[] conts, final int index) {
        long abortAfter = System.currentTimeMillis() + (getWait() * 1000);

        Container[] search;
        do {
            if (conts == null) {
                search = WindowMonitor.getWindows();
            }
            else {
                search = conts;
            }

            // Recherche de l'InternalFrame
            int idx = 0;
            for (Container aSearch : search) {
                List comps = findComponentList(this, aSearch, new ArrayList(), index - idx);
                int size = comps.size();

                if ((size > 0) && (size > (index - idx))) {
                    return (Component)comps.get(index - idx);
                }

                idx += comps.size();
            }

            // Recherche d'une Window (i.e. Dialog ou Frame)
            Object[] all = this.findAll().toArray();
            if (all.length != index) {
                return (Component)all[index];
            }

            // Pause pour attendre l'affichage
            pause(abortAfter);
        }
        while (System.currentTimeMillis() < abortAfter);

        return null;
    }
}