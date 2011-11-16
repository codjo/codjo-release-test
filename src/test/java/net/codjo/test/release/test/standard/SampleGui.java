/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.test.standard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Ihm d'essai permettant de tester les 'gui-task'.
 */
public class SampleGui extends JFrame {
    private JDesktopPane desktopPane;
    private JPopupMenu popup = new JPopupMenu();
    private JButton viewPopupButton = new JButton("View Popup");


    public SampleGui() {
        super("SampleGui");
        buildGui();
        setSize(1200, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }


    private void buildGui() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        this.setContentPane(contentPanel);

        // DesktopPane
        desktopPane = new JDesktopPane();
        contentPanel.add(desktopPane, BorderLayout.CENTER);

        // Toolbar
        JToolBar toolBar = new JToolBar();
        toolBar.add(new JButton("Outil 1"));
        toolBar.add(new SampleAction()).setName("toolbar.newFrame");
        toolBar.add(new DialogAction()).setName("toolbar.newDialog");
        toolBar.add(new JButton("Outil 3"));
        toolBar.add(new JButton("Doublon 1")).setName("Doublon");
        toolBar.add(new ExcelAction()).setName("Excel");
        contentPanel.add(toolBar, BorderLayout.NORTH);

        // Création du menu Fichier
        JMenu fileMenu = new JMenu("Fichier");
        fileMenu.add(new JMenuItem("Un menu"));
        fileMenu.add(new JMenuItem(new SampleAction()));
        fileMenu.add(new JMenuItem(new SampleAction("PortfolioWindow")));
        fileMenu.add(new JMenuItem(new QuitAction()));

        // Création du menu Edition
        JMenu editMenu = new JMenu("Edition");
        editMenu.add(new JMenuItem("Couper"));
        editMenu.add(new JMenuItem("Copier"));
        editMenu.add(new JMenuItem("Coller"));

        JMenuBar bar = new JMenuBar();
        bar.add(fileMenu);
        bar.add(editMenu);
        this.setJMenuBar(bar);

        //Creation du popup avec le checkbox
        JCheckBoxMenuItem menuItemChecked = new JCheckBoxMenuItem("Test check", true);
        menuItemChecked.setName("menuItemChecked");

        JCheckBoxMenuItem menuItemUnChecked = new JCheckBoxMenuItem("Test uncheck", false);
        menuItemUnChecked.setName("menuItemUnChecked");

        popup.add(menuItemChecked);
        popup.add(menuItemUnChecked);
        toolBar.addSeparator();

        viewPopupButton.setName("ViewPopup");
        toolBar.add(viewPopupButton);
        viewPopupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                popup.show(viewPopupButton, 0, viewPopupButton.getHeight());
            }
        });
    }


    public static void main(String[] args) {
        JFrame frame = new SampleGui();
        frame.setVisible(true);
    }


    private final class SampleAction extends AbstractAction {
        private String listId;


        private SampleAction() {
            super("Nouvelle frame");
        }


        private SampleAction(String listId) {
            super("Liste " + listId);

            this.listId = listId;
        }


        public void actionPerformed(ActionEvent event) {
            displayFrame(new SampleInternalFrame(listId));
        }


        private void displayFrame(final JInternalFrame frame) {
            frame.setVisible(true);
            desktopPane.add(frame);
            try {
                frame.setSelected(true);
            }
            catch (PropertyVetoException error) {
                ;
            }
        }
    }

    private final class QuitAction extends AbstractAction {
        private QuitAction() {
            super("Quitter");
        }


        public void actionPerformed(ActionEvent event) {
            System.exit(5);
        }
    }

    private final class DialogAction extends AbstractAction {
        private DialogAction() {
            super("Un dialogue");
        }


        public void actionPerformed(ActionEvent event) {
            displayDialog();
        }


        private void displayDialog() {
            JOptionPane
                    .showConfirmDialog(SampleGui.this, "Ca va ?", "Mon dialogue", JOptionPane.YES_NO_OPTION);
        }
    }

    private final class ExcelAction extends AbstractAction {
        private ExcelAction() {
            super("Excel");
        }


        public void actionPerformed(ActionEvent event) {
            openExcel();
        }


        private void openExcel() {
            String filename = getClass().getResource("ExcelScenario_actual.xls").getPath();
            try {
                String executableName = "rundll32.exe url.dll,FileProtocolHandler";
                Runtime.getRuntime().exec(executableName + " " + new File(filename).getAbsolutePath());
            }
            catch (IOException e) {
                throw new RuntimeException("Impossible de charger le fichier " + filename, e);
            }
        }
    }

    private final class SampleInternalFrame extends JInternalFrame {
        private SampleInternalFrame(String listId) {
            super("", true, true, true);

            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            if (listId != null) {
                setName("ListWindow");
                setTitle("Liste de " + listId);
                buildListe();
            } else {
                setName("DetailWindow");
                setTitle("Une frame");
                buildDetail();
            }
            pack();
        }


        private void buildListe() {
            JPanel content = new JPanel();
            setContentPane(content);
            addTable(content);
            addTableWithCellSelectable(content);
            addTableWithCellRenderer(content);
            addTableWithCustomCellRenderer(content);
            addTableWithPopupMenu(content);
            addComboBoxWithRenderer(content);
            addList(content);
            addTreeWithPopupMenu(content);
            addTextArea(content);
            addEditButton(content);
            addTabbedPane(content);
            addDuplicateComponent(content);
            content.setPreferredSize(new Dimension(800, 500));
        }


        private void addDuplicateComponent(JPanel content) {
            content.add(new JButton("Doublon 2")).setName("Doublon");
        }


        private void addTabbedPane(JPanel panel) {
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.setName("MyJTabbedPane");

            JTextField textFieldPage1 = new JTextField("Hello1");
            textFieldPage1.setName("textField1");

            JTextField textFieldPage2 = new JTextField("Hello2");
            textFieldPage2.setName("textField2");

            JPanel page1 = new JPanel();
            page1.add(textFieldPage1);

            JPanel page2 = new JPanel();
            page2.add(textFieldPage2);

            tabbedPane.addTab("page 1", page1);
            tabbedPane.addTab("page 2", page2);

            panel.add(tabbedPane);
        }


        private void addEditButton(JPanel content) {
            content.add(new JLabel("Bouton Edit="));
            JButton ok = new JButton("Edit");
            content.add(nameComponent(ok, "Button.edit"));
            ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    new SampleAction().actionPerformed(event);
                }
            });
        }


        private void addComboBoxWithRenderer(JPanel content) {
            DefaultComboBoxModel model =
                    new DefaultComboBoxModel(new String[]{"ligne1", "ligne2", "ligne3", "ligne4", "ligne5"});

            JComboBox comboBoxWithRenderer = new JComboBox(model);
            comboBoxWithRenderer.setRenderer(new ListCellRenderer() {
                public Component getListCellRendererComponent(JList list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    return new JLabel("renderer" + (index + 1));
                }
            });

            content.add(nameComponent(comboBoxWithRenderer, "comboBoxWithRenderer"));
        }


        private void addList(JPanel content) {
            content.add(new JLabel("lignes="));
            content.add(nameComponent(new JList(new String[]{"ligne0", "ligne1", "ligne2"}), "lignes"));
        }


        private void addTable(JPanel content) {
            content.add(new JLabel("table="));
            content.add(nameComponent(
                    new JTable(new Object[][]{
                            {"1A", "1B"},
                            {"2A", "2B"},
                            {"3A", "3B"}
                    }, new Object[]{"titreA", "titreB"}),
                    "type"));
        }


        private void addTableWithCellSelectable(JPanel content) {
            content.add(new JLabel("table2="));
            JTable table =
                    new JTable(new Object[][]{
                            {"1A", "1B"},
                            {"2A", "2B"},
                            {"3A", "3B"}
                    }, new Object[]{"titreA", "titreB"});

            table.setCellSelectionEnabled(true);
            JScrollPane scrollPane = new JScrollPane(nameComponent(table, "tableWithCellSelectable"));
            Dimension dimension = new Dimension(250, 80);
            scrollPane.setMaximumSize(dimension);
            scrollPane.setPreferredSize(dimension);
            content.add(scrollPane);

            table.getTableHeader().addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent event) {
                    if (event.getClickCount() > 1) {
                        JTableHeader tableHeader = (JTableHeader) event.getSource();
                        int clickedColumn = tableHeader.getColumnModel().getColumnIndexAtX(event.getX());
                        JOptionPane.showConfirmDialog(SampleGui.this, "Ca va ?", "Colonne=" + clickedColumn,
                                JOptionPane.YES_NO_OPTION);
                    }
                }
            });
        }


        private void addTableWithCellRenderer(JPanel content) {
            content.add(new JLabel("tableRenderer="));
            JTable table =
                    new JTable(new Object[][]{
                            {"a", "1"},
                            {"b", "2"}
                    }, new Object[]{"Col1", "ColWithRenderer"});
            TableColumn secondColumn = table.getColumnModel().getColumn(1);
            addMiddleClickPopup(table.getTableHeader());
            JScrollPane scrollPane = new JScrollPane(nameComponent(table, "tableWithCellRenderer"));
            Dimension dimension = new Dimension(250, 80);
            scrollPane.setMaximumSize(dimension);
            scrollPane.setPreferredSize(dimension);
            content.add(scrollPane);
            secondColumn.setCellRenderer(new TableRenderer());
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON2) {
                        JTable table = (JTable) e.getSource();
                        int row = table.rowAtPoint(e.getPoint());
                        int col = table.columnAtPoint(e.getPoint());
                        new JFrame("clic sur tableWithCellRenderer avec le bouton milieu sur " + col + "," + row).setVisible(true);
                    }
                }
            });
        }


        private void addTableWithCustomCellRenderer(JPanel content) {
            content.add(new JLabel("tableRenderer="));
            JTable table =
                    new JTable(new Object[][]{
                            {"a", "true"},
                            {"b", "false"}
                    }, new Object[]{"Col1", "ColWithCustomRenderer"});

            TableColumn secondColumn = table.getColumnModel().getColumn(1);
            secondColumn.setCellRenderer(new CustomRenderer());
            content.add(nameComponent(table, "tableWithCustomCellRenderer"));
        }


        private void addTableWithPopupMenu(JPanel content) {
            content.add(new JLabel("table="));
            JTable table =
                    new JTable(new Object[][]{
                            {"1A", "1B"},
                            {"2A", "2B"},
                            {"3A", "3B"}
                    }, new Object[]{"titreA", "titreB"});

            addMenu(table, new String[]{"menu1", "menu2"}, new boolean[]{false, true});
            addMenu(table.getTableHeader(), new String[]{"TableHeaderMenu1", "TableHeaderMenu2"},
                    new boolean[]{true, true});

            JScrollPane jScrollPane = new JScrollPane(nameComponent(table, "tableWithPopupMenu"));
            Dimension dimension = new Dimension(250, 80);
            jScrollPane.setMaximumSize(dimension);
            jScrollPane.setPreferredSize(dimension);
            content.add(jScrollPane);
        }


        private void addMenu(JComponent component, String[] menuItemLabels, boolean[] booleans) {
            JPopupMenu popupMenu = new JPopupMenu();
            for (int i = 0; i < menuItemLabels.length; i++) {
                JMenuItem menuItem = new JMenuItem(menuItemLabels[i]);
                menuItem.setEnabled(booleans[i]);
                popupMenu.add(menuItem);
            }
            popupMenu.setInvoker(component);
            component.addMouseListener(new PopupHelper(popupMenu));
        }


        private void addTreeWithPopupMenu(JPanel content) {
            content.add(new JLabel("Arbre="));

            // Noeud racine
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("rootName");

            // Ajout d'une feuille à la racine
            DefaultMutableTreeNode child1 = new DefaultMutableTreeNode("child1");
            root.add(child1);
            child1.add(new DefaultMutableTreeNode("child11"));
            root.add(new DefaultMutableTreeNode("child2"));

            // Construction de l'arbre
            DefaultTreeModel treeModel = new DefaultTreeModel(root);
            JTree tree = new JTree(treeModel);

            // Création de la popupMenu
            JPopupMenu popupMenu = new JPopupMenu();
            popupMenu.add("menuTree1");
            popupMenu.add("menuTree2");
            popupMenu.setInvoker(tree);
            tree.addMouseListener(new PopupHelper(popupMenu));
            content.add(nameComponent(tree, "treeNameWithPopupMenu"));
        }


        private void addTextArea(JPanel content) {
            content.add(nameComponent(new JTextArea(3, 10), "theTextArea"));
        }


        private void buildDetail() {
            JPanel content = new JPanel();
            setContentPane(content);
            content.add(new JLabel("ptfCode="));
            content.add(nameComponent(new JTextField("un field"), "ptfCode"));
            content.add(new JLabel("period="));
            content.add(nameComponent(new JTextField(), "period"));
            content.add(new JLabel("type="));
            content.add(nameComponent(new JComboBox(new Object[]{"valA", "valB", "valC"}), "type"));

            final JComboBox editableCombo = new JComboBox(new Object[]{"edit1", "edit2", "edit3"});

            editableCombo.setEditable(true);
            editableCombo.setPreferredSize(new Dimension(60, 22));
            content.add(nameComponent(editableCombo, "editableCombo"));

            final JComboBox comboWithRenderer =
                    new JComboBox(new Object[]{"firstItemModel", "secondItemModel", "thirdItemModel"});

            comboWithRenderer.setRenderer(new SampleGui.ComboRenderer());
            content.add(nameComponent(comboWithRenderer, "comboWithRenderer"));
            content.add(nameComponent(new JCheckBox("validBox"), "validBox"));

            JButton button = new JButton("Button disable");
            button.setName("buttonDisable");
            button.setEnabled(false);
            content.add(button);

            JTextField textField = new JTextField("TextField enable");
            textField.setName("textFieldEnable");
            content.add(textField);

            JComboBox comboBox = new JComboBox();
            comboBox.setName("comboBoxEnable");
            comboBox.setPreferredSize(new Dimension(60, 22));
            comboBox.setEnabled(false);

            content.add(comboBox);
            JTable table =
                    new JTable(new String[][]{
                            {"a"},
                            {"b"}
                    }, new String[]{"col1"}) {
                        @Override
                        public boolean isCellEditable(int rowIndex, int vColIndex) {
                            return rowIndex == 0;
                        }
                    };
            table.setName("tableEnable");
            content.add(table);

            JTable table2 = new JTable();
            table2.setEnabled(false);
            table2.setName("tableEnable2");
            content.add(table2);

            content.add(new JLabel("boutton OK="));
            JButton ok = new JButton("OK");
            content.add(nameComponent(ok, "Button.ok"));
            ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    closeMe();
                }
            });
        }


        private JComponent nameComponent(JComponent cp, String name) {
            if (cp instanceof JTextField) {
                ((JTextField) cp).setColumns(10);
            }
            cp.setName(name);
            return cp;
        }


        private void closeMe() {
            try {
                setClosed(true);
            }
            catch (PropertyVetoException ex) {
                ;
            }
            setVisible(false);
            dispose();
        }
    }

    private void addMiddleClickPopup(JTableHeader tableHeader) {
        tableHeader.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON2) {
                    JTableHeader tableHeader = (JTableHeader) e.getSource();
                    JTable table = tableHeader.getTable();
                    int columnAtPoint = table.columnAtPoint(e.getPoint());
                    String columnName = table.getColumnName(columnAtPoint);
                    JDialog popup = new JDialog(SampleGui.this, "Middle Click at " + columnName);
                    popup.setVisible(true);
                }
            }
        });
    }

    private class ComboRenderer implements ListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            String label = "Bla bla";
            if (index == 0) {
                label = "First Item Label";
            }
            if (index == 1) {
                label = "Second Item Label";
            }
            if (index == 2) {
                label = "Third Item Label";
            }

//            String stringValue = ((String)value);
//            String label = "Bla bla";
//            if ("firstItemModel".equals(stringValue)) {
//                label = "First Item Label";
//            }
//
//            if ("secondItemModel".equals(stringValue)) {
//                label = "Second Item Label";
//            }
//
//            if ("thirdItemModel".equals(stringValue)) {
//                label = "Third Item Label";
//            }
//            return new JLabel(label);
            return new JLabel(label);
        }
    }

    class TableRenderer implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            if ("1".equals(value)) {
                return new JLabel("One");
            }

            if ("2".equals(value)) {
                return new JLabel("Two");
            }

            return new JLabel("Default");
        }
    }

    private class CustomRenderer extends JCheckBox implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            try {
                this.setSelected(Boolean.valueOf(value.toString()));
            }

            catch (Exception e) {
                this.setSelected(false);
            }

            return this;
        }
    }

    private final class PopupHelper extends MouseAdapter {
        private JPopupMenu popupMenu;


        private PopupHelper(JPopupMenu popupMenu) {
            this.popupMenu = popupMenu;
        }


        @Override
        public void mouseReleased(MouseEvent event) {
            maybeShowPopup(event);
        }


        private void maybeShowPopup(MouseEvent event) {
            if (event.isPopupTrigger()) {
                popupMenu.show(event.getComponent(), event.getX(), event.getY());
            } else {
                popupMenu.setVisible(false);
            }
        }
    }
}
