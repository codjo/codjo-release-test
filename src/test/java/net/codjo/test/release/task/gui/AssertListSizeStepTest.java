/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
/**
 * Test de AssertListSizeStep.
 *
 * @version $Revision: 1.10 $
 */
public class AssertListSizeStepTest extends JFCTestCase {
    private AssertListSizeStep step;


    @Override
    protected void setUp() throws Exception {
        step = new AssertListSizeStep();
        step.setTimeout(3);
        step.setDelay(5);
        step.setWaitingNumber(10);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    public void test_defaults() throws Exception {
        assertEquals(null, step.getName());
        assertEquals(-1, step.getExpected());
    }


    public void test_ok() throws Exception {
        showFrame();

        step.setName("TableTest");
        step.setExpected(2);
        step.proceed(new TestContext(this));

        step.setName("ListTest");
        step.setExpected(2);
        step.proceed(new TestContext(this));

        step.setName("ListTestEmpty");
        step.setExpected(0);
        step.proceed(new TestContext(this));

        step.setName("ComboTest");
        step.setExpected(3);
        step.proceed(new TestContext(this));

        step.setName("PopupTest");
        step.setExpected(3);
        step.proceed(new TestContext(this));

        step.setName("TreeTest");
        step.setExpected(4);
        step.proceed(new TestContext(this));
    }


    public void test_ok_withRetry() throws Exception {
        Thread myThread =
              new Thread() {
                  @Override
                  public void run() {
                      try {
                          Thread.sleep(200);

                          JFrame frame = new JFrame("Test JTable");
                          DefaultListModel model = new DefaultListModel();
                          JList list = new JList(model);
                          model.addElement("A");
                          model.addElement("B");
                          list.setName("ListTest");
                          frame.getContentPane().add(list);
                          frame.pack();
                          frame.setVisible(true);

                          Thread.sleep(200);
                          model.addElement("Toto 1");
                          model.addElement("Toto 2");
                          frame.pack();
                      }
                      catch (InterruptedException e) {
                          ;
                      }
                  }
              };

        myThread.start();

        step.setName("ListTest");
        step.setTimeout(5);
        step.setDelay(30);
        step.setWaitingNumber(1000);
        step.setExpected(4);
        step.proceed(new TestContext(this));
    }


    public void test_not_ok() throws Exception {
        showFrame();

        step.setName("TableTest");
        step.setExpected(6);
        try {
            step.proceed(new TestContext(this));
            fail("Une exception devrait etre jeté");
        }
        catch (GuiAssertException ex) {
            ; // normal
        }
    }


    public void test_nok_tableNotFound() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("UneTableInconnue");
        step.setExpected(3);

        try {
            step.proceed(context);
            fail("Pas d'erreur alors que la table n'existe pas.");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
    }


    private void showFrame() {
        JFrame frame = new JFrame("Test JTable");

        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        JTable table =
              new JTable(new Object[][]{
                    {"l0c0", "l0c1", "l0c2"},
                    {"l1c0", "l1c1", "l1c2"}
              }, new Object[]{"Col 0", "Col 1", "Col 2"});
        table.setName("TableTest");
        panel.add(table);

        JList list = new JList(new Object[]{"A", "B"});
        list.setName("ListTest");
        panel.add(list);

        JList listEmpty = new JList();
        listEmpty.setName("ListTestEmpty");
        panel.add(listEmpty);

        JComboBox comboBox = new JComboBox(new Object[]{"A", "B", "C"});
        comboBox.setName("ComboTest");
        panel.add(comboBox);

        JPopupMenu popup = new JPopupMenu();
        popup.add("ligne1");
        popup.add("ligne2");
        popup.add("ligne3");
        popup.setName("PopupTest");
        panel.add(popup);

        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode();
        treeNode.add(new DefaultMutableTreeNode("child1"));
        DefaultMutableTreeNode treeChild2 = new DefaultMutableTreeNode("child2");
        treeChild2.add(new DefaultMutableTreeNode("subChild1"));
        treeNode.add(treeChild2);
        treeNode.add(new DefaultMutableTreeNode("child3"));
        JTree tree = new JTree(treeNode);
        tree.setName("TreeTest");
        panel.add(tree);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }
}
