/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import static net.codjo.test.release.task.gui.TreeUtils.MODEL_CONVERTER;
import static net.codjo.test.release.task.gui.TreeUtils.RENDERER_ASSERT_CONVERTER;
import static net.codjo.test.release.task.gui.TreeUtils.RENDERER_CONVERTER;
import static net.codjo.test.release.task.gui.TreeUtils.convertIntoTreePath;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import junit.framework.TestCase;
/**
 *
 */
public class TreeUtilsTest extends TestCase {
    private JTree tree;
    private DefaultMutableTreeNode node1;


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        tree = new JTree();
        tree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree,
                                                          Object value,
                                                          boolean sel,
                                                          boolean expanded,
                                                          boolean leaf,
                                                          int row,
                                                          boolean focus) {
                JLabel label =
                      (JLabel)super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
                                                                 focus);
                label.setText("renderer " + label.getText());
                return label;
            }
        });

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        node1 = new DefaultMutableTreeNode("node1");
        root.add(node1);
        root.add(new DefaultMutableTreeNode("node2"));
        tree.setModel(new DefaultTreeModel(root));
    }


    public void test_convertIntoTreePathBasedOnModel()
          throws Exception {
        assertEquals(new TreePath(node1.getPath()), convertIntoTreePath(tree, "root:node1", MODEL_CONVERTER));

        try {
            convertIntoTreePath(tree, "root:unknown", MODEL_CONVERTER);
        }
        catch (GuiFindException e) {
            assertEquals("Le noeud 'root:unknown' n'existe pas.\n" + "Liste des noeuds présents :\n"
                         + "-root\n" + "-root:node1\n" + "-root:node2\n", e.getMessage());
        }
    }


    public void test_convertIntoTreePathBasedOnDisplay()
          throws Exception {
        assertEquals(new TreePath(node1.getPath()),
                     convertIntoTreePath(tree, "renderer root:renderer node1", RENDERER_CONVERTER));

        try {
            convertIntoTreePath(tree, "root:unknown", RENDERER_CONVERTER);
        }
        catch (GuiFindException e) {
            assertEquals("Le noeud 'root:unknown' n'existe pas.\n" + "Liste des noeuds présents :\n"
                         + "-renderer root\n" + "-renderer root:renderer node1\n"
                         + "-renderer root:renderer node2\n",
                         e.getMessage());
        }
    }


    public void test_convertIntoTreePath() throws Exception {
        JTree myTree = new JTree();
        myTree.setModel(new TreeModelMock());

        NodeMock<NodeMock> root = new NodeMock<NodeMock>("root");
        NodeMock<NodeMock> n1 = new NodeMock<NodeMock>("node1");
        NodeMock<NodeMock> n2 = new NodeMock<NodeMock>("node2");
        NodeMock<NodeMock> n3 = new NodeMock<NodeMock>("node3");
        n1.addChild(n2);
        n1.addChild(n3);

        TreePath etalon = new TreePath(new Object[]{root, n1, n2});
        TreePath testValue = convertIntoTreePath(myTree, "root:node1:node2", MODEL_CONVERTER);
        assertEquals(etalon, testValue);
    }


    public void test_classicConvertIntoCheckBoxTreePathBasedOnDisplay()
          throws Exception {
        JTree checkBoxTree = new JTree(new CheckableTreeModelMock());
        checkBoxTree.setCellRenderer(new CheckBoxNodeRenderer());
        TreePath expected = new TreePath(checkBoxTree.getModel().getRoot());

        assertEquals(expected, convertIntoTreePath(checkBoxTree, "root", RENDERER_CONVERTER));

        try {
            convertIntoTreePath(checkBoxTree, "root:unknownChild", RENDERER_CONVERTER);
        }
        catch (GuiFindException e) {
            assertEquals("Le noeud 'root:unknownChild' n'existe pas.\n"
                         + "Liste des noeuds présents :\n" + "-root\n"
                         + "-root:child1\n"
                         + "-root:child1:child11\n" + "-root:child2\n",
                         e.getMessage());
        }
    }


    public void test_convertForAssertIntoCheckBoxTreePathBasedOnDisplay()
          throws Exception {
        JTree checkBoxTree = new JTree(new CheckableTreeModelMock());
        checkBoxTree.setCellRenderer(new CheckBoxNodeRenderer());
        TreePath expected = new TreePath(checkBoxTree.getModel().getRoot());

        assertEquals(expected, convertIntoTreePath(checkBoxTree, "root [false]", RENDERER_ASSERT_CONVERTER));

        try {
            convertIntoTreePath(checkBoxTree,
                                "root [false]:unknownChild [false]",
                                RENDERER_ASSERT_CONVERTER);
        }
        catch (GuiFindException e) {
            assertEquals("Le noeud 'root [false]:unknownChild [false]' n'existe pas.\n"
                         + "Liste des noeuds présents :\n" + "-root [false]\n"
                         + "-root [false]:child1 [false]\n"
                         + "-root [false]:child1 [false]:child11 [false]\n" + "-root [false]:child2 [true]\n",
                         e.getMessage());
        }
    }


    public void test_convertIntoCheckBoxTreePathBasedOnModel()
          throws Exception {
        JTree checkBoxTree = new JTree(new CheckableTreeModelMock());
        checkBoxTree.setCellRenderer(new CheckBoxNodeRenderer());
        TreePath expected = new TreePath(checkBoxTree.getModel().getRoot());

        assertEquals(expected, convertIntoTreePath(checkBoxTree, "root", MODEL_CONVERTER));

        try {
            convertIntoTreePath(checkBoxTree, "root:unknownChild", MODEL_CONVERTER);
        }
        catch (GuiFindException e) {
            assertEquals("Le noeud 'root:unknownChild' n'existe pas.\n" + "Liste des noeuds présents :\n"
                         + "-root\n" + "-root:child1\n" + "-root:child1:child11\n" + "-root:child2\n",
                         e.getMessage());
        }
    }


    public abstract class AbstractTreeModelMock implements TreeModel {
        protected NodeMock root;


        protected AbstractTreeModelMock() {
            this.init();
        }


        public Object getRoot() {
            return root;
        }


        protected abstract void init();


        public Object getChild(Object parent, int index) {
            return ((NodeMock)parent).getChildren().get(index);
        }


        public int getChildCount(Object parent) {
            return ((NodeMock)parent).getChildren().size();
        }


        public boolean isLeaf(Object node) {
            return node instanceof NodeMock && ((NodeMock)node).children.size() == 0;
        }


        public int getIndexOfChild(Object parent, Object child) {
            return 0;
        }


        public void valueForPathChanged(TreePath path, Object newValue) {
        }


        public void addTreeModelListener(TreeModelListener treeModelListener) {
        }


        public void removeTreeModelListener(TreeModelListener treeModelListener) {
        }
    }

    class TreeModelMock extends AbstractTreeModelMock {
        @Override
        protected void init() {
            root = new NodeMock<NodeMock>("root");
            NodeMock<NodeMock> father1 = new NodeMock<NodeMock>("node1");
            father1.children.add(new NodeMock("node2"));
            father1.children.add(new NodeMock("node3"));
            root.addChild(father1);
        }
    }

    class CheckableTreeModelMock extends AbstractTreeModelMock {
        @Override
        protected void init() {
            root = new CheckableNode("root");
            CheckableNode child1 = new CheckableNode("child1");
            child1.addChild(new CheckableNode("child11"));
            CheckableNode child2 = new CheckableNode("child2", true);
            root.addChild(child1);
            root.addChild(child2);
        }
    }

    public class NodeMock<T extends NodeMock> {
        private String label;
        private List<T> children = new ArrayList<T>();


        public NodeMock(String name) {
            this.label = name;
        }


        public void addChild(T checkableNode) {
            children.add(checkableNode);
        }


        public List<T> getChildren() {
            return children;
        }


        @Override
        public String toString() {
            return label;
        }


        @Override
        public boolean equals(Object object) {
            return object.toString().equals(label);
        }
    }

    public class CheckableNode extends NodeMock<CheckableNode> {
        private boolean selected;


        public CheckableNode(String text) {
            this(text, false);
        }


        public CheckableNode(String label, boolean selected) {
            super(label);
            this.selected = selected;
        }


        public boolean isSelected() {
            return selected;
        }


        public void setSelected(boolean newValue) {
            selected = newValue;
        }


        public boolean isLeaf() {
            return getChildren().isEmpty();
        }
    }

    public class CheckBoxNodeRenderer implements TreeCellRenderer {
        private JCheckBox checkBox = new JCheckBox();


        public CheckBoxNodeRenderer() {
            this.checkBox.setOpaque(false);
        }


        protected JCheckBox getCheckBox() {
            return checkBox;
        }


        public Component getTreeCellRendererComponent(JTree tree,
                                                      Object value,
                                                      boolean selected,
                                                      boolean expanded,
                                                      boolean leaf,
                                                      int row,
                                                      boolean hasFocus) {
            CheckableNode node = ((CheckableNode)value);

            checkBox.setText(node.toString());
            checkBox.setSelected(node.isSelected());
            return checkBox;
        }
    }
}
