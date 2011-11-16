package net.codjo.test.release.task.gui.converter;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTree;
/**
 *
 */
public class TreeNodeRendererConverter implements TreeNodeConverter {
    public static final String INVALID_COMPONENT_MESSAGE
          = "agf-test supports only JLabel or JCheckBox components for Tree renderers.";

    private final boolean isAssert;


    public TreeNodeRendererConverter(boolean isAssert) {
        this.isAssert = isAssert;
    }


    public String getValue(JTree tree, Object node) {

        Component component = tree.getCellRenderer()
              .getTreeCellRendererComponent(tree, node, false, false, false, 0, false);

        if ((component instanceof JLabel)) {
            return ((JLabel)component).getText();
        }

        else if (component instanceof JCheckBox) {
            JCheckBox checkBox = (JCheckBox)component;
            if (isAssert) {
                return checkBox.getText() + " [" + checkBox.isSelected() + "]";
            }
            else {
                return checkBox.getText();
            }
        }

        else {
            throw new RuntimeException(INVALID_COMPONENT_MESSAGE);
        }
    }
}

