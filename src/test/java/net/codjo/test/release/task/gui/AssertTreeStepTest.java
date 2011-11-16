/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
/**
 * Classe de test de {@link AssertTreeStep}.
 */
public class AssertTreeStepTest extends JFCTestCase {
    private AssertTreeStep step;


    @Override
    protected void setUp() throws Exception {
        step = new AssertTreeStep();
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
        assertEquals(null, step.getPath());
    }


    public void test_nok_treeNotFound() throws Exception {
        JTree tree = new JTree();
        showFrame(tree);

        TestContext context = new TestContext(this);
        step.setName("UnArbreInconnu");
        step.setPath("root");
        try {
            step.proceed(context);
            fail("Pas d'erreur alors que l'arbre n'existe pas.");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
    }


    public void test_ok() throws Exception {
        // Création d'un arbre avec un noeud racine sans feuille
        JTree tree = new JTree();
        DefaultTreeModel treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("rootName"));
        tree.setModel(treeModel);
        showFrame(tree);

        step.setName("treeName");
        step.setPath("rootName");
        step.proceed(new TestContext(this));
    }


    public void test_treeWithRootOnly_ok() throws Exception {
        createTreeWithRootOnly();

        step.setName("treeName");
        step.setPath("rootName");
        TestContext context = new TestContext(this);
        step.proceed(context);
    }


    private void createTreeWithRootOnly() {
        JTree tree = new JTree();

        // Noeud racine
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("rootName");

        // Construction de l'arbre
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        tree.setModel(treeModel);
        showFrame(tree);
    }


    /**
     * Test sur le path non renseigné
     */
    public void test_tree_nok_noPath() throws Exception {
        TestContext context = new TestContext(this);
        createTreeWithRootOnly();

        step.setName("treeName");
        step.setPath(null);
        step.setExists(false);

        try {
            step.proceed(context);
            fail("Proceed n'a pas echoué alors que le path n'est pas renseigné.");
        }
        catch (GuiConfigurationException ex) {
            ; // cas normal
        }
    }


    public void test_treeWithChilds() throws Exception {
        showFrame(createTreeWithChilds());

        step.setName("treeName");
        step.setPath("rootName:child2");
        step.setExists(true);
        TestContext context = new TestContext(this);
        step.proceed(context);

        step.setPath("rootName:child11");
        step.setExists(false);
        step.proceed(context);

        step.setPath("rootName:child8");
        step.setExists(false);
        step.proceed(context);

        try {
            step.setPath("rootName:child8");
            step.setExists(true);
            step.proceed(context);
            fail("Le test doit echouer car le noeud n'existe pas");
        }
        catch (GuiAssertException guiException) {
            assertEquals("Le noeud 'rootName:child8' n'existe pas.\n"
                         + "Liste des noeuds présents :\n" + "-rootName\n" + "-rootName:child1\n"
                         + "-rootName:child1:child11\n" + "-rootName:child2\n", guiException.getMessage());
        }

        step.setPath("rootName");
        step.setExists(true);
        step.proceed(context);
    }


    public void test_treeWithProperties() throws Exception {
        showFrame(createTreeWithChilds());

        step.setName("treeName");
        step.setPath("rootName:child${two}");
        step.setExists(true);
        TestContext context = new TestContext(this);
        context.setProperty("two", "2");
        step.proceed(context);
    }


    public void test_mode() throws Exception {
        JTree tree = createTreeWithChilds();
        tree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree,
                                                          Object value,
                                                          boolean sel,
                                                          boolean expanded,
                                                          boolean leaf,
                                                          int row,
                                                          boolean focus) {
                return super.getTreeCellRendererComponent(tree,
                                                          value.toString() + " (" + value.toString() + ")",
                                                          sel, expanded, leaf, row, focus);
            }
        });

        showFrame(tree);
        step.setName("treeName");
        TestContext context = new TestContext(this);

        step.setPath("rootName (rootName):child2 (child2)");
        step.setExists(true);
        step.proceed(context);

        step.setPath("rootName (rootName):child11 (child11)");
        step.setExists(false);
        step.proceed(context);

        step.setMode(AbstractGuiStep.MODEL_MODE);
        step.setPath("rootName:child2");
        step.setExists(true);
        step.proceed(context);

        step.setPath("rootName:child11");
        step.setExists(false);
        step.proceed(context);

        step.setMode(AbstractGuiStep.DISPLAY_MODE);
        step.setPath("rootName (rootName):child2 (child2)");
        step.setExists(true);
        step.proceed(context);

        step.setPath("rootName (rootName):child11 (child11)");
        step.setExists(false);
        step.proceed(context);

        step.setMode("plusALaMode");
        step.setPath("rootName (rootName):child2 (child2)");
        step.setExists(true);
        try {
            step.proceed(context);
            fail();
        }
        catch (Exception e) {
            assertEquals("Invalid value of 'mode' attribute : must be in {\"display\", \"model\"}.",
                         e.getMessage());
        }
    }


    public void test_assertRow() throws Exception {
        JTree tree = createTreeWithChilds();
        showFrame(tree);

        TreeUtils.expandSubtree(tree, new TreePath(tree.getModel().getRoot()));

        step.setName("treeName");
        step.setPath("rootName");
        step.setExists(true);
        step.setRow(0);
        TestContext context = new TestContext(this);
        step.proceed(context);

        step.setPath("rootName:child1");
        step.setRow(1);
        step.proceed(context);

        step.setPath("rootName:child1:child11");
        step.setRow(2);
        step.proceed(context);

        step.setPath("rootName:child2");
        step.setRow(3);
        step.proceed(context);

        try {
            step.setPath("rootName:child1");
            step.setRow(3);
            step.proceed(context);
            fail();
        }
        catch (GuiAssertException guiException) {
            assertEquals("Le noeud 'rootName:child1' ne se situe pas à la position '3' "
                         + "mais à la position '1'", guiException.getMessage());
        }
    }


    private JTree createTreeWithChilds() {
        JTree tree = new JTree();

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

        return tree;
    }


    private void showFrame(JTree tree) {
        JFrame frame = new JFrame("Test JTree");

        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        tree.setName("treeName");
        panel.add(tree);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }
}
