package net.codjo.test.release.task.gui;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 *
 */
public class ExpandAllTreeStep extends AbstractGuiStep {
    private String name;


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void proceed(TestContext context) {
        NamedComponentFinder finder = new NamedComponentFinder(JTree.class, name);
        JTree tree = (JTree)findOnlyOne(finder, context, 0);

        if (tree == null) {
            throw new GuiFindException("L'arbre '" + getName() + "' est introuvable.");
        }

        TreeUtils.expandSubtree(tree, new TreePath(tree.getModel().getRoot()));
    }
}
