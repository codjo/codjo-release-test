package net.codjo.test.release.task.gui.converter;
import javax.swing.JTree;
/**
 *
 */
public interface TreeNodeConverter {
    String getValue(JTree tree, Object node);
}
