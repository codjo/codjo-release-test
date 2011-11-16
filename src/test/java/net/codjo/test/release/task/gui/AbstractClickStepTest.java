package net.codjo.test.release.task.gui;

import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.codjo.test.common.LogString;

import java.security.InvalidParameterException;
import java.awt.event.*;

public abstract class AbstractClickStepTest extends JFCTestCase {
    protected static final String TREE_NAME = "Tree";
    protected static final String BUTTON_NAME = "DetailButton";
    protected static final String BUTTON_LABEL = "Détails";
    protected static final String LABEL = "Labelle";
    protected static final String POPUP_BUTTON_NAME = "PopupButton";
    protected static final String POPUP_BUTTON_LABEL = "Popup";
    protected AbstractButtonClickStep step;
    protected MyActionListener menuOuvrirListener;
    protected MyActionListener menuDocumentListener;
    protected MyActionListener menuLespaceListener;
    protected MyActionListener buttonListener;
    protected JFrame frame;
    protected JButton button;
    protected JTable table;
    protected MyMouseListener myMouseListener;
    protected boolean showFrameCalled = false;
    protected JMenu menuFichier;
    protected JMenuItem openItem;
    protected JList list;
    protected JButton popupButton;
    protected JPopupMenu popupMenu;
    protected LogString menuSelectionLog = new LogString();
    protected AbstractAction subMenu1;
    protected AbstractAction subMenu2;
    protected PopupMenuButtonHelper popupHelper = new PopupMenuButtonHelper();
    protected JTree tree;
    protected LogString treeMouseLog;


    abstract protected AbstractButtonClickStep createClickStep();

    @Override
    protected void setUp() throws Exception {
        showFrameCalled = false;
        step = createClickStep();
        step.setTimeout(1);
        step.setWaitingNumber(10);
        treeMouseLog = new LogString();
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (showFrameCalled) {
            TestHelper.cleanUp(this);
            showFrameCalled = false;
        }
    }

    public void test_clickingUsingBadLabel() throws Exception {
        showFrame();

        step.setLabel("je ne suis pas le bon label!");
        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (Exception e) {
            assertEquals("Le composant 'je ne suis pas le bon label!' est introuvable.", e.getMessage());
        }
    }


    


    public void test_nok_noFrame() throws Exception {
        // Dans ce test, on n'appelle pas showFrame()
        step.setMenu("Fichier:Ouvrir");
        try {
            step.proceed(new TestContext(this));
            fail("Le clic n'a pas échoué bien qu'il n'y ait pas de frame.");
        }
        catch (GuiFindException ex) {
            ; // OK
        }
    }


    public void test_nok_timeout() throws Exception {
        // Dans ce test, on n'appelle pas showFrame()
        step.setMenu("Fichier:Ouvrir");
        long startTime = System.currentTimeMillis();
        step.setTimeout(1);
        step.setWaitingNumber(10);
        try {
            step.proceed(new TestContext(this));
            fail("Le clic n'a pas échoué bien qu'il n'y ait pas de frame.");
        }
        catch (GuiFindException ex) {
            long stopTime = System.currentTimeMillis();
            long elaps = stopTime - startTime;

            //verification que le timeout est compris entre le timeout est le timeout * 2
            // borné à 5s
            long maxTimeOut = Math.min(step.getTimeout() * 2, step.getTimeout() + 5);
            assertTrue("Timeout inférieur au timeout attendu (" + elaps + " contre " + step.getTimeout()
                       + "000)", elaps >= step.getTimeout() * 1000);
            assertTrue("Timeout trop grand ! (" + elaps + " contre " + maxTimeOut + "000)",
                       elaps < (maxTimeOut * 1000));
        }
    }


    public void test_nok_badMenu() throws Exception {
        showFrame();

        try {
            step.setMenu("N'importe quoi");
            step.proceed(new TestContext(this));
            fail("Le clic doit échouer si la syntaxe de l'élément de menu n'est pas bonne.");
        }
        catch (InvalidParameterException ex) {
            ; // OK
        }
    }


    public void test_nok_noMenuBar() throws Exception {
        showFrame();
        frame.setJMenuBar(null);
        flushAWT();

        try {
            step.setMenu("Fichier:Ouvrir");
            step.proceed(new TestContext(this));
            fail("Le clic doit échouer si la frame n'a pas de menu");
        }
        catch (GuiFindException ex) {
            ; // OK
        }
    }

    public void test_clickOnUnkownColumnThrowsAnException() throws Exception {
        showFrame();

        step.setName(table.getName());
        step.setRow(1);
        step.setColumn("45");

        try {
            step.proceed(new TestContext(this));
        }
        catch (Exception e) {
            assertEquals("La cellule [row=1, col=45] n'existe pas dans la table maTable", e.getMessage());
        }
    }


    public void test_clickOnUnkownTableCellThrowsAnException() throws Exception {
        showFrame();

        step.setName(table.getName());
        step.setRow(45);
        step.setColumn("45");

        try {
            step.proceed(new TestContext(this));
        }
        catch (Exception e) {
            assertEquals("La cellule [row=45, col=45] n'existe pas dans la table maTable", e.getMessage());
        }
    }


   


    /**
     */

    public void test_ok_onLabel() throws Exception {
        showFrame();

        step.setLabel(LABEL);
        step.proceed(new TestContext(this));
        assertEquals(1, myMouseListener.nbCalled);
    }


    public void test_nok_onLabel() throws Exception {
        showFrame();

        final String labelStr = "lebeau";
        try {
            step.setLabel(labelStr);
            step.proceed(new TestContext(this));
            fail("ah bon ça existe ! tu mens !");
        }
        catch (GuiFindException ex) {
            assertEquals("Le composant '" + labelStr + "' est introuvable.", ex.getMessage()); // OK
        }
    }


    public void test_clickOnDisabledMenu() throws Exception {
        showFrame();

        try {
            menuFichier.setEnabled(false);
            step.setMenu("Fichier:Ouvrir");
            step.proceed(new TestContext(this));
            fail("On ne doit pas pouvoir cliquer sur un menu grisé!");
        }
        catch (GuiFindException e) {
            assertEquals("Menu désactivé : Fichier", e.getLocalizedMessage());
        }
    }


    public void test_clickOnDisabledMenuItem() throws Exception {
        showFrame();

        try {
            openItem.setEnabled(false);
            step.setMenu("Fichier:Ouvrir");
            step.proceed(new TestContext(this));
            fail("On ne doit pas pouvoir cliquer sur un menu grisé!");
        }
        catch (GuiFindException e) {
            assertEquals("Menu désactivé : Ouvrir", e.getLocalizedMessage());
        }
    }


    public void test_clickToDisplayAPopupMenuByLabel() throws Exception {
        showFrame();

        step.setLabel(POPUP_BUTTON_LABEL);
        step.setSelect("sous-menu1");
        step.proceed(new TestContext(this));
        assertEquals("sous-menu1 selected", menuSelectionLog.getContent());
    }


    public void test_clickToDisplayAPopupMenuByName() throws Exception {
        showFrame();

        step.setName(POPUP_BUTTON_NAME);
        step.setSelect("sous-menu1");
        step.proceed(new TestContext(this));
        assertEquals("sous-menu1 selected", menuSelectionLog.getContent());
    }


    public void test_clickToDisplayAPopupMenuWithUnkownEntry() throws Exception {
        showFrame();

        step.setLabel(POPUP_BUTTON_LABEL);
        step.setSelect("sous-menu inconnu");
        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (Exception e) {
            assertEquals("MenuItem non trouvé : sous-menu inconnu", e.getMessage());
        }
    }


    public void test_clickToDisplayAPopupMenuWithDisabledEntry() throws Exception {
        showFrame();

        subMenu2.setEnabled(false);
        step.setLabel(POPUP_BUTTON_LABEL);
        step.setSelect("sous-menu2");
        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (Exception e) {
            assertEquals("Menu désactivé : sous-menu2", e.getMessage());
        }
    }


    public void test_clickToDisplayAPopupMenuThatDoesNotAppear() throws Exception {
        showFrame();

        step.setName(POPUP_BUTTON_NAME);
        step.setSelect("sous-menu inconnu");
        popupHelper.setMustShowPopup(false);
        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (Exception e) {
            assertEquals("Le menu contextuel associé au composant 'PopupButton' est introuvable",
                         e.getMessage());
        }
    }


    public void test_clickOnATreepathThatExists() throws Exception {
        showFrame();

        step.setName(TREE_NAME);
        step.setPath("Root:Node1");

        step.proceed(new TestContext(this));
        assertEquals("mouseEntered., mousePressed., mouseReleased., mouseClicked 1 times.",
                     treeMouseLog.getContent());
    }


    public void test_clickOnATreepathThatDoesNotExist() throws Exception {
        showFrame();

        step.setName(TREE_NAME);
        step.setPath("Root:Unkown");

        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (GuiFindException e) {
            assertTrue(e.getMessage().contains("Le noeud 'Root:Unkown' n'existe pas."));
        }
    }


    protected void showFrame() {
        showFrameCalled = true;
        frame = new JFrame("Frame de test pour ClickStep");

        JMenuBar menuBar = new JMenuBar();

        menuFichier = new JMenu("Fichier");
        openItem = new JMenuItem("Ouvrir");
        menuOuvrirListener = new MyActionListener();
        openItem.addActionListener(menuOuvrirListener);
        menuFichier.add(openItem);

        menuFichier.addSeparator();

        JMenu menuNouveau = new JMenu("Nouveau");

        JMenuItem docItem = new JMenuItem("Document");
        menuDocumentListener = new MyActionListener();
        docItem.addActionListener(menuDocumentListener);
        menuNouveau.add(docItem);

        // Test avec une quote dans le libellé du sous-menu
        JMenuItem espItem = new JMenuItem("L'espace");
        menuLespaceListener = new MyActionListener();
        espItem.addActionListener(menuLespaceListener);
        menuNouveau.add(espItem);

        menuFichier.add(menuNouveau);

        menuBar.add(menuFichier);

        frame.setJMenuBar(menuBar);

        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        button = new JButton(BUTTON_LABEL);
        button.setName(BUTTON_NAME);
        buttonListener = new MyActionListener();
        button.addActionListener(buttonListener);
        panel.add(button);

        JLabel label = new JLabel(LABEL);
        myMouseListener = new MyMouseListener();
        label.addMouseListener(myMouseListener);
        panel.add(label);

        popupMenu = new JPopupMenu();
        subMenu1 = new AbstractAction("sous-menu1") {
            public void actionPerformed(ActionEvent event) {
                menuSelectionLog.info("sous-menu1 selected");
            }
        };
        subMenu2 = new AbstractAction("sous-menu2") {
            public void actionPerformed(ActionEvent event) {
                menuSelectionLog.info("sous-menu2 selected");
            }
        };
        popupMenu.add(subMenu1);
        popupMenu.add(subMenu2);
        popupButton = new JButton(POPUP_BUTTON_LABEL);
        popupButton.setName(POPUP_BUTTON_NAME);
        popupButton.addMouseListener(popupHelper);
        panel.add(popupButton);

        table = new JTable(new Object[][]{
              {"toto", "titi", "tutu"},
              {"pim", "pam", "poum"}
        }, new Object[]{"A", "B", "C"});
        table.setName("maTable");
        table.setCellEditor(new DefaultCellEditor(new JTextField()));
        panel.add(new JScrollPane(table));

        list = new JList(new Object[]{"titi", "tata", "tutu"});
        list.setName("maListe");
        panel.add(list);

        tree = createJTree();
        panel.add(tree);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }


    private JTree createJTree() {
        tree = new JTree();
        tree.setName(TREE_NAME);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("Node1");
        node1.add(new DefaultMutableTreeNode("Node1.1"));
        node1.add(new DefaultMutableTreeNode("Node1.2"));
        DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("Node2");
        root.add(node1);
        root.add(node2);

        tree.setModel(new DefaultTreeModel(root));
        tree.addMouseListener(new TreeMouseListener(treeMouseLog));

        return tree;
    }

    protected abstract boolean isCorrectClickEvent(MouseEvent event);

    private class TreeMouseListener implements MouseListener {
        private LogString treeMouseLog;


        private TreeMouseListener(LogString treeMouseLog) {
            this.treeMouseLog = treeMouseLog;
        }


        public void mouseClicked(MouseEvent e) {
            treeMouseLog.info("mouseClicked " + e.getClickCount() + " times.");
        }


        public void mousePressed(MouseEvent e) {
            treeMouseLog.info("mousePressed.");
        }


        public void mouseReleased(MouseEvent e) {
            treeMouseLog.info("mouseReleased.");
        }


        public void mouseEntered(MouseEvent e) {
            treeMouseLog.info("mouseEntered.");
        }


        public void mouseExited(MouseEvent e) {
            treeMouseLog.info("mouseExited.");
        }
    }

    class MyActionListener implements ActionListener {
        int nbCalled = 0;


        public void actionPerformed(ActionEvent event) {
            nbCalled++;
        }
    }

    class MyMouseListener extends MouseAdapter {
        int nbCalled = 0;


        @Override
        public void mouseClicked(MouseEvent event) {
            if (isCorrectClickEvent(event)) {
                nbCalled++;
            }
        }
    }

    class PopupMenuButtonHelper extends MouseAdapter {
        private boolean mustShowPopup = true;


        public void setMustShowPopup(boolean mustShowPopup) {
            this.mustShowPopup = mustShowPopup;
        }


        @Override
        public void mousePressed(MouseEvent event) {
            if (mustShowPopup && isCorrectClickEvent(event)) {
                popupMenu.show(event.getComponent(), event.getX(), event.getY());
            }
        }
    }

}
