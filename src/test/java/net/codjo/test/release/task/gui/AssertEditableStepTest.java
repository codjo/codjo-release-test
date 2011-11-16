/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
/**
 * Classe de test de {@link net.codjo.test.release.task.gui.AssertEditableStep}.
 */
public class AssertEditableStepTest extends JFCTestCase {
    private static final String BUTTON_NAME = "BUTTON_NAME";
    private static final String TEXT_FIELD_NAME = "TEXT_FIELD_NAME";
    private static final String TEXT_AREA_NAME = "TEXT_AREO_NAME";
    private static final String COMBO_NAME = "COMBO_NAME";
    private static final String TABLE1_NAME = "TABLE1_NAME";
    private static final String CHECK_BOX_NAME = "CHECK_BOX_NAME";
    private static final String MY_COMPONENT_NAME = "MY_COMPONENT_NAME";
    private AssertEditableStep buttonStep;
    private AssertEditableStep textFieldStep;
    private AssertEditableStep textAreaStep;
    private AssertEditableStep comboStep;
    private AssertEditableStep tableStep;
    private AssertEditableStep checkBoxStep;
    private AssertEditableStep myComponentStep;
    private JButton button = new JButton();
    private JTextField textField = new JTextField();
    private JTextArea textArea = new JTextArea();
    private JCheckBox checkBox = new JCheckBox();
    private MyComponentWithEditableMethod myComponent = new MyComponentWithEditableMethod();
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


    @Override
    protected void setUp() throws Exception {
        buttonStep = new AssertEditableStep();
        buttonStep.setTimeout(1);
        buttonStep.setDelay(5);
        buttonStep.setWaitingNumber(10);

        textFieldStep = new AssertEditableStep();
        textFieldStep.setTimeout(1);
        textFieldStep.setDelay(5);
        textFieldStep.setWaitingNumber(10);

        textAreaStep = new AssertEditableStep();
        textAreaStep.setTimeout(1);
        textAreaStep.setDelay(5);
        textAreaStep.setWaitingNumber(10);

        comboStep = new AssertEditableStep();
        comboStep.setTimeout(1);
        comboStep.setDelay(5);
        comboStep.setWaitingNumber(10);

        tableStep = new AssertEditableStep();
        tableStep.setTimeout(1);
        tableStep.setDelay(5);
        tableStep.setWaitingNumber(10);

        checkBoxStep = new AssertEditableStep();
        checkBoxStep.setTimeout(1);
        checkBoxStep.setDelay(5);
        checkBoxStep.setWaitingNumber(10);

        myComponentStep = new AssertEditableStep();
        myComponentStep.setTimeout(1);
        myComponentStep.setDelay(5);
        myComponentStep.setWaitingNumber(10);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    public void test_defaultOk() throws Exception {
        comboBox.setName(COMBO_NAME);
        textField.setName(TEXT_FIELD_NAME);
        textArea.setName(TEXT_AREA_NAME);
        showFrame(new JComponent[]{comboBox, textArea, textField, checkBox, myComponent});

        comboStep.setName(COMBO_NAME);
        comboStep.setExpected("true");
        assertEquals(COMBO_NAME, comboStep.getName());
        assertEquals("true", comboStep.getExpected());

        textFieldStep.setName(TEXT_FIELD_NAME);
        textFieldStep.setExpected("false");

        assertEquals(TEXT_FIELD_NAME, textFieldStep.getName());
        assertEquals("false", textFieldStep.getExpected());

        textAreaStep.setName(TEXT_AREA_NAME);
        textAreaStep.setExpected("false");

        assertEquals(TEXT_AREA_NAME, textAreaStep.getName());
        assertEquals("false", textAreaStep.getExpected());

        checkBoxStep.setName(CHECK_BOX_NAME);
        checkBoxStep.setExpected("false");

        assertEquals(CHECK_BOX_NAME, checkBoxStep.getName());
        assertEquals("false", checkBoxStep.getExpected());

        myComponentStep.setName(MY_COMPONENT_NAME);
        myComponentStep.setExpected("false");

        assertEquals(MY_COMPONENT_NAME, myComponentStep.getName());
        assertEquals("false", myComponentStep.getExpected());
    }


    public void test_assertComboNotEditableByDefault()
          throws Exception {
        comboBox.setName(COMBO_NAME);
        showFrame(new JComponent[]{comboBox});

        comboStep.setName(COMBO_NAME);
        comboStep.setExpected("false");

        assertEquals(COMBO_NAME, comboStep.getName());
        assertEquals("false", comboStep.getExpected());

        TestContext context = new TestContext(this);
        try {
            comboStep.proceed(context);
        }
        catch (Exception e) {
            fail("Erreur alors que la valeur est celle attendue. " + e.getMessage());
        }
    }


    public void test_proceed_notSupported() throws Exception {
        button.setName(BUTTON_NAME);
        showFrame(new JComponent[]{button});

        buttonStep.setName(BUTTON_NAME);
        buttonStep.setExpected("true");

        TestContext context = new TestContext(this);
        try {
            buttonStep.proceed(context);
            fail("Button not supported");
        }
        catch (Exception e) {
        }
    }


    public void test_proceed_ok_enable() throws Exception {
        comboBox.setName(COMBO_NAME);
        comboBox.setEditable(true);
        textArea.setName(TEXT_AREA_NAME);
        textArea.setEditable(true);
        textField.setName(TEXT_FIELD_NAME);
        textField.setEditable(true);
        myComponent.setName(MY_COMPONENT_NAME);
        myComponent.setEditable(true);
        showFrame(new JComponent[]{comboBox, textArea, textField, myComponent});

        comboStep.setName(COMBO_NAME);
        comboStep.setExpected("true");

        textAreaStep.setName(TEXT_AREA_NAME);
        textAreaStep.setExpected("true");

        textFieldStep.setName(TEXT_FIELD_NAME);
        textFieldStep.setExpected("true");

        myComponentStep.setName(MY_COMPONENT_NAME);
        myComponentStep.setExpected("true");

        TestContext context = new TestContext(this);
        try {
            comboStep.proceed(context);
            textFieldStep.proceed(context);
            textAreaStep.proceed(context);
            myComponentStep.proceed(context);
        }
        catch (Exception e) {
            fail("Erreur alors que la valeur est celle attendue.");
        }
    }


    public void test_proceed_ok_disable() throws Exception {
        comboBox.setName(COMBO_NAME);
        comboBox.setEditable(false);
        textArea.setName(TEXT_AREA_NAME);
        textArea.setEditable(false);
        textField.setName(TEXT_FIELD_NAME);
        textField.setEditable(false);
        myComponent.setName(MY_COMPONENT_NAME);
        myComponent.setEditable(false);

        showFrame(new JComponent[]{comboBox, textArea, textField, myComponent});

        comboStep.setName(COMBO_NAME);
        comboStep.setExpected("false");

        textAreaStep.setName(TEXT_AREA_NAME);
        textAreaStep.setExpected("false");

        textFieldStep.setName(TEXT_FIELD_NAME);
        textFieldStep.setExpected("false");

        myComponentStep.setName(MY_COMPONENT_NAME);
        myComponentStep.setExpected("false");

        TestContext context = new TestContext(this);
        try {
            comboStep.proceed(context);
            textAreaStep.proceed(context);
            textFieldStep.proceed(context);
            myComponentStep.proceed(context);
        }
        catch (Exception e) {
            fail("Erreur alors que la valeur est celle attendue.");
        }
    }


    public void test_badTextFieldValue() throws Exception {
        textField.setName(TEXT_FIELD_NAME);

        showFrame(new JComponent[]{textField});
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


    public void test_badTextAreaValue() throws Exception {
        textArea.setName(TEXT_AREA_NAME);

        showFrame(new JComponent[]{textArea});
        textAreaStep.setName(TEXT_AREA_NAME);
        textAreaStep.setExpected("false");

        TestContext context = new TestContext(this);
        try {
            textAreaStep.proceed(context);
            fail("Pas d'erreur alors que la valeur n'est pas celle attendue.\"");
        }
        catch (Exception e) {
            ;
        }
    }


    public void test_badComboValue() throws Exception {
        comboBox.setName(COMBO_NAME);

        showFrame(new JComponent[]{comboBox});
        comboStep.setName(COMBO_NAME);
        comboStep.setExpected("true");

        TestContext context = new TestContext(this);
        try {
            comboStep.proceed(context);
            fail("Pas d'erreur alors que la valeur n'est pas celle attendue.\"");
        }
        catch (Exception e) {
            ;
        }
    }


    public void test_badMyComponentValue() throws Exception {
        myComponent.setName(MY_COMPONENT_NAME);

        showFrame(new JComponent[]{myComponent});
        myComponentStep.setName(MY_COMPONENT_NAME);
        myComponentStep.setExpected("false");

        TestContext context = new TestContext(this);
        try {
            myComponentStep.proceed(context);
            fail("Pas d'erreur alors que la valeur n'est pas celle attendue.\"");
        }
        catch (Exception e) {
            ;
        }
    }


    public void test_nok_notFound() throws Exception {
        textArea.setName("nimportekoi");
        textField.setName("badTextFieldName");

        showFrame(new JComponent[]{textArea, textField});
        textAreaStep.setName(TEXT_AREA_NAME);
        textAreaStep.setExpected("true");

        textFieldStep.setName(TEXT_FIELD_NAME);
        textFieldStep.setExpected("true");

        TestContext context = new TestContext(this);
        try {
            textAreaStep.proceed(context);
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


    public void test_proceed_table_Editable() throws Exception {
        table1.setName(TABLE1_NAME);

        showFrame(new Component[]{table1});
        TestContext context = new TestContext(this);

        table1.setEnabled(true);
        tableStep.setRow("0");
        tableStep.setColumn("chiffre");
        tableStep.setName(TABLE1_NAME);
        tableStep.setExpected("false");
        tableStep.proceed(context);

        tableStep.setColumn("1");
        tableStep.setExpected("false");
        tableStep.proceed(context);

        tableStep.setColumn("lettre");
        tableStep.setExpected("true");
        tableStep.proceed(context);

        tableStep.setColumn("0");
        tableStep.setExpected("true");
        tableStep.proceed(context);
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


    static class MyComponentWithEditableMethod extends JPanel {
        // GUI
        private JTextField textField1 = new JTextField();
        private JTextField textField2 = new JTextField();
        private GridBagLayout gridBagLayout1 = new GridBagLayout();


        MyComponentWithEditableMethod() {
            jbInit();
        }


        /**
         * Assemblage des composants.
         */
        void jbInit() {
            textField1.setName("textField1");
            textField2.setName("textField2");
            this.setLayout(gridBagLayout1);
            this.add(textField1,
                     new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
                                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            this.add(textField2,
                     new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH,
                                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }


        public void setEditable(boolean isEditable) {
            this.textField1.setEditable(isEditable);
            this.textField2.setEditable(isEditable);
        }


        public boolean isEditable() {
            return textField1.isEditable() && textField2.isEditable();
        }


        @Override
        public void setEnabled(boolean enabled) {
            textField1.setEnabled(enabled);
            textField2.setEnabled(enabled);
            super.setEnabled(enabled);
        }


        @Override
        public boolean isEnabled() {
            return textField1.isEnabled() && textField2.isEnabled();
        }
    }
}
