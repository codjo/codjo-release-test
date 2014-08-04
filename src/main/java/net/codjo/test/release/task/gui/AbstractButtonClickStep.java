package net.codjo.test.release.task.gui;

import java.awt.Component;
import java.awt.Rectangle;
import java.security.InvalidParameterException;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import junit.extensions.jfcunit.eventdata.JMenuMouseEventData;
import junit.extensions.jfcunit.eventdata.MouseEventData;
import junit.extensions.jfcunit.eventdata.PathData;
import junit.extensions.jfcunit.finder.Finder;
import junit.extensions.jfcunit.finder.JMenuItemFinder;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
import net.codjo.test.release.task.gui.finder.LabeledAndNamedFinder;
import net.codjo.test.release.task.gui.finder.LabeledJComponentFinder;
import net.codjo.test.release.task.gui.metainfo.ClickDescriptor;
import net.codjo.test.release.task.gui.metainfo.Introspector;

/**
 *
 */
public abstract class AbstractButtonClickStep extends AbstractClickStep {
    private static final String MATCHER_EQUALS = "equals";
    private static final String MATCHER_CONTAINS = "contains";

    private String menu;
    private String label;
    private String matcher = MATCHER_EQUALS;
    private int row = 0;
    private String path;
    private boolean openParentNode = false;
    private String mode;


    public String getMenu() {
        return menu;
    }


    public void setMenu(String menu) {
        this.menu = menu;
    }


    public void setRow(int row) {
        this.row = row;
    }


    public int getRow() {
        return row;
    }


    public String getLabel() {
        return label;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    public String getMatcher() {
        return matcher;
    }


    public void setMatcher(String matcher) {
        this.matcher = matcher;
    }


    public String getPath() {
        return path;
    }


    public void setPath(String path) {
        this.path = path;
    }


    public String getMode() {
        return mode;
    }


    public void setMode(String mode) {
        this.mode = mode;
    }


    @Override
    public void proceed(TestContext context) {
        if (menu != null) {
            proceedMenu(context);
        }
        else if (name != null && label != null) {
            proceedComponent(context, label, new LabeledAndNamedFinder(name, label));
        }
        else if (name != null) {
            proceedComponent(context, name, new NamedComponentFinder(JComponent.class, name));
        }
        else {
            proceedComponent(context, label, new LabeledJComponentFinder(label));
        }
    }


    @Override
    protected int getFinderOperation() {
        return MATCHER_CONTAINS.equals(matcher) ? Finder.OP_CONTAINS : Finder.OP_EQUALS;
    }


    private void proceedMenu(TestContext context) {
        StringTokenizer tok = new StringTokenizer(menu, ":");
        if (tok.countTokens() < 2) {
            throw new InvalidParameterException("L'identifiant '" + menu
                                                + "' ne respecte pas la syntaxe 'menu:sous-menu:element'.");
        }

        String[] labels = new String[tok.countTokens()];

        for (int i = 0; tok.hasMoreTokens(); i++) {
            labels[i] = tok.nextToken();
        }

        PathData path = new PathData(labels);

        String menuName = labels[0];
        JMenuItemFinder finder = new JMenuItemFinder(menuName);
        JMenu currentMenu = (JMenu)findOnlyOne(finder, context);

        if (currentMenu == null) {
            throw new GuiFindException("Menu non trouvé : " + menuName);
        }

        JMenuBar menuBar = (JMenuBar)path.getRoot(currentMenu);
        int[] indexes = getPathIndexes(menuBar, labels);
        JMenuMouseEventData eventData = new JMenuMouseEventData(context.getTestCase(), menuBar, indexes,
                                                                JMenuMouseEventData.DEFAULT_NUMBEROFCLICKS,
                                                                getMouseModifiers(),
                                                                JMenuMouseEventData.DEFAULT_ISPOPUPTRIGGER,
                                                                JMenuMouseEventData.DEFAULT_SLEEPTIME);
        eventData.setSleepTime(5000);
        context.getHelper().enterClickAndLeave(eventData);
    }


    private static int[] getPathIndexes(JMenuBar menuBar, String[] labels) {
        int[] indexes = new int[labels.length];
        indexes[0] = findMenuIndex(menuBar, labels[0]);
        JPopupMenu popupMenu = menuBar.getMenu(indexes[0]).getPopupMenu();

        String[] labelsWithoutFirst = new String[labels.length - 1];
        System.arraycopy(labels, 1, labelsWithoutFirst, 0, labels.length - 1);
        int[] pathIndexes = getPathIndexes(popupMenu, labelsWithoutFirst);
        System.arraycopy(pathIndexes, 0, indexes, 1, pathIndexes.length);

        return indexes;
    }


    static int[] getPathIndexes(JPopupMenu popupMenu, String[] labels) {
        int[] indexes = new int[labels.length];
        for (int i = 0; i < labels.length; i++) {
            indexes[i] = findMenuItemIndex(popupMenu, labels[i]);

            if (i < (labels.length - 1)) {
                Component child = popupMenu.getComponent(indexes[i]);
                if (!(child instanceof JMenu)) {
                    throw new GuiFindException(labels[i] + " n'est pas un sous-menu");
                }

                JMenu childMenu = (JMenu)child;
                popupMenu = childMenu.getPopupMenu();
            }
        }

        return indexes;
    }


    private static int findMenuIndex(JMenuBar menuBar, String label) {
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu currentMenu = menuBar.getMenu(i);
            if (currentMenu.getText().equals(label)) {
                if (!currentMenu.isEnabled()) {
                    throw new GuiFindException("Menu désactivé : " + label);
                }

                return i;
            }
        }

        throw new GuiFindException("Menu non trouvé : " + label);
    }


    private static int findMenuItemIndex(JPopupMenu popupMenu, String label) {
        for (int i = 0; i < popupMenu.getComponentCount(); i++) {
            Component comp = popupMenu.getComponent(i);
            if (!(comp instanceof JMenuItem)) {
                continue;
            }

            JMenuItem menuItem = (JMenuItem)comp;
            if (menuItem.getText().equals(label)) {
                if (!menuItem.isEnabled()) {
                    throw new GuiFindException("Menu désactivé : " + label);
                }

                return i;
            }
        }

        throw new GuiFindException("MenuItem non trouvé : " + label);
    }


    @Override
    protected void setReferencePointIfNeeded(MouseEventData eventData, Component component) {
        if (descriptor != null) {
            super.setReferencePointIfNeeded(eventData, component);
        }
        else {
            Rectangle cellRect = null;
            if (JTable.class.isInstance(component)) {
                JTable table = (JTable)component;
                int columnNumber = TableTools.searchColumn(table, column);
                TableTools.checkTableCellExists(table, row, columnNumber);
                cellRect = table.getCellRect(row, columnNumber, true);
            }
            else if (JList.class.isInstance(component)) {
                JList list = (JList)component;
                ListTools.checkRowExists(list, row);
                cellRect = list.getCellBounds(row, row);
            }

            if (cellRect != null) {
                eventData.setPosition(MouseEventData.CUSTOM);
                eventData.setReferencePoint(cellRect.getLocation());
            }
        }
    }


    @Override
    protected void initDescriptor(Component comp) {
        if (path != null) {
            descriptor = new ClickDescriptor() {
                public Component getComponentToClick(Component comp, AbstractClickStep step) {
                    return comp;
                }


                public void setReferencePointIfNeeded(MouseEventData eventData,
                                                      Component component,
                                                      AbstractClickStep step) {
                    JTree tree = (JTree)component;
                    if (getPath() == null) {
                        throw new GuiConfigurationException("Le path n'a pas été renseigné.");
                    }

                    final TreePath treePath = TreeUtils
                          .convertIntoTreePath(tree, getPath(), TreeStepUtils.getConverter(getMode()));
                    TreePath parentTreePath = treePath.getParentPath();
                    if (!tree.isExpanded(parentTreePath)) {
                        if (!openParentNode) {
                            String parentPath = TreeUtils.convertPath(tree,
                                                                      parentTreePath,
                                                                      TreeStepUtils.getConverter(getMode()));
                            throw new GuiConfigurationException("The parent node '" + parentPath + "' is not opened.");
                        }
                        tree.expandPath(parentTreePath);
                    }
                    Rectangle cellRect = tree.getPathBounds(treePath);
                    if (cellRect != null) {
                        cellRect.x += cellRect.width / 2;
                        cellRect.y += cellRect.height / 2;

                        eventData.setPosition(MouseEventData.CUSTOM);
                        eventData.setReferencePoint(cellRect.getLocation());
                    }
                }
            };
        }
        else {
            descriptor = Introspector.getTestBehavior(comp.getClass(), ClickDescriptor.class);
        }
    }


    public void setOpenParentNode(boolean openParentNode) {
        this.openParentNode = openParentNode;
    }
}
