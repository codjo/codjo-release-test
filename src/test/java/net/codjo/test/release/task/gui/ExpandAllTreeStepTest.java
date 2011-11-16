package net.codjo.test.release.task.gui;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
/**
 *
 */
public class ExpandAllTreeStepTest extends JFCTestCase {
    private ExpandAllTreeStep step;


    @Override
    protected void setUp() throws Exception {
        step = new ExpandAllTreeStep();
        step.setTimeout(1);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    public void test_expandAllTree() throws Exception {
        JTree tree = createTreeWithChilds();
        showFrame(tree);
        assertTrue(tree.isExpanded(0));
        assertFalse(tree.isExpanded(1));
        assertFalse(tree.isExpanded(2));
        assertFalse(tree.isExpanded(3));
        assertFalse(tree.isExpanded(4));
        assertFalse(tree.isExpanded(5));

        step.setName("treeName");

        TestContext context = new TestContext(this);
        step.proceed(context);

        assertTrue(tree.isExpanded(0));
        assertTrue(tree.isExpanded(1));
        assertTrue(tree.isExpanded(2));
        assertFalse("c'est une feuille !", tree.isExpanded(3));
        assertTrue(tree.isExpanded(4));
        assertFalse("c'est une feuille !", tree.isExpanded(5));
    }


    public void test_ko() throws Exception {
        JTree tree = createTreeWithChilds();
        showFrame(tree);

        step.setName("unknownTree");

        try {
            TestContext context = new TestContext(this);
            step.proceed(context);
        }
        catch (Exception e) {
            assertEquals("L'arbre 'unknownTree' est introuvable.", e.getMessage());
        }
    }


    private JTree createTreeWithChilds() {
        JTree tree = new JTree();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("rootName");

        DefaultMutableTreeNode child1 = new DefaultMutableTreeNode("child1");
        root.add(child1);
        DefaultMutableTreeNode child11 = new DefaultMutableTreeNode("child11");
        child1.add(child11);
        child11.add(new DefaultMutableTreeNode("child111"));
        DefaultMutableTreeNode child2 = new DefaultMutableTreeNode("child2");
        root.add(child2);
        child2.add(new DefaultMutableTreeNode("child21"));

        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        tree.setModel(treeModel);

        return tree;
    }


    private void showFrame(JTree tree) {
        JFrame frame = new JFrame("Test JTree");

        JPanel panel = new JPanel();
        frame.setContentPane(panel);
        panel.setPreferredSize(new Dimension(100, 100));

        tree.setName("treeName");
        panel.add(tree);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }
}
