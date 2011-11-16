/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Component;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import junit.extensions.jfcunit.eventdata.PathData;
import junit.extensions.jfcunit.finder.JMenuItemFinder;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 * Classe permettant de vérifier qu'un bouton {@link javax.swing.JButton} est activé ou pas.
 */
public class AssertEnabledStep extends AbstractAssertStep {
    private String name;
    private String menu;
    private String expected;
    private String row = "-1";
    private String column = "-1";


    public String getMenu() {
        return menu;
    }


    public void setMenu(String menu) {
        this.menu = menu;
    }


    public String getName() {
        if (name == null && menu != null) {
            return menu;
        }
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getExpected() {
        return expected;
    }


    public void setExpected(String expected) {
        this.expected = expected;
    }


    private void proceedMenu(JMenuItem menuFinded, String menuName, String[] labels) {

        if (menuFinded == null) {
            throw new GuiFindException("Menu non trouvé : " + menuName);
        }

        if (labels.length == 1) {
            proceed(menuFinded);
        }
        else {
            PathData path = new PathData(labels);
            JMenuBar menuBar = (JMenuBar)path.getRoot(menuFinded);

            assertMenuEnableState(menuBar, labels);
        }
    }


    private void proceedPopupMenu(TestContext context) {
        StringTokenizer tok = new StringTokenizer(menu, ":");
        String[] labels = new String[tok.countTokens()];

        for (int i = 0; tok.hasMoreTokens(); i++) {
            labels[i] = tok.nextToken();
        }

        String menuName = labels[0];
        JMenuItemFinder finder = new JMenuItemFinder(menuName);
        JComponent menuFinded = (JComponent)findOnlyOne(finder, context, 0);

        if (menuFinded == null) {
            throw new GuiFindException("Menu non trouvé : " + menuName);
        }

        if (!findMenuItem(labels, (JPopupMenu)menuFinded, 0, null)) {
            throw new GuiFindException("MenuItem non trouvé : " + labels);
        }
    }


    @Override
    protected void proceedOnce(TestContext context) {
        if (menu != null) {
            StringTokenizer tok = new StringTokenizer(menu, ":");
            String[] labels = new String[tok.countTokens()];

            for (int i = 0; tok.hasMoreTokens(); i++) {
                labels[i] = tok.nextToken();
            }

            String menuName = labels[0];
            JMenuItemFinder finder = new JMenuItemFinder(menuName);
            JComponent menuFinded = (JComponent)findOnlyOne(finder, context, 0);
            if (menuFinded instanceof JMenuItem) {
                proceedMenu((JMenuItem)menuFinded, menuName, labels);
            }
            else if (menuFinded instanceof JPopupMenu) {
                proceedPopupMenu(context);
            }
        }
        else {
            Component component = findComponent(context);
            if (component instanceof JTable && !"-1".equals(row)) {
                proceed((JTable)component);
            }
            else {
                proceed((JComponent)component);
            }
        }
    }


    private Component findComponent(TestContext context) {
        NamedComponentFinder finder = new NamedComponentFinder(JComponent.class, name);
        Component comp = findOnlyOne(finder, context, 0);
        if (comp == null) {
            throw new GuiFindException("Le composant '" + getName() + "' est introuvable.");
        }
        return comp;
    }


    private void proceed(JTable table) {
        int realRow = -1;
        try {
            realRow = Integer.parseInt(row);
        }
        catch (NumberFormatException nfe) {
            ;
        }
        if (table == null) {
            throw new GuiFindException("La table '" + getName() + "' est introuvable.");
        }
        int realColumn = TableTools.searchColumn(table, column);
        TableTools.checkTableCellExists(table, realRow, realColumn);

        boolean isEditable = table.isCellEditable(realRow, realColumn);

        assertExpected(String.valueOf(isEditable));
    }


    private void proceed(JComponent component) {
        String actualValue;
        if (component.isEnabled()) {
            actualValue = "true";
        }
        else {
            actualValue = "false";
        }
        assertExpected(actualValue);
    }


    private void assertExpected(String actualValue) {
        if (!expected.equals(actualValue)) {
            throwBadEnableStateException(actualValue);
        }
    }


    private void throwBadEnableStateException(String actualValue) {
        throw new GuiAssertException("Composant '" + getName() + "' : attendu='" + expected + "' obtenu='"
                                     + actualValue + "'");
    }


    public void setRow(String rowIndex) {
        this.row = rowIndex;
    }


    public void setColumn(String columnNameOrIndex) {
        this.column = columnNameOrIndex;
    }


    static int findMenuIndex(JMenuBar menuBar, String label) {
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu currentMenu = menuBar.getMenu(i);
            if (currentMenu.getText().equals(label)) {
                return i;
            }
        }

        throw new GuiFindException("Menu non trouvé : " + label);
    }


    static int findMenuItemIndex(JPopupMenu popupMenu, String label) {
        for (int i = 0; i < popupMenu.getComponentCount(); i++) {
            Component comp = popupMenu.getComponent(i);
            if (!(comp instanceof JMenuItem)) {
                continue;
            }

            JMenuItem menuItem = (JMenuItem)comp;
            if (menuItem.getText().equals(label)) {
                return i;
            }
        }

        throw new GuiFindException("MenuItem non trouvé : " + label);
    }


    private void assertMenuEnableState(JMenuBar menuBar, String[] labels) {
        int[] indexes = new int[labels.length];
        indexes[0] = findMenuIndex(menuBar, labels[0]);
        JMenu currentMenu = menuBar.getMenu(indexes[0]);

        JPopupMenu popupMenu = currentMenu.getPopupMenu();

        if (isAlreadyOk(currentMenu)) {
            return;
        }

        if (findMenuItem(labels, popupMenu, 1, currentMenu)) {
            return;
        }

        throw new GuiFindException("MenuItem non trouvé : " + labels);
    }


    private boolean findMenuItem(String[] labels, JPopupMenu popupMenu, int startIndex, JMenu currentMenu) {
        int[] indexes = new int[labels.length];
        for (int i = startIndex; i < labels.length; i++) {
            indexes[i] = findMenuItemIndex(popupMenu, labels[i]);

            if (i < (labels.length - 1)) {
                Component child = popupMenu.getComponent(indexes[i]);
                if (!(child instanceof JMenu)) {
                    throw new GuiFindException(labels[i] + " n'est pas un sous-menu");
                }

                if (currentMenu != null && isAlreadyOk(currentMenu)) {
                    return true;
                }
                JMenu childMenu = (JMenu)child;
                popupMenu = childMenu.getPopupMenu();
            }
            else {
                Component child = popupMenu.getComponent(indexes[i]);
                if (!(child instanceof JMenuItem)) {
                    throw new GuiFindException("MenuItem non trouvé : " + labels[i]);
                }
                proceed((JMenuItem)child);
                return true;
            }
        }
        return false;
    }


    private boolean isAlreadyOk(JMenu currentMenu) {
        if ("false".equals(expected) && !currentMenu.isEnabled()) {
            return true;
        }
        if ("true".equals(expected) && !currentMenu.isEnabled()) {
            throwBadEnableStateException("false");
        }
        return false;
    }
}
