/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
/**
 * Classe de test de {@link AssertSelectedStep}.
 */
public class AssertSelectedStepTest extends JFCTestCase {
    private AssertSelectedStep step;
    private JTable table;
    private JTree tree;
    private DefaultMutableTreeNode child11;
    private DefaultMutableTreeNode child2;


    @Override
    protected void setUp() throws Exception {
        step = new AssertSelectedStep();
        step.setTimeout(1);
        step.setDelay(5);
        step.setWaitingNumber(10);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    public void test_defaults() throws Exception {
        assertEquals(null, step.getName());
        assertEquals(-1, step.getRow());
        assertEquals(-1, step.getColumn());
        assertTrue(step.isExpected());
    }


    public void test_ok_table() throws Exception {
        showFrame();

        step.setName("PortfolioList");
        step.setRow(2);
        step.setExpected(true);
        step.proceed(new TestContext(this));
    }


    public void test_ok_table_cell() throws Exception {
        showFrame();

        // Sélection de la cellule de coordonnées (1,2)
        table.setCellSelectionEnabled(true);
        table.changeSelection(1, 2, false, false);

        TestContext context = new TestContext(this);
        step.setName("PortfolioList");

        step.setRow(1);
        step.setColumn(2);
        step.setExpected(true);
        step.proceed(context);

        step.setRow(1);
        step.setColumn(0);
        step.setExpected(false);
        step.proceed(context);

        step.setRow(2);
        step.setColumn(0);
        step.setExpected(false);
        step.proceed(context);

        step.setRow(2);
        step.setColumn(1);
        step.setExpected(false);
        step.proceed(context);
    }


    public void test_ok_list() throws Exception {
        showFrame();

        step.setName("FunctionList");
        step.setRow(1);
        step.setExpected(true);
        step.proceed(new TestContext(this));
    }


    public void test_nok_componentNotFound() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("UneTableInconnue");
        step.setRow(1);
        step.setExpected(true);

        try {
            step.proceed(context);
            fail("Pas d'erreur alors que le composant n'existe pas.");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
    }


    public void test_nok_table_outOfRange() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("PortfolioList");
        step.setRow(100);
        step.setExpected(true);

        try {
            step.proceed(context);
            fail("Pas d'erreur alors que row est invalide.");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
    }


    public void test_nok_list_outOfRange() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("FunctionList");
        step.setRow(100);
        step.setExpected(true);

        try {
            step.proceed(context);
            fail("Pas d'erreur alors que row est invalide.");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
    }


    public void test_nok_table_badStatus() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("PortfolioList");
        step.setRow(2);
        step.setExpected(false);

        try {
            step.proceed(context);
            fail("Pas d'erreur alors que la valeur n'est pas celle attendue.");
        }
        catch (GuiAssertException ex) {
            ; // Cas normal
        }
    }


    public void test_nok_list_badStatus() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("FunctionList");
        step.setRow(1);
        step.setExpected(false);

        try {
            step.proceed(context);
            fail("Pas d'erreur alors que la valeur n'est pas celle attendue.");
        }
        catch (GuiAssertException ex) {
            ; // Cas normal
        }
    }


    public void test_nok_unknownComponent() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("Parasite");
        step.setRow(2);

        try {
            step.proceed(context);
            fail("Type de composant non supporté.");
        }
        catch (GuiConfigurationException ex) {
            ; // Cas normal
        }
    }


    public void test_ok_tree() throws Exception {
        showFrame();

        step.setName("zeTree");
        step.setPath("rootName:child1:child11");

        tree.setSelectionPath(new TreePath(child11.getPath()));

        step.proceed(new TestContext(this));
    }


    public void test_ko_bad_selection_path_tree() throws Exception {
        showFrame();

        step.setName("zeTree");
        step.setPath("rootName:child1");

        tree.setSelectionPath(new TreePath(child11.getPath()));

        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (GuiAssertException ex) {
            assertEquals(
                  "Le noeud sélectionné ne correspond pas : attendu = '[rootName, child1]' obtenu = '[rootName, child1, child11]'",
                  ex.getMessage());
        }
    }


    public void test_ko_unknown_expected_path_tree() throws Exception {
        showFrame();

        step.setName("zeTree");
        step.setPath("rootName:child1:unknown");

        tree.setSelectionPath(new TreePath(child11.getPath()));

        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (GuiFindException ex) {
            assertEquals("Le noeud 'rootName:child1:unknown' n'existe pas.\n"
                         + "Liste des noeuds présents :\n"
                         + "-rootName\n"
                         + "-rootName:child1\n"
                         + "-rootName:child1:child11\n"
                         + "-rootName:child2\n", ex.getMessage());
        }
    }


    public void test_ok_empty_selection_expected_tree() throws Exception {
        showFrame();

        step.setName("zeTree");
        step.setExpected(false);

        tree.clearSelection();

        step.proceed(new TestContext(this));
    }


    public void test_ko_no_selection_expected_tree() throws Exception {
        showFrame();

        step.setName("zeTree");
        step.setExpected(false);

        tree.setSelectionPath(new TreePath(child2.getPath()));

        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (GuiAssertException ex) {
            assertEquals("Aucune sélection dans l'arbre est attendue.", ex.getMessage());
        }
    }


    public void test_ok_at_least_one_selection_expected_tree() throws Exception {
        showFrame();

        step.setName("zeTree");

        tree.setSelectionPath(new TreePath(child2.getPath()));

        step.proceed(new TestContext(this));
    }


    public void test_ko_at_least_one_selection_expected_tree() throws Exception {
        showFrame();

        step.setName("zeTree");

        tree.clearSelection();

        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (GuiAssertException ex) {
            assertEquals("Au moins une sélection dans l'arbre est attendue.", ex.getMessage());
        }
    }


    public void test_ko_no_selection() throws Exception {
        showFrame();

        step.setName("zeTree");
        step.setPath("rootName:child1");

        tree.clearSelection();

        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (GuiAssertException ex) {
            assertEquals("Aucun noeud de l'arbre n'est sélectionné.", ex.getMessage());
        }
    }


    public void test_ok_no_specific_selection_expected_tree() throws Exception {
        showFrame();

        step.setName("zeTree");
        step.setPath("rootName:child1");
        step.setExpected(false);

        tree.setSelectionPath(new TreePath(child2.getPath()));

        step.proceed(new TestContext(this));
    }


    public void test_ko_no_specific_selection_expected_tree() throws Exception {
        showFrame();

        step.setName("zeTree");
        step.setPath("rootName:child1:child11");
        step.setExpected(false);

        tree.setSelectionPath(new TreePath(child11.getPath()));

        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (GuiAssertException ex) {
            assertEquals(
                  "Noeud '[rootName, child1, child11]' : attendu = 'non sélectionné' obtenu = 'sélectionné'",
                  ex.getMessage());
        }
    }


    private void showFrame() {
        JFrame frame = new JFrame();

        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        table =
              new JTable(new String[][]{
                    {"line0", "col 0 1"},
                    {"line1", "col 1 1"},
                    {"line2", "col 2 1"},
                    {"line3", "col 3 1"}
              }, new String[]{"Col", "Col 1"});
        table.setName("PortfolioList");
        table.setRowSelectionInterval(2, 3);
        panel.add(table);

        tree = createTreeWithChilds();
        panel.add(tree);

        JList list = new JList(new String[]{"line0", "line1", "line2", "line3"});
        list.setName("FunctionList");
        list.setSelectedIndex(1);
        panel.add(list);

        JPanel parasite = new JPanel();
        parasite.setBackground(Color.yellow);
        parasite.setPreferredSize(new Dimension(40, 30));
        parasite.setName("Parasite");
        panel.add(parasite);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }


    private JTree createTreeWithChilds() {
        JTree tree = new JTree();
        tree.setName("zeTree");

        // Noeud racine
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("rootName");

        // Ajout d'une feuille à la racine
        DefaultMutableTreeNode child1 = new DefaultMutableTreeNode("child1");
        root.add(child1);
        child11 = new DefaultMutableTreeNode("child11");
        child1.add(child11);
        child2 = new DefaultMutableTreeNode("child2");
        root.add(child2);

        // Construction de l'arbre
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        tree.setModel(treeModel);

        return tree;
    }
}
