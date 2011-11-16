/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
/**
 *
 */
public class EditCellStepTest extends JFCTestCase {
    private EditCellStep editCellStep;
    private JTable table;


    @Override
    protected void setUp() throws Exception {
        editCellStep = new EditCellStep();
        editCellStep.setTimeout(1);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    public void test_editCellWithAColumnNumber() throws Exception {
        showFrame();
        editTitleColumn("1");
    }


    public void test_editCellWithAColumnName() throws Exception {
        showFrame();
        editTitleColumn("Titre");
    }


    public void test_editCellWithAUnknownColumnThrowsAnException()
          throws Exception {
        showFrame();

        editCellStep.setName("kingTable");
        editCellStep.setRow(0);
        editCellStep.setColumn("pipoColumn");
        try {
            editCellStep.proceed(new TestContext(this));
            fail();
        }
        catch (GuiFindException e) {
            assertEquals(TableTools.computeUnknownColumnMessage(table, "pipoColumn"), e.getMessage());
        }
    }


    public void test_onlyOneValidateOrCancelIsAllowed()
          throws Exception {
        editCellStep.addAssertListSize(new AssertListSizeStep());
        CancelTableEditionStep cancelStep = new CancelTableEditionStep();
        editCellStep.addCancel(cancelStep);

        try {
            editCellStep.addValidate(new ValidateTableEditionStep());
            fail();
        }
        catch (GuiConfigurationException e) {
            assertEquals(EditCellStep.computeStepAlreadyDefinedMessage(cancelStep), e.getMessage());
        }
    }


    public void test_noTableNameInEditCellStepThrowsAnException()
          throws Exception {
        showFrame();

        try {
            editCellStep.proceed(new TestContext(this));
            fail();
        }
        catch (GuiFindException e) {
            assertEquals(EditCellStep.NO_NAME_HAS_BEEN_SET, e.getMessage());
        }
    }


    public void test_badNameInSubStepsThrowsAnException()
          throws Exception {
        showFrame();

        editCellStep.setName("kingTable");
        editCellStep.setRow(0);
        editCellStep.setColumn("1");

        AssertListSizeStep assertListSizeStep = new AssertListSizeStep();
        assertListSizeStep.setName("pipo");
        assertListSizeStep.setTimeout(1);
        editCellStep.addAssertListSize(assertListSizeStep);

        try {
            editCellStep.proceed(new TestContext(this));
        }
        catch (GuiFindException e) {
            assertEquals("Le composant 'pipo' est introuvable.", e.getMessage());
        }
    }


    public void test_badLabelInClickStepThrowsAnException() throws Exception {
        showFrame();

        editCellStep.setName("kingTable");
        editCellStep.setRow(0);
        editCellStep.setColumn("0");

        ClickStep clickStep = new ClickStep();
        clickStep.setLabel("je ne suis pas un bon label!");
        clickStep.setTimeout(1);
        editCellStep.addClick(clickStep);

        try {
            editCellStep.proceed(new TestContext(this));
        }
        catch (GuiFindException e) {
            assertEquals("Le composant 'je ne suis pas un bon label!' est introuvable.", e.getMessage());
        }
    }


    public void test_editACellNotEditableThrowsAnException()
          throws Exception {
        showFrame();

        try {
            editTitleColumn("2");
            fail();
        }
        catch (GuiConfigurationException e) {
            assertEquals(EditCellStep.computeNotEditableCellMessage("2"), e.getMessage());
        }
    }


    public void test_editACellFromDisabledTableThrowsAnException()
          throws Exception {
        showFrame();
        table.setEnabled(false);
        try {
            editTitleColumn("2");
            fail();
        }
        catch (GuiConfigurationException e) {
            assertEquals("Le composant '" + table.getName() + "' n'est pas actif.", e.getMessage());
        }
    }


    public void test_cancelOrValidateTagsMustBeAtTheEnd()
          throws Exception {
        showFrame();

        editCellStep.setName("kingTable");
        editCellStep.setRow(0);
        editCellStep.setColumn("Titre");
        SetValueStep setValueStep = new SetValueStep();
        setValueStep.setValue("Christine");
        editCellStep.addSetValue(setValueStep);
        CancelTableEditionStep cancelStep = new CancelTableEditionStep();
        editCellStep.addCancel(cancelStep);

        try {
            editCellStep.addSetValue(setValueStep);
            fail();
        }
        catch (Exception e) {
            assertEquals(EditCellStep.computeBadTagOrderMessage(cancelStep), e.getMessage());
        }
    }


    public void test_multipleTableWithSameCellComponentName() throws Exception {
        JFrame frame = new JFrame();

        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        addEditableComplexTable(panel, "MaTable1");
        addEditableComplexTable(panel, "MaTable2");

        frame.pack();
        frame.setVisible(true);
        flushAWT();

        AssertValueStep assertValueStep = new AssertValueStep();
        assertValueStep.setName("author");
        assertValueStep.setExpected("S.King");

        editCellStep.setName("MaTable1");
        editCellStep.setRow(0);
        editCellStep.setColumn(Integer.toString(0));
        editCellStep.addAssertValue(assertValueStep);
        editCellStep.proceed(new TestContext(this));

        assertValueStep.setName("title");
        assertValueStep.setExpected("Simetierre");

        editCellStep.setName("MaTable2");
        editCellStep.setColumn(Integer.toString(1));
        editCellStep.proceed(new TestContext(this));
    }


    public void test_editCellWorksWithTrees() throws Exception {
        showTree(createTree());

        editCellStep.setName("tree");
        editCellStep.setMode(EditCellStep.MODEL_MODE);
        editCellStep.setPath("root:ProgramFiles:Adobe");
        editCellStep.addClick(createClickButtonStep("hello"));
        editCellStep.addClick(createClickButtonStep("OK"));
        editCellStep.proceed(new TestContext(this));
    }


    public void test_editCellWorksWithClickRight() throws Exception {
        showFrame();

        editCellStep.setName("kingTable");
        editCellStep.setRow(0);
        editCellStep.setColumn("3");
        editCellStep.addClickRight(createClickRightStep());
        editCellStep.proceed(new TestContext(this));
    }


    public void test_editingTreeCellWithUnknownPathThrowsAnException() throws Exception {
        showTree(createTree());

        editCellStep.setName("tree");
        editCellStep.setMode(EditCellStep.MODEL_MODE);
        editCellStep.setPath("root:unknown");
        try {
            editCellStep.proceed(new TestContext(this));
            fail();
        }
        catch (Exception e) {
            assertEquals("Le noeud 'root:unknown' n'existe pas.\n"
                         + "Liste des noeuds présents :\n"
                         + "-root\n"
                         + "-root:ProgramFiles\n"
                         + "-root:ProgramFiles:Adobe\n"
                         + "-root:ProgramFiles:Adobe:Photoshop\n"
                         + "-root:ProgramFiles:Adobe:Acrobat\n"
                         + "-root:Documents\n"
                         + "-root:Documents:Musique\n"
                         + "-root:Documents:Musique:larafabian.mp3\n"
                         + "-root:Documents:CV.doc\n"
                         + "-root:System\n", e.getMessage());
        }
    }


    public void test_editingANonEditableTreeThrowsAnException() throws Exception {
        JTree tree = createTree();
        tree.setEditable(false);

        showTree(tree);

        editCellStep.setName("tree");
        editCellStep.setPath("root:Documents:Musique:larafabian.mp3");

        try {
            editCellStep.proceed(new TestContext(this));
            fail();
        }
        catch (Exception e) {
            assertEquals("L'arbre n'est pas éditable.", e.getMessage());
        }
    }


    private ClickStep createClickButtonStep(String label) {
        ClickStep clickStep = new ClickStep();
        clickStep.setLabel(label);
        return clickStep;
    }


    private ClickRightStep createClickRightStep() {
        final ClickRightStep clickRightStep = new ClickRightStep();
        
        AssertEnabledStep assertEnabledStep = new AssertEnabledStep();
        assertEnabledStep.setMenu("Oualid rulz");
        assertEnabledStep.setExpected("true");
        clickRightStep.addAssertEnabled(assertEnabledStep);
        
        return clickRightStep;
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
        JTree tree = new JTree(treeModel);
        tree.setName("tree");
        tree.setCellRenderer(new ButtonTreeCellEditor());
        tree.setCellEditor(new ButtonTreeCellEditor());
        tree.setEditable(true);
        
        
        
        return tree;
    }


    private void showTree(JTree tree) {
        JFrame frame = new JFrame();

        frame.setContentPane(tree);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }


    private void editTitleColumn(String column) {
        AssertListSizeStep assertListSizeStep = new AssertListSizeStep();
        assertListSizeStep.setExpected(5);
        editCellStep.setName("kingTable");
        editCellStep.setRow(0);
        editCellStep.setColumn(column);
        editCellStep.addAssertListSize(assertListSizeStep);
        editCellStep.proceed(new TestContext(this));
    }


    private void addEditableComplexTable(JPanel panel, String tableName) {
        JTable mytable = new JTable();
        mytable.setName(tableName);
        mytable.setModel(new MyDefaultModel(
              new Object[][]{
                    {"S.King", "Simetierre", "9"},
                    {"S.King", "Roadmaster", "5"}
              },
              new Object[]{"Auteur", "Titre", "Note"}));

        JTextField textField1 = new JTextField();
        textField1.setName("author");
        mytable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(textField1));

        JTextField textField2 = new JTextField();
        textField2.setName("title");
        mytable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(textField2));

        panel.add(mytable);
        
        
        mytable.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent
                  event) {
                if (event.isPopupTrigger()) {
                    showPopup(event);
                }
            }


            @Override
            public void mouseReleased(MouseEvent
                  event) {
                if (event.isPopupTrigger()) {
                    showPopup(event);
                }
            }


            private void showPopup(MouseEvent event) {
                JPopupMenu popupMenu = new JPopupMenu();
                popupMenu.add(new JMenuItem("Oualid rulz"));
                popupMenu.show(event.getComponent(), event.getX(), event.getY());
            }
        });
    }


    private void addEditableTable(JPanel panel) {
        table = new JTable();
        table.setModel(new MyDefaultModel(
              new Object[][]{
                    {"S.King", "Simetierre", "9", "génial"},
                    {"S.King", "Roadmaster", "5", "bof"}
              },
              new Object[]{"Auteur", "Titre", "Note", "Commentaire"}));
        table.setName("kingTable");

        JComboBox autoValidatedCombo =
              new JComboBox(new Object[]{"Simetierre", "Roadmaster", "Charlie", "Christine", "Ca"});

        JComboBox standardCombo = new JComboBox(new Object[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"});

        table.getColumnModel().getColumn(0).setCellEditor(new ButtonCellEditor());

        table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(autoValidatedCombo));

        table.getColumnModel().getColumn(2).setCellEditor(new ComboCellEditor(standardCombo));

        panel.add(table);
    }

    
    private void showFrame() {
        JFrame frame = new JFrame();

        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        addEditableTable(panel);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }


    private Component createHelloWorldPanel(String value) {
        JPanel panel = new JPanel();
        panel.setName("helloWorldPanel");
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


    private class MyDefaultModel extends AbstractTableModel {
        private Object[][] rowData;
        private Object[] columnNames;


        private MyDefaultModel(Object[][] rowData, Object[] columnNames) {
            this.rowData = rowData;
            this.columnNames = columnNames;
        }


        @Override
        public String getColumnName(int column) {
            return columnNames[column].toString();
        }


        public int getRowCount() {
            return rowData.length;
        }


        public int getColumnCount() {
            return columnNames.length;
        }


        public Object getValueAt(int row, int col) {
            return rowData[row][col];
        }


        @Override
        public boolean isCellEditable(int row, int column) {
            return column != 2;
        }


        @Override
        public void setValueAt(Object value, int row, int col) {
            rowData[row][col] = value;
            fireTableCellUpdated(row, col);
        }
    }

    private class ComboCellEditor extends AbstractCellEditor implements TableCellEditor {
        private JComboBox standardCombo;


        private ComboCellEditor(JComboBox standardCombo) {
            this.standardCombo = standardCombo;
        }


        public Object getCellEditorValue() {
            return standardCombo.getSelectedItem();
        }


        public Component getTableCellEditorComponent(JTable table,
                                                     Object value,
                                                     boolean isSelected,
                                                     int row,
                                                     int column) {
            standardCombo.setSelectedItem(value);
            return standardCombo;
        }
    }

    private class ButtonCellEditor extends AbstractCellEditor implements TableCellEditor {

        public Object getCellEditorValue() {
            return null;
        }


        public Component getTableCellEditorComponent(JTable table,
                                                     Object value,
                                                     boolean isSelected,
                                                     int row,
                                                     int column) {
            return createHelloWorldPanel((String)value);
        }
    }

    private final class ButtonTreeCellEditor implements TreeCellEditor, TreeCellRenderer {

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
}
