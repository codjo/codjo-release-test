/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
/**
 * Classe de test de {@link AssertEnabledStep}.
 */
public class AssertEnabledStepTest extends JFCTestCase {
    private static final String BUTTON_NAME = "BUTTON_NAME";
    private static final String TEXT_FIELD_NAME = "TEXT_FIELD_NAME";
    private static final String COMBO_NAME = "COMBO_NAME";
    private static final String TABLE1_NAME = "TABLE1_NAME";
    private static final String TABLE2_NAME = "TABLE2_NAME";
    private static final String TABLE3_NAME = "TABLE3_NAME";
    private static final String MENU_LABEL = "MENU_LABEL";
    private static final String MENU_ITEM_LABEL = "MENU_ITEM_LABEL";
    private AssertEnabledStep buttonStep;
    private AssertEnabledStep textFieldStep;
    private AssertEnabledStep comboStep;
    private AssertEnabledStep tableStep;
    private AssertEnabledStep menuStep;
    private AssertEnabledStep menuItemStep;
    private JButton button = new JButton();
    private JTextField textField = new JTextField();
    private JComboBox comboBox = new JComboBox(new String[]{"toto", "titi"});
    private JTable table1 =
          new JTable(new String[][]{
                {"a", "1"},
                {"b", "2"}
          }, new String[]{"lettre", "chiffre"}) {
              @Override
              public boolean isCellEditable(int rowIndex, int vColIndex) {
                  return vColIndex == 0;
              }
          };
    private JTable table2 =
          new JTable(new String[][]{
                {"a", "1"},
                {"b", "2"}
          }, new String[]{"lettre", "chiffre"});
    private JTable table3 = new JTable();
    private JMenu menu = new JMenu(MENU_LABEL);
    private JMenuItem menuItem = new JMenuItem(MENU_ITEM_LABEL);


    @Override
    protected void setUp() throws Exception {
        buttonStep = new AssertEnabledStep();
        buttonStep.setTimeout(1);
        buttonStep.setDelay(5);
        buttonStep.setWaitingNumber(10);

        textFieldStep = new AssertEnabledStep();
        textFieldStep.setTimeout(1);
        textFieldStep.setDelay(5);
        textFieldStep.setWaitingNumber(10);

        comboStep = new AssertEnabledStep();
        comboStep.setTimeout(1);
        comboStep.setDelay(5);
        comboStep.setWaitingNumber(10);

        tableStep = new AssertEnabledStep();
        tableStep.setTimeout(1);
        tableStep.setDelay(5);
        tableStep.setWaitingNumber(10);

        menuStep = new AssertEnabledStep();
        menuStep.setTimeout(1);
        menuStep.setDelay(5);
        menuStep.setWaitingNumber(10);

        menuItemStep = new AssertEnabledStep();
        menuItemStep.setTimeout(1);
        menuItemStep.setDelay(5);
        menuItemStep.setWaitingNumber(10);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    public void test_defaultOk() throws Exception {
        button.setName(BUTTON_NAME);
        textField.setName(TEXT_FIELD_NAME);
        showFrame(button, textField);

        buttonStep.setName(BUTTON_NAME);
        buttonStep.setExpected("true");
        textFieldStep.setName(TEXT_FIELD_NAME);
        textFieldStep.setExpected("false");

        assertEquals(BUTTON_NAME, buttonStep.getName());
        assertEquals("true", buttonStep.getExpected());

        assertEquals(TEXT_FIELD_NAME, textFieldStep.getName());
        assertEquals("false", textFieldStep.getExpected());
    }


    public void test_assertEnabled_combo() throws Exception {
        comboBox.setName(COMBO_NAME);
        showFrame(new JComponent[]{comboBox});

        comboStep.setName(COMBO_NAME);
        comboStep.setExpected("true");

        assertEquals(COMBO_NAME, comboStep.getName());
        assertEquals("true", comboStep.getExpected());

        TestContext context = new TestContext(this);
        try {
            comboStep.proceed(context);
        }
        catch (Exception e) {
            fail("Erreur alors que la valeur est celle attendue. " + e.getMessage());
        }
    }


    public void test_proceed_ok_enable() throws Exception {
        button.setName(BUTTON_NAME);
        button.setEnabled(true);
        textField.setName(TEXT_FIELD_NAME);
        textField.setEnabled(true);
        showFrame(button, textField);

        buttonStep.setName(BUTTON_NAME);
        buttonStep.setExpected("true");
        textFieldStep.setName(TEXT_FIELD_NAME);
        textFieldStep.setExpected("true");

        TestContext context = new TestContext(this);
        try {
            buttonStep.proceed(context);
            textFieldStep.proceed(context);
        }
        catch (Exception e) {
            fail("Erreur alors que la valeur est celle attendue.");
        }
    }


    public void test_proceed_ok_disable() throws Exception {
        button.setName(BUTTON_NAME);
        button.setEnabled(false);
        textField.setName(TEXT_FIELD_NAME);
        textField.setEnabled(false);
        showFrame(button, textField);

        buttonStep.setName(BUTTON_NAME);
        buttonStep.setExpected("false");
        textFieldStep.setName(TEXT_FIELD_NAME);
        textFieldStep.setExpected("false");

        TestContext context = new TestContext(this);
        try {
            buttonStep.proceed(context);
            textFieldStep.proceed(context);
        }
        catch (Exception e) {
            fail("Erreur alors que la valeur est celle attendue.");
        }
    }


    public void test_badButtonValue() throws Exception {
        button.setName(BUTTON_NAME);

        showFrame(button, null);
        buttonStep.setName(BUTTON_NAME);
        buttonStep.setExpected("false");

        TestContext context = new TestContext(this);
        try {
            buttonStep.proceed(context);
            fail("Pas d'erreur alors que la valeur n'est pas celle attendue.\"");
        }
        catch (Exception e) {
            ;
        }
    }


    public void test_badTextFieldValue() throws Exception {
        textField.setName(TEXT_FIELD_NAME);

        showFrame(null, textField);
        textFieldStep.setName(TEXT_FIELD_NAME);
        textFieldStep.setExpected("false");

        TestContext context = new TestContext(this);
        try {
            textFieldStep.proceed(context);
            fail("Pas d'erreur alors que la valeur n'est pas celle attendue.\"");
        }
        catch (Exception e) {
            ;
        }
    }


    public void test_nok_notFound() throws Exception {
        button.setName("nimportekoi");
        textField.setName("badTextFieldName");

        showFrame(button, textField);
        buttonStep.setName(BUTTON_NAME);
        buttonStep.setExpected("true");

        textFieldStep.setName(TEXT_FIELD_NAME);
        textFieldStep.setExpected("true");

        TestContext context = new TestContext(this);
        try {
            buttonStep.proceed(context);
            fail("Le composant n'existe pas.");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
        try {
            textFieldStep.proceed(context);
            fail("Le composant n'existe pas.");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
    }


    public void test_proceed_table_enabled() throws Exception {
        table1.setName(TABLE1_NAME);
        table2.setName(TABLE2_NAME);
        table3.setName(TABLE3_NAME);

        showFrame(new Component[]{table1, table2, table3});
        TestContext context = new TestContext(this);

        table1.setEnabled(false);
        tableStep.setRow("-1");
        tableStep.setColumn("-1");
        tableStep.setName(TABLE1_NAME);
        tableStep.setExpected("false");
        tableStep.proceed(context);

        table1.setEnabled(true);
        tableStep.setName(TABLE1_NAME);
        tableStep.setExpected("true");
        tableStep.proceed(context);

        tableStep.setName(TABLE1_NAME);
        tableStep.setRow("0");
        tableStep.setColumn("lettre");
        tableStep.setExpected("true");
        tableStep.proceed(context);

        tableStep.setRow("0");
        tableStep.setColumn("1");
        tableStep.setExpected("false");
        tableStep.proceed(context);

        tableStep.setName(TABLE2_NAME);
        tableStep.setRow("0");
        tableStep.setColumn("0");
        tableStep.setExpected("true");
        tableStep.proceed(context);

        tableStep.setRow("0");
        tableStep.setColumn("chiffre");
        tableStep.setExpected("true");
        tableStep.proceed(context);

        tableStep.setName(TABLE3_NAME);
        table3.setEnabled(false);
        tableStep.setRow("-1");
        tableStep.setExpected("false");
        tableStep.proceed(context);
    }


    public void test_proceed_menu() throws Exception {
        menu.setName(MENU_LABEL);
        showFrameWithMenu();

        TestContext context = new TestContext(this);

        menu.setEnabled(true);
        assertEquals(true, menu.isEnabled());

        menuStep.setName(MENU_LABEL);
        menuStep.setExpected("true");
        menuStep.proceed(context);

        menu.setEnabled(false);
        assertEquals(false, menu.isEnabled());

        menuStep.setExpected("false");
        menuStep.proceed(context);

        menuStep.setName(null);
        menuStep.setMenu(MENU_LABEL);

        menu.setEnabled(true);
        assertEquals(true, menu.isEnabled());
        menuStep.setExpected("true");
        menuStep.proceed(context);

        menu.setEnabled(false);
        assertEquals(false, menu.isEnabled());

        menuStep.setExpected("false");
        menuStep.proceed(context);
    }


    public void test_proceed_menuItem() throws Exception {
        menu.setName(MENU_LABEL);
        menuItem.setName(MENU_ITEM_LABEL);
        showFrameWithMenu();

        menuItemStep.setMenu(MENU_LABEL + ":" + MENU_ITEM_LABEL);

        TestContext context = new TestContext(this);

        menuItem.setEnabled(true);
        menuItemStep.setExpected("true");
        menuItemStep.proceed(context);

        menuItem.setEnabled(false);
        menuItemStep.setExpected("false");
        menuItemStep.proceed(context);

        menu.setEnabled(false);
        menuItem.setEnabled(true);
        menuItemStep.setExpected("false");
        menuItemStep.proceed(context);

        menu.setEnabled(false);
        menuItem.setEnabled(true);
        menuItemStep.setExpected("true");
        try {
            menuItemStep.proceed(context);
            fail();
        }
        catch (GuiAssertException exception) {
            assertEquals("Composant 'MENU_LABEL:MENU_ITEM_LABEL' : attendu='true' obtenu='false'",
                         exception.getMessage());
        }
    }


    private void showFrame(JButton btn, JTextField txtField) {
        JFrame frame = new JFrame();

        JPanel panel = new JPanel();
        if (btn != null) {
            btn.setText(btn.getName());
            panel.add(btn);
        }
        if (txtField != null) {
            txtField.setText(txtField.getName());
            panel.add(txtField);
        }
        addParasite(panel);
        frame.setContentPane(panel);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }


    private void showFrameWithMenu() {
        JFrame frame = new JFrame();

        JMenuBar jMenuBar = new JMenuBar();
        menu.add(menuItem);
        jMenuBar.add(menu);
        frame.setJMenuBar(jMenuBar);

        JPanel panel = new JPanel();
        addParasite(panel);
        frame.setContentPane(panel);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }


    private void showFrame(Component[] components) {
        JFrame frame = new JFrame();

        JPanel panel = new JPanel();

        for (Component component : components) {
            panel.add(component);
        }
        addParasite(panel);
        frame.setContentPane(panel);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }


    private void addParasite(JPanel panel) {
        JButton parasite = new JButton();
        parasite.setName("Parasite");
        parasite.setText(parasite.getName());
        panel.add(parasite);
    }
}
