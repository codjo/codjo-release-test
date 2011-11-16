/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
/**
 * Classe de test de {@link SelectStep}.
 */
public class SelectStepTest extends JFCTestCase {
    private SelectStep step;
    private JTable table;
    private JTree tree;
    private JList list;
    private JPanel panel;
    private JComboBox comboBox;
    private JTree badRendererComponent = new JTree();
    private ListCellRenderer badRenderer = new BadRenderer();
    private ListCellRenderer thisIsPrefixRenderer = new ThisIsPrefixRenderer();
    private JPopupMenu popup;


    @Override
    protected void setUp() throws Exception {
        panel = new JPanel();
        addTable();
        addList("FunctionList", new String[]{"line0", "line1", "line2", "line3"});
        addTree();
        addParasite();
        addJCombo();
        addPopupMenu();
        step = new SelectStep();
        step.setTimeout(1);
        step.setPopupDelay(10);
        step.setListDelay(10);
//        step.setWaitingNumber(10);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    public void test_defaults() throws Exception {
        assertEquals(null, step.getName());
        assertEquals(-1, step.getRow());
        assertEquals(-1, step.getColumn());
        assertFalse(step.isMultiple());
        assertNull(step.getPath());
    }


    public void test_getSet() throws Exception {
        final String name = "PtfCode";
        step.setName(name);
        assertEquals(name, step.getName());

        final int row = 1;
        step.setRow(row);
        assertEquals(row, step.getRow());

        final int column = 1;
        step.setColumn(column);
        assertEquals(column, step.getColumn());

        final boolean multiple = true;
        step.setMultiple(multiple);
        assertEquals(multiple, step.isMultiple());

        final String path = "root:child";
        step.setPath(path);
        assertEquals(path, step.getPath());
    }


    public void test_tableOk() throws Exception {
        showFrame();

        // JTable 1 ligne
        step.setName("PortfolioList");
        step.setMultiple(false);
        step.setRow(0);
        step.proceed(new TestContext(this));
        step.setRow(2);
        step.proceed(new TestContext(this));
        assertEquals(1, table.getSelectedRowCount());
        assertTrue(table.isRowSelected(2));

        // JTable multiselection
        step.setName("PortfolioList");
        step.setMultiple(true);
        //selectionn 0
        step.setRow(0);
        step.proceed(new TestContext(this));
        //deselection 2
        step.setRow(2);
        step.proceed(new TestContext(this));
        assertEquals(1, table.getSelectedRowCount());
        assertTrue(table.isRowSelected(0));
        assertFalse(table.isRowSelected(2));
        //selection 1
        step.setRow(1);
        step.proceed(new TestContext(this));
        assertEquals(2, table.getSelectedRowCount());
        assertTrue(table.isRowSelected(0));
        assertFalse(table.isRowSelected(2));
        assertTrue(table.isRowSelected(1));
        //select 1 only
        step.setMultiple(false);
        step.setRow(1);
        step.proceed(new TestContext(this));
        assertEquals(1, table.getSelectedRowCount());
        assertFalse(table.isRowSelected(0));
        assertFalse(table.isRowSelected(2));
        assertTrue(table.isRowSelected(1));
    }


    public void test_tableOkCell() throws Exception {
        showFrame();
        table.setCellSelectionEnabled(true);

        TestContext context = new TestContext(this);
        step.setName("PortfolioList");

        step.setRow(2);
        step.setColumn(1);
        step.proceed(context);

        assertFalse(table.isCellSelected(2, 0));
        assertTrue(table.isCellSelected(2, 1));
        assertFalse(table.isCellSelected(0, 1));
    }


    public void test_tableUnsupportedMultipleWithColumn()
          throws Exception {
        showFrame();

        step.setName("PortfolioList");
        step.setRow(2);
        step.setColumn(1);
        step.setMultiple(true);

        try {
            step.proceed(new TestContext(this));
            fail("Doit échouer car multiple et column sont renseignés.");
        }
        catch (GuiActionException e) {
            ;
        }
    }


    public void test_listSingleSelectionOkWithRow()
          throws Exception {
        showFrame();
        step.setName("FunctionList");
        step.setMultiple(false);
        step.setRow(0);
        checkListSelection(list, new int[]{0});
        step.setRow(2);
        checkListSelection(list, new int[]{2});
    }


    public void test_listMultipleSelectionOkWithRow()
          throws Exception {
        showFrame();
        step.setName("FunctionList");
        step.setMultiple(true);
        step.setRow(0);
        checkListSelection(list, new int[]{0});

        step.setRow(2);
        checkListSelection(list, new int[]{0, 2});
    }


    public void test_listSingleSelectionOkWithLabel()
          throws Exception {
        showFrame();
        step.setName("FunctionList");
        step.setMultiple(false);

        step.setLabel("line1");
        checkListSelection(list, new int[]{1});
        step.setLabel("line3");
        checkListSelection(list, new int[]{3});
    }


    public void test_listWithASpecificRenderer() throws Exception {
        list.setCellRenderer(thisIsPrefixRenderer);
        showFrame();
        step.setName("FunctionList");
        step.setMultiple(false);
        step.setLabel("this is line1");
        checkListSelection(list, new int[]{1});
        step.setLabel("this is line3");
        checkListSelection(list, new int[]{3});
    }


    public void test_listMultipleSelectionOkWithLabel()
          throws Exception {
        showFrame();
        step.setName("FunctionList");
        step.setMultiple(true);
        step.setLabel("line0");
        checkListSelection(list, new int[]{0});

        step.setLabel("line2");
        checkListSelection(list, new int[]{0, 2});
    }


    public void test_listLabelOverrideRow() throws Exception {
        showFrame();
        step.setName("FunctionList");
        step.setMultiple(false);
        step.setRow(12545);
        step.setLabel("line1");
        checkStepFailure(SelectStep.computeIllegalUsageOfLabelAndRowMessage("FunctionList"));

        step.setLabel("");
        checkStepFailure(SelectStep.computeIllegalUsageOfLabelAndRowMessage("FunctionList"));
    }


    public void test_listWithUnknownLabel() throws Exception {
        showFrame();
        step.setName("FunctionList");
        step.setLabel("unknownLabel");
        checkStepFailure(SelectStep.computeUnknownLabelMessage("unknownLabel", "FunctionList"));
    }


    public void test_listWhenSeveralItemsHaveSameLabel()
          throws Exception {
        addList("puzzleBubbleList", new String[]{"line0", "line0", "line1"});
        showFrame();
        step.setName("puzzleBubbleList");
        step.setLabel("line1");
        checkListSelection(list, new int[]{2});
        step.setLabel("line0");
        checkStepFailure(SelectStep.computeDoubleLabelMessage("line0", "puzzleBubbleList"));
    }


    public void test_listWithBadRenderer() throws Exception {
        list.setCellRenderer(badRenderer);
        showFrame();
        step.setName("FunctionList");
        step.setLabel("line1");
        checkStepFailure(SelectStep.computeUnexpectedRendererMessage(
              badRendererComponent.getClass().getName(),
              "FunctionList"));
    }


    public void test_jComboOk() throws Exception {
        showFrame();
        checkComboSelection(new Functor() {
                                public void setAttribute(SelectStep step) {
                                    step.setRow(1);
                                }
                            }, 1, "Christine");
        checkComboSelection(new Functor() {
                                public void setAttribute(SelectStep step) {
                                    step.setRow(2);
                                }
                            }, 2, "Cujo");
        checkComboSelection(new Functor() {
                                public void setAttribute(SelectStep step) {
                                    step.setRow(0);
                                }
                            }, 0, "Roadmaster");
        checkComboSelection(new Functor() {
                                public void setAttribute(SelectStep step) {
                                    step.setLabel("Christine");
                                }
                            }, 1, "Christine");
        checkComboSelection(new Functor() {
                                public void setAttribute(SelectStep step) {
                                    step.setLabel("Cujo");
                                }
                            }, 2, "Cujo");
        checkComboSelection(new Functor() {
                                public void setAttribute(SelectStep step) {
                                    step.setLabel("Roadmaster");
                                }
                            }, 0, "Roadmaster");
    }


    public void test_jComboboxDisabled() throws Exception {
        comboBox.setEnabled(false);
        showFrame();

        checkDisabledComponentSelection(new Functor() {
                                            public void setAttribute(SelectStep step) {
                                                step.setRow(1);
                                            }
                                        }, "combo");
    }


    public void test_jListDisabled() throws Exception {
        list.setEnabled(false);
        showFrame();

        checkDisabledComponentSelection(new Functor() {
                                            public void setAttribute(SelectStep step) {
                                                step.setRow(0);
                                            }
                                        }, "FunctionList");
    }


    public void test_jTreeDisabled() throws Exception {
        tree.setEnabled(false);
        showFrame();

        checkDisabledComponentSelection(new Functor() {
                                            public void setAttribute(SelectStep step) {
                                                step.setPath("rootName:child1");
                                            }
                                        }, "treeName");
    }


    public void test_jTableDisabled() throws Exception {
        table.setEnabled(false);
        showFrame();

        checkDisabledComponentSelection(new Functor() {
                                            public void setAttribute(SelectStep step) {
                                                step.setRow(1);
                                                step.setColumn(1);
                                            }
                                        }, "PortfolioList");
    }


    public void test_jComboWithASpecificRenderer()
          throws Exception {
        comboBox.setRenderer(thisIsPrefixRenderer);
        showFrame();
        checkComboSelection(new Functor() {
                                public void setAttribute(SelectStep step) {
                                    step.setLabel("this is Christine");
                                }
                            }, 1, "Christine");
        checkComboSelection(new Functor() {
                                public void setAttribute(SelectStep step) {
                                    step.setLabel("this is Cujo");
                                }
                            }, 2, "Cujo");
    }


    public void test_jComboWithBadRowThrowsAnException()
          throws Exception {
        showFrame();
        step.setName("combo");
        step.setRow(15);
        checkStepFailure(SelectStep.computeBadRowMessage(15, "combo"));
    }


    public void test_jComboWithUnknownLabelThrowsAnException()
          throws Exception {
        showFrame();
        step.setName("combo");
        step.setLabel("unknown");
        checkStepFailure(SelectStep.computeUnknownLabelMessage("unknown", "combo"));
    }


    public void test_jComboWithABadRendererThrowsAnException()
          throws Exception {
        comboBox.setRenderer(badRenderer);
        showFrame();
        step.setName("combo");
        step.setLabel("Cujo");
        checkStepFailure(SelectStep.computeUnexpectedRendererMessage(
              badRendererComponent.getClass().getName(),
              "combo"));
    }


    public void test_jComboWithMultipleThrowsAnException()
          throws Exception {
        showFrame();
        step.setName("combo");
        step.setLabel("Cujo");
        step.setMultiple(true);
        checkStepFailure(SelectStep.MULTIPLE_UNSUPPORTED_MESSAGE);
    }


    public void test_treeOk() throws Exception {
        showFrame();
        step.setName("treeName");
        // selection d'un noeud existant
        step.setPath("rootName:child1");
        step.proceed(new TestContext(this));
        assertEquals("[rootName, child1]", tree.getSelectionPath().toString());
    }


    public void test_treeMultipleSelection() throws Exception {
        showFrame();
        step.setName("treeName");
        // selection d'un noeud existant
        step.setPath("rootName:child1");
        step.proceed(new TestContext(this));
        assertEquals("[rootName, child1]", tree.getSelectionPath().toString());

        step.setPath("rootName:child2");
        step.setMultiple(true);

        step.proceed(new TestContext(this));
        TreePath[] treePaths = tree.getSelectionPaths();
        assertEquals(2, treePaths.length);
        assertEquals("[rootName, child1]", treePaths[0].toString());
        assertEquals("[rootName, child2]", treePaths[1].toString());

        step.setMultiple(false);
        step.setPath("rootName:child1");
        step.proceed(new TestContext(this));
        treePaths = tree.getSelectionPaths();
        assertEquals(1, treePaths.length);
        assertEquals("[rootName, child1]", treePaths[0].toString());
    }


    public void test_treeWithRenderer() throws Exception {
        showFrame();
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
        step.setName("treeName");
        // selection d'un noeud existant
        step.setPath("rootName (rootName):child1 (child1)");
        step.proceed(new TestContext(this));
        assertEquals("[rootName, child1]", tree.getSelectionPath().toString());

        step.setPath("rootName (rootName):child3 (child3)");
        try {
            step.proceed(new TestContext(this));
            fail("Doit échouer car le noeud n'existe pas.");
        }
        catch (GuiFindException e) {
            ;
        }
    }


    public void test_modeUnsupportedComponent() throws Exception {
        showFrame();
        step.setName("combo");
        // selection d'un noeud existant
        step.setMode(AbstractGuiStep.MODEL_MODE);
        step.setRow(1);
        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (Exception ex) {
            assertEquals("Component doesn't support the attribute 'mode'", ex.getMessage());
        }
    }


    public void test_badMode() throws Exception {
        showFrame();
        step.setName("treeName");
        // selection d'un noeud existant
        step.setMode("noModel");
        step.setPath("rootName:child1");
        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (Exception ex) {
            assertEquals("Invalid value of 'mode' attribute : must be in {\"display\", \"model\"}.",
                         ex.getMessage());
        }
    }


    public void test_treeWithRendererAndMode() throws Exception {
        showFrame();
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

        step.setName("treeName");
        step.setPath("rootName (rootName):child1 (child1):child11 (child11)");
        step.proceed(new TestContext(this));
        assertEquals("[rootName, child1, child11]", tree.getSelectionPath().toString());
        assertPathDoesNotExist("rootName (rootName):child3 (child2)");

        step.setMode(AbstractGuiStep.MODEL_MODE);
        step.setPath("rootName:child1");
        step.proceed(new TestContext(this));
        assertEquals("[rootName, child1]", tree.getSelectionPath().toString());
        assertPathDoesNotExist("rootName:child3");

        step.setMode(AbstractGuiStep.DISPLAY_MODE);
        step.setPath("rootName (rootName):child1 (child1)");
        step.proceed(new TestContext(this));
        assertEquals("[rootName, child1]", tree.getSelectionPath().toString());
        assertPathDoesNotExist("rootName (rootName):child3 (child3)");
    }


    private void assertPathDoesNotExist(String path) {
        step.setPath(path);
        try {
            step.proceed(new TestContext(this));
            fail("Doit échouer car le noeud n'existe pas.");
        }
        catch (GuiFindException e) {
            ;
        }
    }


    public void test_treeInexistantNodeNok() throws Exception {
        showFrame();
        step.setName("treeName");
        // selection d'un noeud inexistant
        step.setPath("rootName:child3");
        try {
            step.proceed(new TestContext(this));
            fail("Doit échouer car le noeud n'existe pas.");
        }
        catch (GuiFindException e) {
            String errorWaited =
                  "Le noeud 'rootName:child3' n'existe pas.\n" + "Liste des noeuds présents :\n"
                  + "-rootName\n"
                  + "-rootName:child1\n" + "-rootName:child1:child11\n" + "-rootName:child2\n";
            assertEquals(errorWaited, e.getMessage());
            ;
        }
    }


    public void test_treeUnsupportedRow() throws Exception {
        showFrame();
        step.setName("treeName");
        step.setPath("rootName:child1");
        step.setRow(2);
        try {
            step.proceed(new TestContext(this));
            fail("Doit échouer car row est renseigné.");
        }
        catch (GuiConfigurationException e) {
            ;
        }
    }


    public void test_treePathNok() throws Exception {
        showFrame();
        step.setName("treeName");
        try {
            step.proceed(new TestContext(this));
            fail("Le noeud ne devrait pas être trouvé.");
        }
        catch (GuiConfigurationException ex) {
            ;
        }
    }


    public void test_nokNotFound() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("Branche");
        step.setRow(1);

        try {
            step.proceed(context);
            fail("Le composant ne devrait pas être trouvé.");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
    }


    public void test_nokUnknownComponent() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("Parasite");
        step.setRow(2);

        try {
            step.proceed(context);
            fail("Type de composant non supporté.");
        }
        catch (GuiConfigurationException ex) {
            ; // Cas normal
        }
    }


    public void test_nokTableRowOutOfRange() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("PortfolioList");
        step.setRow(5);

        try {
            step.proceed(context);
            fail("Row index out of range");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
    }


    public void test_nokListRowOutOfRange() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("FunctionList");
        step.setRow(5);

        try {
            step.proceed(context);
            fail("Row index out of range");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
    }


    public void test_popupMenuMultipleNotAllowed()
          throws Exception {
        showFrame();
        TestContext context = new TestContext(this);

        step.setName("Popup");
        step.setMultiple(true);
        try {
            step.proceed(context);
            fail("multiple not allowed");
        }
        catch (GuiConfigurationException e) {
            ;
        }
    }


    public void test_popupMenuRowAndLabelMissing()
          throws Exception {
        showFrame();
        TestContext context = new TestContext(this);

        step.setName("Popup");
        try {
            step.proceed(context);
            fail("row and label missing");
        }
        catch (GuiConfigurationException e) {
            ;
        }
    }


    public void test_popupMenuWithLabelOk() throws Exception {
        showFrame();
        TestContext context = new TestContext(this);

        step.setName("Popup");
        step.setLabel("ligne2");
        step.proceed(context);
    }


    public void test_popupMenuWithBadLabel() throws Exception {
        showFrame();
        TestContext context = new TestContext(this);

        step.setName("Popup");
        step.setLabel("ligne4");
        try {
            step.proceed(context);
            fail("label doesn't exist");
        }
        catch (GuiFindException e) {
            ;
        }
    }


    public void test_popupMenuWithRowOk() throws Exception {
        showFrame();
        TestContext context = new TestContext(this);

        step.setName("Popup");
        step.setRow(1);
        step.proceed(context);
    }


    public void test_popupMenuWithBadRow() throws Exception {
        showFrame();
        TestContext context = new TestContext(this);

        step.setName("Popup");
        step.setRow(5);
        try {
            step.proceed(context);
            fail("line doesn't exist");
        }
        catch (GuiFindException e) {
            ;
        }
    }


    public void test_popupMenuWithLabelAndRowOk()
          throws Exception {
        showFrame();
        TestContext context = new TestContext(this);

        step.setName("Popup");
        step.setLabel("ligne2");
        step.setRow(1);
        try {
            step.proceed(context);
            fail("row and label");
        }
        catch (GuiConfigurationException e) {
            ;
        }
    }


    public void test_popupMenuWithSwingComponent() throws Exception {
        popup.removeAll();
        popup.add(new JTextField());
        popup.add("frederic");
        popup.add("cedric");
        popup.add("ismael");
        showFrame();
        TestContext context = new TestContext(this);

        step.setName("Popup");
        step.setLabel("frederic");
        step.proceed(context);
    }


    public void test_findSubMenusInPopupMenus() throws Exception {
        popup.removeAll();
        popup.add(new JTextField());
        popup.add("frederic");
        popup.add("cedric");
        popup.add("ismael");

        step.setName("Popup");
        step.setLabel("frederic");
        assertEquals(1, step.findComponentToSelect(popup));
    }


    private void checkStepFailure(String expectedMessage) {
        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }


    private void checkComboSelection(Functor functor, int expectedRow, String expectedLabel) {
        step = new SelectStep();
        step.setTimeout(1);
        step.setName("combo");
        functor.setAttribute(step);
        step.proceed(new TestContext(this));
        assertEquals(expectedLabel, comboBox.getSelectedItem());
        assertEquals(expectedRow, comboBox.getSelectedIndex());
    }


    public void test_jTableSelectCellByLabelOk() throws Exception {
        showFrame();
        TestContext context = new TestContext(this);
        step.setName("PortfolioList");
        step.setLabel("line2");
        step.setColumn(0);
        step.proceed(context);

        assertFalse(table.isCellSelected(0, 0));
        assertFalse(table.isCellSelected(0, 1));
        assertFalse(table.isCellSelected(1, 0));
        assertFalse(table.isCellSelected(1, 1));
        assertTrue(table.isCellSelected(2, 0));
        assertTrue(table.isCellSelected(2, 1));
    }


    public void test_jTableSelectCellByLabelWithRendererOk() throws Exception {
        showFrame();
        table.setDefaultRenderer(Object.class, new TableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value,
                                                           boolean isSelected,
                                                           boolean hasFocus,
                                                           int row,
                                                           int column) {
                return new JLabel("hello " + value);
            }
        });

        TestContext context = new TestContext(this);
        step.setName("PortfolioList");
        step.setLabel("hello line2");
        step.setColumn(0);
        step.proceed(context);

        assertFalse(table.isCellSelected(0, 0));
        assertFalse(table.isCellSelected(0, 1));
        assertFalse(table.isCellSelected(1, 0));
        assertFalse(table.isCellSelected(1, 1));
        assertTrue(table.isCellSelected(2, 0));
        assertTrue(table.isCellSelected(2, 1));
    }


    public void test_jTableSelectCellByLabelNotFound() throws Exception {
        showFrame();
        TestContext context = new TestContext(this);
        step.setName("PortfolioList");
        step.setLabel("line25");
        step.setColumn(0);
        try {
            step.proceed(context);
            fail();
        }
        catch (Exception e) {
            assertEquals("Le label 'line25' n'existe pas dans la table PortfolioList", e.getMessage());
        }

        assertFalse(table.isCellSelected(0, 0));
        assertFalse(table.isCellSelected(0, 1));
        assertFalse(table.isCellSelected(1, 0));
        assertFalse(table.isCellSelected(1, 1));
        assertFalse(table.isCellSelected(2, 0));
        assertFalse(table.isCellSelected(2, 1));
    }


    public void test_jTableSelectCellByLabelDuplicate() throws Exception {
        showFrame();
        TestContext context = new TestContext(this);
        table.setValueAt("line2", 0, 0);
        step.setName("PortfolioList");
        step.setLabel("line2");
        step.setColumn(0);
        try {
            step.proceed(context);
            fail();
        }
        catch (Exception e) {
            assertEquals("Le label 'line2' est présent plusieurs fois dans la table PortfolioList",
                         e.getMessage());
        }

        assertFalse(table.isCellSelected(0, 0));
        assertFalse(table.isCellSelected(0, 1));
        assertFalse(table.isCellSelected(1, 0));
        assertFalse(table.isCellSelected(1, 1));
        assertFalse(table.isCellSelected(2, 0));
        assertFalse(table.isCellSelected(2, 1));
    }


    public void test_jTableSelectCellNeitherLabelNorRow() throws Exception {
        showFrame();
        TestContext context = new TestContext(this);
        step.setName("PortfolioList");
        step.setColumn(0);
        try {
            step.proceed(context);
            fail();
        }
        catch (Exception e) {
            assertEquals("La ligne -1 n'existe pas dans la table PortfolioList", e.getMessage());
        }

        assertFalse(table.isCellSelected(0, 0));
        assertFalse(table.isCellSelected(0, 1));
        assertFalse(table.isCellSelected(1, 0));
        assertFalse(table.isCellSelected(1, 1));
        assertFalse(table.isCellSelected(2, 0));
        assertFalse(table.isCellSelected(2, 1));
    }


    private void showFrame() {
        JFrame frame = new JFrame();
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }


    private void checkListSelection(JList checkedList, int[] expectedIndexes) {
        step.proceed(new TestContext(this));
        assertEquals(expectedIndexes.length, checkedList.getSelectedIndices().length);
        for (int expectedIndexe : expectedIndexes) {
            assertTrue(checkedList.isSelectedIndex(expectedIndexe));
        }
    }


    private void checkDisabledComponentSelection(Functor functor, String componentName) {
        step = new SelectStep();
        step.setTimeout(1);
        step.setName(componentName);
        functor.setAttribute(step);
        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (GuiConfigurationException ex) {
            assertEquals("Le composant '" + componentName + "' n'est pas éditable.", ex.getMessage());
        }
    }


    private void addParasite() {
        JPanel parasite = new JPanel();
        parasite.setBackground(Color.yellow);
        parasite.setPreferredSize(new Dimension(40, 30));
        parasite.setName("Parasite");
        panel.add(parasite);
    }


    private void addJCombo() {
        comboBox = new JComboBox(new Object[]{"Roadmaster", "Christine", "Cujo"});
        comboBox.setName("combo");
        panel.add(comboBox);
    }


    private void addTree() {
        tree = new JTree();
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
        tree.setName("treeName");
        panel.add(tree);
    }


    private void addList(String name, String[] listData) {
        list = new JList(listData);
        list.setName(name);
        panel.add(list);
    }


    private void addTable() {
        table =
              new JTable(new String[][]{
                    {"line0", "col 0 1"},
                    {"line1", "col 1 1"},
                    {"line2", "col 2 1"},
              }, new String[]{"Col", "Col 1"});
        table.setName("PortfolioList");
        panel.add(table);
    }


    private void addPopupMenu() {
        popup = new JPopupMenu();
        popup.add("ligne1");
        popup.add("ligne2");
        popup.add("ligne3");
        popup.setName("Popup");
        panel.add(popup);
    }


    private interface Functor {
        void setAttribute(SelectStep step);
    }

    private class BadRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            return badRendererComponent;
        }
    }

    private class ThisIsPrefixRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            return super.getListCellRendererComponent(list, "this is " + value, index, isSelected,
                                                      cellHasFocus);
        }
    }
}
