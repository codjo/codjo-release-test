/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.test.tableeditor;
import net.codjo.test.release.test.AbstractSampleGui;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.EventObject;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
/**
 *
 */
public class SampleTableEditorGui extends AbstractSampleGui {
    public SampleTableEditorGui() {
        super("SampleTableEditorGui");
    }


    @Override
    protected void buildGui() {
        JPanel panel = new JPanel();
        this.setContentPane(panel);
        JTable table =
              new JTable(new Object[][]{
                    {"S.King", "Simetierre", "9", "génial", ""},
                    {"S.King", "Roadmaster", "5", "pas lu (livre volé !)", ""}
              }, new Object[]{"Auteur", "Titre", "Note", "Avis", "Catégorie"});
        table.setName("kingTable");

        JComboBox autoValidatedCombo =
              new JComboBox(new Object[]{"Simetierre", "Roadmaster", "Charlie", "Christine", "Ca"});

        JComboBox standardCombo = new JComboBox(new Object[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"});

        table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(autoValidatedCombo));

        table.getColumnModel().getColumn(2).setCellEditor(new MyCellEditor(standardCombo));

        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JTextField()));

        table.getColumnModel().getColumn(4).setCellEditor(new MyTableTreeCellEditor());

        table.setRowHeight(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(200);

        panel.add(table);
    }


    public static void main(String[] args) {
        JFrame frame = new SampleTableEditorGui();
        frame.setVisible(true);
    }


    private final class MyTableTreeCellEditor extends AbstractCellEditor implements TableCellEditor {

        public Object getCellEditorValue() {
            return null;
        }


        public Component getTableCellEditorComponent(JTable table,
                                                     Object value,
                                                     boolean isSelected,
                                                     int row,
                                                     int column) {
            return createTree();
        }


        private JTree createTree() {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
            DefaultMutableTreeNode program = new DefaultMutableTreeNode("ProgramFiles");
            DefaultMutableTreeNode documents = new DefaultMutableTreeNode("Documents");
            DefaultMutableTreeNode adobe = new DefaultMutableTreeNode("Adobe");
            adobe.add(new DefaultMutableTreeNode("Photoshop"));
            adobe.add(new DefaultMutableTreeNode("Acrobat"));
            program.add(adobe);
            root.add(program);
            DefaultMutableTreeNode musique = new DefaultMutableTreeNode("Musique");
            musique.add(new DefaultMutableTreeNode("larafabian.mp3"));
            documents.add(musique);
            documents.add(new DefaultMutableTreeNode("CV.doc"));
            root.add(documents);
            root.add(new DefaultMutableTreeNode("System"));
            DefaultTreeModel treeModel = new DefaultTreeModel(root);
            final JTree tree = new JTree(treeModel);
            tree.setName("tree");
            tree.setCellRenderer(new ButtonTreeCellEditor());
            tree.setCellEditor(new ButtonTreeCellEditor());
            tree.setEditable(true);
            return tree;
        }
    }

    private final class ButtonTreeCellEditor implements TreeCellEditor,
                                                        TreeCellRenderer {

        public Component getTreeCellEditorComponent(JTree tree,
                                                    Object value,
                                                    boolean isSelected,
                                                    boolean expanded,
                                                    boolean leaf,
                                                    int row) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            return createHelloWorldPanel((String)node.getUserObject());
        }


        public Component getTreeCellRendererComponent(JTree tree,
                                                      Object value,
                                                      boolean selected,
                                                      boolean expanded,
                                                      boolean leaf,
                                                      int row,
                                                      boolean hasFocus) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            return createHelloWorldPanel((String)node.getUserObject());
        }


        private Component createHelloWorldPanel(String value) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            panel.add(new JLabel(value));
            panel.add(new JButton(new AbstractAction("hello") {
                public void actionPerformed(ActionEvent event) {
                    JOptionPane
                          .showMessageDialog(null, "Hello world!", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }));
            return panel;
        }


        public Object getCellEditorValue() {
            return null;
        }


        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }


        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }


        public boolean stopCellEditing() {
            return false;
        }


        public void cancelCellEditing() {
        }


        public void addCellEditorListener(CellEditorListener l) {
        }


        public void removeCellEditorListener(CellEditorListener l) {
        }
    }

    private final class MyCellEditor extends AbstractCellEditor implements TableCellEditor {
        private JComboBox standardCombo;


        private MyCellEditor(JComboBox standardCombo) {
            this.standardCombo = standardCombo;
        }


        public Object getCellEditorValue() {
            return standardCombo.getSelectedItem();
        }


        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                                                     int column) {
            standardCombo.setSelectedItem(value);
            return standardCombo;
        }
    }
}
