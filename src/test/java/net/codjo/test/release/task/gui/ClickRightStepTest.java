/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.MenuElement;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
/**
 *
 */
public class ClickRightStepTest extends AbstractClickButtonStepTestCase {
    private JTree tree;
    private JList list;
    private JComponent combo;
    Boolean rightClicked;
    private ClickRightStep clickButtonStep;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        clickButtonStep = new ClickRightStep();
        clickButtonStep.setTimeout(1);
        clickButtonStep.setFinderTimeout(1);
        clickButtonStep.setWaitingNumber(5);
        clickButtonStep.setDisappearTryingNumber(5);
    }


    public void test_clickRightOnCombo() throws Exception {
        createCombo();

        showFrame(combo);
        clickButtonStep.setName("myCombo");
        clickButtonStep.setRow(1);
        clickButtonStep.setSelect("menu2");
        menuSelectionLog.assertContent("");
        clickButtonStep.proceed(new TestContext(this));
        menuSelectionLog.assertContent("menu2 selected");
    }


    public void test_clickRightOnComboRowIncorrect() throws Exception {
        createCombo();

        showFrame(combo);
        clickButtonStep.setName("myCombo");
        clickButtonStep.setRow(3);
        clickButtonStep.setSelect("menu2");
        menuSelectionLog.assertContent("");

        try {
            clickButtonStep.proceed(new TestContext(this));
            fail();
        }
        catch (GuiFindException gfe) {
            assertTrue("La ligne '3' est introuvable dans la combo 'myCombo'".equals(gfe.getMessage()));
        }
    }


    public void test_clickRightOnTextField() throws Exception {
        JTextField textField = new JTextField();
        textField.setColumns(3);
        textField.setName("textField");
        rightClicked = false;

        textField.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                clickDroit(event);
            }
        }

        );
        showFrame(textField);

        clickButtonStep.setName("textField");
        clickButtonStep.proceed(new TestContext(this));
        assertTrue(rightClicked);
    }


    private void clickDroit(MouseEvent event) {
        if (event.getButton() == MouseEvent.BUTTON3) {
            rightClicked = true;
        }
    }


    public void test_selectMenuTabbedPaneOK() throws Exception {
        createTabbedPane();
        showFrame(tabbedPane);

        clickButtonStep.setName("tabbedPane");
        //clickButtonStep.setTabIndex(0);
        clickButtonStep.setTabLabel("tab2");
        clickButtonStep.setSelect("menu1");
        clickButtonStep.proceed(new TestContext(this));

        checkMenuSelection("menu1 selected");
    }


    public void test_selectMenuTableOk() throws Exception {
        createTable();
        showFrame(table);
        clickButtonStep.setName("kingTable");
        clickButtonStep.setRow(0);
        clickButtonStep.setSelect("menu2");
        clickButtonStep.proceed(new TestContext(this));
        checkMenuSelection("menu2 selected");
    }


    public void test_selectMenuBadTable() throws Exception {
        createTable();
        showFrame(table);
        clickButtonStep.setName("QueenTable");
        clickButtonStep.setRow(0);
        clickButtonStep.setSelect("menu2");
        try {
            clickButtonStep.proceed(new TestContext(this));
            fail("la table n'existe pas");
        }
        catch (GuiFindException e) {
            assertEquals("Le composant 'QueenTable' est introuvable.", e.getMessage());
        }
    }


    public void test_selectMenuTableBadRow() throws Exception {
        createTable();
        showFrame(table);
        clickButtonStep.setName("kingTable");
        clickButtonStep.setRow(5);
        clickButtonStep.setSelect("menu2");
        try {
            clickButtonStep.proceed(new TestContext(this));
            fail("la ligne n'existe pas");
        }
        catch (GuiFindException e) {
            assertEquals("La ligne '5' est introuvable dans la table 'kingTable'", e.getMessage());
        }
    }


    public void test_selectMenuTableBadMenuItem()
          throws Exception {
        createTable();
        showFrame(table);
        clickButtonStep.setName("kingTable");
        clickButtonStep.setRow(2);
        clickButtonStep.setSelect("menu99");
        try {
            clickButtonStep.proceed(new TestContext(this));
            fail("la ligne de menu n'existe pas");
        }
        catch (GuiFindException e) {
            assertEquals("MenuItem non trouvé : menu99", e.getMessage());
        }
    }


    public void test_selectMenuTableNoPopupMenu()
          throws Exception {
        createTable();
        showFrame(table);
        table.removeMouseListener(popupHelper);
        clickButtonStep.setName("kingTable");
        clickButtonStep.setRow(2);
        clickButtonStep.setSelect("menu2");
        try {
            clickButtonStep.proceed(new TestContext(this));
            fail("le menu n'est pas apparut");
        }
        catch (GuiFindException e) {
            assertEquals("Le menu contextuel associé au composant 'kingTable' est introuvable",
                         e.getMessage());
        }
    }


    public void test_exceptionOnEmptyPopupTableAndPopupVisibleTrue()
          throws Exception {
        createTable();
        showFrame(table);
        clickButtonStep.setPopupVisible(true);
        popupMenu.removeAll();
        popupHelper.setMustShowPopup(false);

        clickButtonStep.setName("kingTable");
        clickButtonStep.setRow(2);

        try {
            clickButtonStep.proceed(new TestContext(this));
            fail("GuiFindException attendue.");
        }
        catch (GuiFindException e) {
            assertEquals("Le menu contextuel associé au composant 'kingTable' est introuvable",
                         e.getMessage());
        }
    }


    public void test_exceptionPopupTableAndPopupVisibleFalse()
          throws Exception {
        createTable();
        showFrame(table);
        clickButtonStep.setPopupVisible(false);

        clickButtonStep.setName("kingTable");
        clickButtonStep.setSelect("menu2");
        clickButtonStep.setRow(2);

        try {
            clickButtonStep.proceed(new TestContext(this));
            fail("GuiAssertException attendue.");
        }
        catch (GuiAssertException exception) {
            assertEquals(
                  "Le menu contextuel est visible alors que l'attribut 'popup' est initialisé a 'false'",
                  exception.getMessage());
        }
    }


    public void test_assertMenuEnabledForTable() throws Exception {
        createTable();
        showFrame(table);
        clickButtonStep.setName("kingTable");
        clickButtonStep.setRow(0);
        enabledMenu(1);
        assertEnabledMenu("menu1", "true");
        assertEnabledMenu("menu2", "false");
        clickButtonStep.proceed(new TestContext(this));
    }


    public void test_assertMenuPositionForTable() {
        createTable();
        showFrame(table);

        assertMenuPosition(0, 0);
        assertMenuPosition(0, "titreA");
        assertMenuPosition(2, 1);
    }


    public void test_selectMenuTreeOk() throws Exception {
        createTree();
        showFrame(tree);
        clickButtonStep.setName("treeName");
        clickButtonStep.setPath("rootName:child1");
        clickButtonStep.setSelect("menu2");
        clickButtonStep.proceed(new TestContext(this));
        checkMenuSelection("menu2 selected");
    }


    public void test_selectMenuTreeOkDependingOnMode() throws Exception {
        createTree();
        tree.setCellRenderer(new DefaultTreeCellRenderer() {

            @Override
            public Component getTreeCellRendererComponent(JTree tree,
                                                          Object value,
                                                          boolean sel,
                                                          boolean expanded,
                                                          boolean leaf,
                                                          int row,
                                                          boolean focus) {
                return super
                      .getTreeCellRendererComponent(tree, "pouet " + value, sel, expanded, leaf, row, focus);
            }
        });
        showFrame(tree);
        clickButtonStep.setName("treeName");
        clickButtonStep.setPath("pouet rootName:pouet child1");
        clickButtonStep.setSelect("menu2");
        clickButtonStep.proceed(new TestContext(this));
        checkMenuSelection("menu2 selected");

        tree.clearSelection();

        clickButtonStep.setName("treeName");
        clickButtonStep.setMode(AbstractGuiStep.DISPLAY_MODE);
        clickButtonStep.setPath("pouet rootName:pouet child1");
        clickButtonStep.setSelect("menu2");
        clickButtonStep.proceed(new TestContext(this));
        checkMenuSelection("menu2 selected");

        tree.clearSelection();

        clickButtonStep.setMode(AbstractGuiStep.MODEL_MODE);
        clickButtonStep.setPath("rootName:child1");
        clickButtonStep.setSelect("menu2");
        clickButtonStep.proceed(new TestContext(this));
        checkMenuSelection("menu2 selected");

        tree.clearSelection();

        clickButtonStep.setMode("invalidMode");
        clickButtonStep.setPath("rootName:child1");
        clickButtonStep.setSelect("menu2");
        try {
            clickButtonStep.proceed(new TestContext(this));
            fail();
        }
        catch (Exception e) {
            assertEquals("Invalid value of 'mode' attribute : must be in {\"display\", \"model\"}.",
                         e.getMessage());
        }
    }


    public void test_selectMenuTreeBadTree() throws Exception {
        try {
            createTree();
            showFrame(tree);
            clickButtonStep.setName("treeUnknow");
            clickButtonStep.setPath("rootName:child1");
            clickButtonStep.setSelect("menu2");
            clickButtonStep.proceed(new TestContext(this));
            fail("l'arbre n'existe pas");
        }
        catch (GuiFindException e) {
            assertEquals("Le composant 'treeUnknow' est introuvable.", e.getMessage());
        }
    }


    public void test_selectMenuTreeBadNode() throws Exception {
        try {
            createTree();
            showFrame(tree);
            clickButtonStep.setName("treeName");
            clickButtonStep.setPath("rootName:childUnknow");
            clickButtonStep.setSelect("menu2");
            clickButtonStep.proceed(new TestContext(this));
            fail("le node n'existe pas");
        }
        catch (GuiFindException e) {
            String errorWaited =
                  "Le noeud 'rootName:childUnknow' n'existe pas.\n" + "Liste des noeuds présents :\n"
                  + "-rootName\n" + "-rootName:child1\n" + "-rootName:child1:child11\n"
                  + "-rootName:child2\n";
            assertEquals(errorWaited, e.getMessage());
        }
    }


    public void test_selectMenuTreeBadItemMenu() throws Exception {
        try {
            createTree();
            showFrame(tree);
            tree.removeMouseListener(popupHelper);
            clickButtonStep.setName("treeName");
            clickButtonStep.setPath("rootName:child1");
            clickButtonStep.setSelect("menu2");
            clickButtonStep.proceed(new TestContext(this));
            fail("La popup n'existe pas");
        }
        catch (GuiFindException e) {
            assertEquals("Le menu contextuel associé au composant 'treeName' est introuvable",
                         e.getMessage());
        }
    }


    public void test_selectSubMenuTreeItem() throws Exception {
        createTree();
        showFrame(tree);
        clickButtonStep.setName("treeName");
        clickButtonStep.setPath("rootName:child1");
        clickButtonStep.setSelect("menu3:menu31");
        clickButtonStep.proceed(new TestContext(this));
        checkMenuSelection("menu31 selected");
    }


    public void test_exceptionOnEmptyPopupTreeAndPopupVisibleTrue()
          throws Exception {
        createTree();
        showFrame(tree);
        clickButtonStep.setPopupVisible(true);
        popupMenu.removeAll();
        popupHelper.setMustShowPopup(false);

        clickButtonStep.setName("treeName");
        clickButtonStep.setPath("rootName:child1");

        try {
            clickButtonStep.proceed(new TestContext(this));
            fail("GuiFindException attendue.");
        }
        catch (GuiFindException e) {
            assertEquals("Le menu contextuel associé au composant 'treeName' est introuvable",
                         e.getMessage());
        }
    }


    public void test_exceptionPopupTreeAndPopupVisibleFalse()
          throws Exception {
        createTree();
        showFrame(tree);
        clickButtonStep.setPopupVisible(false);

        clickButtonStep.setName("treeName");
        clickButtonStep.setPath("rootName:child1");
        clickButtonStep.setSelect("menu2");

        try {
            clickButtonStep.proceed(new TestContext(this));
            fail("GuiAssertException attendue.");
        }
        catch (GuiAssertException exception) {
            assertEquals(
                  "Le menu contextuel est visible alors que l'attribut 'popup' est initialisé a 'false'",
                  exception.getMessage());
        }
    }


    public void test_assertMenuEnabledForTree() throws Exception {
        createTree();
        showFrame(tree);
        clickButtonStep.setName("treeName");
        clickButtonStep.setPath("rootName:child1");

        enabledMenu(1);

        assertEnabledMenu("menu1", "true");
        AssertEnabledStep assertEnabledStep;

        assertEnabledStep = new AssertEnabledStep();
        assertEnabledStep.setMenu("menu2");
        assertEnabledStep.setExpected("false");

        assertEnabledStep = new AssertEnabledStep();
        assertEnabledStep.setMenu("menu3:menu31");
        assertEnabledStep.setExpected("true");
        clickButtonStep.addAssertEnabled(assertEnabledStep);
        clickButtonStep.proceed(new TestContext(this));
    }


    private void assertEnabledMenu(String menu, String expected) {
        AssertEnabledStep assertEnabledStep = new AssertEnabledStep();
        assertEnabledStep.setMenu(menu);
        assertEnabledStep.setExpected(expected);
        clickButtonStep.addAssertEnabled(assertEnabledStep);
    }


    public void test_selectMenuListOk() throws Exception {
        createList();
        showFrame(list);
        clickButtonStep.setName("kingList");
        clickButtonStep.setRow(0);
        clickButtonStep.setSelect("menu2");
        clickButtonStep.proceed(new TestContext(this));
        checkMenuSelection("menu2 selected");
    }


    public void test_selectMenuBadList() throws Exception {
        createList();
        showFrame(list);
        clickButtonStep.setName("AerosmithTable");
        clickButtonStep.setRow(0);
        clickButtonStep.setSelect("menu2");
        try {
            clickButtonStep.proceed(new TestContext(this));
            fail("la table n'existe pas");
        }
        catch (GuiFindException e) {
            assertEquals("Le composant 'AerosmithTable' est introuvable.", e.getMessage());
        }
    }


    public void test_selectMenuListBadRow() throws Exception {
        createList();
        showFrame(list);
        clickButtonStep.setName("kingList");
        clickButtonStep.setRow(3);
        clickButtonStep.setSelect("menu2");
        try {
            clickButtonStep.proceed(new TestContext(this));
            fail("la ligne n'existe pas");
        }
        catch (GuiFindException e) {
            assertEquals("La ligne '3' est introuvable dans la liste 'kingList'", e.getMessage());
        }
    }


    public void test_selectMenuListBadMenuItem() throws Exception {
        createList();
        showFrame(list);
        clickButtonStep.setName("kingList");
        clickButtonStep.setRow(2);
        clickButtonStep.setSelect("that is wrong dude!");
        try {
            clickButtonStep.proceed(new TestContext(this));
            fail("la ligne de menu n'existe pas");
        }
        catch (GuiFindException e) {
            assertEquals("MenuItem non trouvé : that is wrong dude!", e.getMessage());
        }
    }


    public void test_selectMenuListNoPopupMenu() throws Exception {
        createList();
        showFrame(list);
        list.removeMouseListener(popupHelper);
        clickButtonStep.setName("kingList");
        clickButtonStep.setRow(2);
        clickButtonStep.setSelect("menu2");
        try {
            clickButtonStep.proceed(new TestContext(this));
            fail("le menu n'est pas apparut");
        }
        catch (GuiFindException e) {
            assertEquals("Le menu contextuel associé au composant 'kingList' est introuvable",
                         e.getMessage());
        }
    }


    public void test_exceptionOnEmptyPopupListAndPopupVisibleTrue()
          throws Exception {
        createList();
        showFrame(list);
        clickButtonStep.setPopupVisible(true);
        popupMenu.removeAll();
        popupHelper.setMustShowPopup(false);

        clickButtonStep.setName("kingList");
        clickButtonStep.setRow(2);

        try {
            clickButtonStep.proceed(new TestContext(this));
            fail("GuiFindException attendue.");
        }
        catch (GuiFindException e) {
            assertEquals("Le menu contextuel associé au composant 'kingList' est introuvable",
                         e.getMessage());
        }
    }


    public void test_exceptionPopupListAndPopupVisibleFalse()
          throws Exception {
        createList();
        showFrame(list);
        clickButtonStep.setPopupVisible(false);

        clickButtonStep.setName("kingList");
        clickButtonStep.setSelect("menu2");
        clickButtonStep.setRow(2);

        try {
            clickButtonStep.proceed(new TestContext(this));
            fail("GuiAssertException attendue.");
        }
        catch (GuiAssertException exception) {
            assertEquals(
                  "Le menu contextuel est visible alors que l'attribut 'popup' est initialisé a 'false'",
                  exception.getMessage());
        }
    }


    public void test_assertMenuEnabledForList() throws Exception {
        createList();
        showFrame(list);
        enabledMenu(1);

        clickButtonStep.setName("kingList");
        clickButtonStep.setRow(0);

        assertEnabledMenu("menu1", "true");
        assertEnabledMenu("menu2", "false");
        clickButtonStep.proceed(new TestContext(this));
    }


    public void test_multipleTableWithSamePopupMenu() throws Exception {

        JFrame frame = new JFrame("My frame");
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(600, 400));
        frame.setContentPane(panel);

        createTable();
        table.setName("kingTable1");
        panel.add(table);

        createTable();
        table.setName("kingTable2");
        panel.add(table);

        frame.pack();
        frame.setVisible(true);
        flushAWT();

        clickButtonStep.setName("kingTable1");
        clickButtonStep.setRow(0);
        clickButtonStep.setSelect("menu2");
        clickButtonStep.proceed(new TestContext(this));
        checkMenuSelection("menu2 selected");

        clickButtonStep.setName("kingTable2");
        clickButtonStep.setRow(0);
        clickButtonStep.setSelect("menu1");
        clickButtonStep.proceed(new TestContext(this));
        checkMenuSelection("menu1 selected");
    }


    private void createList() {
        list =
              new JList(new Object[][]{
                    {"1A"},
                    {"2A"},
                    {"3A"}
              });
        list.setName("kingList");

        popupMenu = new JPopupMenu();
        addMenu("menu1");
        addMenu("menu2");

        popupMenu.setInvoker(list);
        popupMenu.setName("MonMenu");
        popupHelper = new PopupHelper();
        list.addMouseListener(popupHelper);
    }


    private void createCombo() {
        combo = new JComboBox(new Object[]{"value1", "value2"});
        combo.setName("myCombo");

        popupMenu = new JPopupMenu();
        addMenu("menu1");
        addMenu("menu2");

        popupMenu.setInvoker(combo);
        popupMenu.setName("MonMenu");
        popupHelper = new PopupHelper();
        combo.addMouseListener(popupHelper);
    }


    private void checkMenuSelection(String expectedMenu) {
        menuSelectionLog.assertContent(expectedMenu);
        menuSelectionLog.clear();
    }


    private void createTree() {
        tree = new JTree();

        // Noeud racine
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("rootName");

        // Ajout d'une feuille à la racine
        DefaultMutableTreeNode child1 = new DefaultMutableTreeNode("child1");
        root.add(child1);
        child1.add(new DefaultMutableTreeNode("child11"));
        root.add(new DefaultMutableTreeNode("child2"));

        // Construction de l'arbre
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        tree.setModel(treeModel);
        tree.setName("treeName");
        popupMenu = new JPopupMenu();
        addMenu("menu1");
        popupMenu.addSeparator();
        addMenu("menu2");
        popupMenu.addSeparator();
        JMenu menu3 = new JMenu("menu3");
        popupMenu.add(menu3);
        addMenuItem(menu3, "menu31");
        popupMenu.setInvoker(tree);
        popupHelper = new PopupHelper();
        tree.addMouseListener(popupHelper);
    }


    private void enabledMenu(int row) {
        MenuElement[] menu = popupMenu.getSubElements();
        JMenuItem item = (JMenuItem)menu[row];
        item.setEnabled(false);
    }


    private void assertMenuPosition(int row, int column) {
        assertRowMenuPosition(row, "" + column);
        Point point = popupHelper.getLastPointClicked();
        assertEquals(column, table.columnAtPoint(point));
    }


    private void assertMenuPosition(int row, String column) {
        assertRowMenuPosition(row, column);
        Point point = popupHelper.getLastPointClicked();
        assertEquals(column, table.getColumnName(table.columnAtPoint(point)));
    }


    private void assertRowMenuPosition(int row, String column) {
        clickButtonStep.setName("kingTable");
        clickButtonStep.setRow(row);
        clickButtonStep.setColumn(column);
        clickButtonStep.proceed(new TestContext(this));
        Point point = popupHelper.getLastPointClicked();
        assertEquals(row, table.rowAtPoint(point));
    }
}
