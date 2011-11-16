package net.codjo.test.release.task.gui.converter;
import javax.swing.JTree;
/**
 *
 */
public class TreeNodeModelConverter implements TreeNodeConverter {
    public String getValue(JTree tree, Object node) {
        return node.toString();
    }
}
