package net.codjo.test.release.task.gui;
import net.codjo.test.release.task.gui.matcher.MatcherFactory;
import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
/**
 *
 */
public class AssertTooltipStepTest extends JFCTestCase {
    private AssertTooltipStep step;
    private JTextField textField;


    @Override
    protected void setUp() throws Exception {
        step = new AssertTooltipStep();
        step.setTimeout(1);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    public void test_assertOK() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("myTextField");
        step.setExpected("je suis un textField");
        step.proceed(context);

        step.setName("myCombo");
        step.setExpected("je suis une combo");
        step.proceed(context);
    }


    public void test_assertOKNoTooltip() throws Exception {
        showFrame();
        textField.setToolTipText(null);

        TestContext context = new TestContext(this);
        step.setName("myTextField");
        step.setExpected("");
        step.proceed(context);
    }


    public void test_assertOKWithMatching() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("myTextField");
        step.setMatching(MatcherFactory.STARTS_WITH_MATCHING);
        step.setExpected("je suis");
        step.proceed(context);

        step.setName("myCombo");
        step.setMatching(MatcherFactory.CONTAINS_MATCHING);
        step.setExpected("combo");
        step.proceed(context);
    }


    public void test_assertKOBadTooltip() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("myTextField");
        step.setExpected("bad expected tooltip");
        try {
            step.proceed(context);
            fail("Pas d'erreur alors que l'expected ne correspond pas à l'actual !");
        }
        catch (Exception e) {
            assertEquals("Composant Component 'myTextField' : "
                         + "attendu='bad expected tooltip' obtenu='je suis un textField'", e.getMessage());
        }
    }


    public void test_assertKONoTooltip() throws Exception {
        showFrame();
        textField.setToolTipText(null);

        TestContext context = new TestContext(this);
        step.setName("myTextField");
        step.setExpected("a tooltip");
        try {
            step.proceed(context);
            fail("Pas d'erreur alors que l'expected ne correspond pas à l'actual !");
        }
        catch (Exception e) {
            assertEquals("Le composant 'myTextField' n'a pas de tooltip.", e.getMessage());
        }
    }


    public void test_assertRowTableToolTip() {
        showFrame();
        TestContext context = new TestContext(this);
        step.setName("tableForToolTipTest");

        step.setRow(0);
        step.setExpected("Swing");
        step.proceed(context);

        try {
            step.setExpected("toto");
            step.proceed(context);
            fail();
        }
        catch (GuiAssertException guiEx) {

        }

        step.setRow(1);
        step.setExpected("SwingLight");
        step.proceed(context);

        step.setRow(2);
        step.setExpected("Gin");
        step.proceed(context);

        step.setRow(3);
        step.setExpected("GinLight");
        step.proceed(context);

        step.setRow(4);
        step.setExpected("Advance");
        step.proceed(context);
    }


    private void showFrame() {
        JFrame frame = new JFrame();

        JPanel mainPanel = new JPanel(new BorderLayout());
        textField = new JTextField();
        mainPanel.add(configureJComponent(textField,
                                          "myTextField",
                                          "je suis un textField"),
                      BorderLayout.CENTER);
        mainPanel.add(configureJComponent(new JComboBox(new String[]{"1", "2", "3"}),
                                          "myCombo",
                                          "je suis une combo"),
                      BorderLayout.SOUTH);
        mainPanel.add(configureJComponent(new JList(new String[]{"A", "B"}),
                                          "myList",
                                          null),
                      BorderLayout.NORTH);

        AssertTooltipStepTest.TableForToolTipTest forToolTipTest = new TableForToolTipTest();
        forToolTipTest.setName("tableForToolTipTest");
        mainPanel.add(new JScrollPane(forToolTipTest), BorderLayout.WEST);

        frame.setContentPane(mainPanel);
        frame.setSize(300, 200);
        frame.setVisible(true);
        flushAWT();
    }


    private JComponent configureJComponent(JComponent component, String name, String tooltip) {
        component.setName(name);
        if (tooltip != null) {
            component.setToolTipText(tooltip);
        }
        return component;
    }


    private class TableForToolTipTest extends JTable {

        private final String[][] donnees = new String[][]{
              {"Swing", "Astral", "standard"},
              {"SwingLight", "Mistral", "standard"},
              {"Gin", "Oasis", "standard"},
              {"GinLight", "boomerang", "compétition"},
              {"Advance", "Omega", "performance"}
        };

        private final String[] colonnes = {"marque", "modèle", "homologation"};


        TableForToolTipTest() {
            DefaultTableModel model = new DefaultTableModel(colonnes, 0);
            for (String[] donnee : donnees) {
                model.addRow(donnee);
            }
            setModel(model);
        }


        @Override
        public String getToolTipText(MouseEvent event) {
            int row = rowAtPoint(event.getPoint());
            return (String)getValueAt(row, 0);
        }
    }
}
