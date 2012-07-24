/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JHTMLEditor;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.IllegalComponentStateException;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SwingUtilities;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
import net.codjo.test.common.LogString;
import net.codjo.test.release.task.gui.metainfo.AnotherComponentMock;
import net.codjo.test.release.task.gui.metainfo.AnotherComponentMockTestBehavior;
import net.codjo.test.release.task.gui.metainfo.MyComponentMock;
import net.codjo.test.release.task.gui.metainfo.MyComponentMockTestBehavior;
import net.codjo.test.release.task.gui.metainfo.YetAnotherComponentMock;
/**
 * Classe de test de {@link SetValueStep}.
 */
public class SetValueStepTest extends JFCTestCase {
    private SetValueStep step;
    private int nbKeyListenerCalled;
    private JTextField textField1;
    private JTextField textField2;
    private JTextArea textArea;
    private JComboBox comboBox;
    private JCheckBox checkBox;
    private JTable table;
    private JPanel parasite;
    private int nbCalledFocusLostTextField1;
    private int nbCalledFocusGainedTextField2;
    private MyComponentMock myComponent;
    private AnotherComponentMock anotherComponent;
    private YetAnotherComponentMock yetAnotherComponent;
    private JSpinner spinner;
    private JSlider slider;
    private JHTMLEditor htmlEditor;


    public void test_defaults() throws Exception {
        assertEquals(null, step.getName());
        assertEquals(null, step.getValue());
        assertEquals(SetValueStep.MODE_SETTER, step.getMode());
    }


    public void test_specificTestBehavior() throws Exception {
        showFrame();
        LogString logString = new LogString();
        MyComponentMockTestBehavior.LOG = logString;
        step.setName(myComponent.getName());
        myComponent.setText("Une valeur");
        step.setValue("Une autre valeur");
        step.proceed(new TestContext(this));
        logString.assertContent("MyComponentMockTestBehavior.setValue(" + myComponent + ", " + step + ")");
        logString.clear();
    }


    public void test_infoClassLegacy() throws Exception {
        showFrame();
        LogString logString = new LogString();
        MyComponentMockTestBehavior.LOG = logString;
        AnotherComponentMockTestBehavior.LOG = logString;

        // héritage et classInfo redéfinie
        step.setName(anotherComponent.getName());
        anotherComponent.setText("Une valeur");
        step.setValue("Une autre valeur");
        step.proceed(new TestContext(this));
        assertEquals("AnotherComponentMock.specificMethod : Une autre valeur", anotherComponent.getText());
        logString.assertContent(
              "AnotherComponentMockTestBehavior.setValue(" + anotherComponent + ", " + step + ")");

        logString.clear();
        // héritage et classInfo absente
        step.setName(yetAnotherComponent.getName());
        yetAnotherComponent.setText("Une valeur");
        step.setValue("Une autre valeur");
        step.proceed(new TestContext(this));
        assertEquals("YetAnotherComponentMock.specificMethod : Une autre valeur",
                     yetAnotherComponent.getText());
        logString.assertContent(
              "MyComponentMockTestBehavior.setValue(" + yetAnotherComponent + ", " + step + ")");
    }


    public void test_ok() throws Exception {
        showFrame();

        // JTextField
        step.setName(textField1.getName());
        textField1.setText("00");
        step.setValue("08");
        step.proceed(new TestContext(this));
        assertEquals("08", textField1.getText());

        // JComboBox
        step.setName(comboBox.getName());
        step.setValue("Vert");
        step.proceed(new TestContext(this));
        assertEquals(1, comboBox.getSelectedIndex());

        // JCheckBox
        step.setName(checkBox.getName());
        step.setValue("true");
        assertFalse(checkBox.isSelected());
        step.proceed(new TestContext(this));
        assertTrue(checkBox.isSelected());
        step.setValue("false");
        step.proceed(new TestContext(this));
        assertFalse(checkBox.isSelected());

        // JTextArea
        step.setName(textArea.getName());
        String expectedValue = "ligne 1\nligne 2";
        step.setValue(expectedValue);
        step.proceed(new TestContext(this));
        assertEquals(expectedValue, textArea.getText());

        // JSpinner
        step.setName(spinner.getName());
        step.setValue("200802");
        step.proceed(new TestContext(this));
        assertEquals("200802", spinner.getValue().toString());

        // JSlider
        step.setName(slider.getName());
        step.setValue("label3");
        step.proceed(new TestContext(this));
        assertEquals(3, slider.getValue());
    }


    public void test_modeKeyboard() throws Exception {
        String value = "Càeä âtéeèsëtê ïnîeö ômùaÿrçc€hÀeÄ ÂpÉaÈsË Ê!ÏÎÖÔÙŸÇ "
                       + "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,;:.?!/\\&é'(-_)~#{[|`^@]=+}%*-";
        showFrame();
        textField1.setText("");
        step.setName(textField1.getName());
        step.setValue(value);
        step.setMode("keyboard");
        step.proceed(new TestContext(this));
        assertEquals(value, textField1.getText());
    }


    public void test_setValue_spinner() throws Exception {
        showFrame();
        step.setName(spinner.getName());
        spinner.setValue("");

        step.setValue("200801");
        step.proceed(new TestContext(this));
        assertEquals("200801", spinner.getValue());
    }


    public void test_setEmptyValue() throws Exception {
        showFrame();

        // NumberField
        textField1.setText("Ceci n'est pas vide");
        step.setName(textField1.getName());
        step.setValue("");
        step.proceed(new TestContext(this));
        assertEquals("", textField1.getText());
    }


    public void test_setEmptyValueKeyboard() throws Exception {
        showFrame();

        // NumberField
        textField1.setText("Ceci n'est pas vide");
        step.setName(textField1.getName());
        step.setValue("");
        step.setMode("keyboard");
        step.proceed(new TestContext(this));
        assertEquals("", textField1.getText());
    }


    public void test_disabledComponent() throws Exception {
        showFrame();

        // JTextField
        checkDisabledComponent(textField1, "08");

        // JComboBox
        checkDisabledComponent(comboBox, "Vert");

        // JCheckBox
        checkDisabledComponent(checkBox, "true");

        // JTextArea
        checkDisabledComponent(textArea, "ligne 1\nligne 2");
    }


    private void checkDisabledComponent(JComponent component, String value) {
        component.setEnabled(false);
        step.setValue(value);
        step.setName(component.getName());
        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (GuiConfigurationException e) {
            assertEquals("Le composant '" + component.getName() + "' n'est pas éditable.", e.getMessage());
        }
    }


    public void test_badValueForCheckBox() throws Exception {
        showFrame();

        step.setName(checkBox.getName());
        step.setValue("badValue");
        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (GuiConfigurationException e) {
            assertEquals(SetValueStep.BAD_BOOLEAN_VALUE_MESSAGE, e.getMessage());
        }
    }


    public void test_element_not_in_combo() throws Exception {
        showFrame();

        // ComboBox non éditable
        step.setName(comboBox.getName());
        step.setValue("Orange");
        try {
            step.proceed(new TestContext(this));
            fail("Le composant Orange n'existe pas.");
        }
        catch (GuiFindException ex) {
            assertEquals("L'élément '" + step.getValue() + "' n'existe pas dans le composant "
                         + comboBox.getName() + ".", ex.getMessage());
        }

        // ComboBox éditable
        comboBox.setEditable(true);
        step.setName(comboBox.getName());
        step.setValue("Orange");
        try {
            step.proceed(new TestContext(this));
            assertEquals("Orange", comboBox.getEditor().getItem());
        }
        catch (GuiFindException ex) {
            fail("Ne doit pas se produire");
        }
        finally {
            comboBox.setEditable(false);
        }
    }


    public void test_ok_table() throws Exception {
        showFrame();

        // JTable
        step.setName(table.getName());
        assertEquals("2,1", table.getValueAt(2, 1));
        step.setRow(2);
        step.setColumn("1");
        step.setValue("modif");
        step.proceed(new TestContext(this));
        assertEquals("modif", table.getValueAt(2, 1));
        //
        step.setRow(0);
        step.setColumn("Col");
        step.setValue("modif");
        step.proceed(new TestContext(this));
        assertEquals("modif", table.getValueAt(0, 0));
        //
        step.setRow(1);
        step.setColumn("'15'");
        step.setValue("modif");
        step.proceed(new TestContext(this));
        assertEquals("modif", table.getValueAt(2, 1));

        try {
            step.setRow(1);
            step.setColumn("toto");
            step.setValue("modif");
            step.proceed(new TestContext(this));
            fail();
        }
        catch (GuiFindException e) {
            assertEquals(TableTools.computeUnknownColumnMessage(table, "toto"), e.getMessage());
        }
    }


    public void test_ok_focus() throws Exception {
        showFrame();
        textField1.requestFocus();
        flushAWT();

        assertEquals(0, nbCalledFocusLostTextField1);
        assertEquals(0, nbCalledFocusGainedTextField2);

        // JTextField
        step.setName(textField2.getName());
        step.setValue("123");
        step.proceed(new TestContext(this));
        assertEquals("123", textField2.getText());
        assertEquals(1, nbCalledFocusLostTextField1);
        assertEquals(1, nbCalledFocusGainedTextField2);
    }


    public void test_ok_listener() throws Exception {
        showFrame();

        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                assertTrue(SwingUtilities.isEventDispatchThread());
                nbKeyListenerCalled++;
            }
        });

        step.setName(textField1.getName());
        step.setValue("un texte");
        step.setMode(SetValueStep.MODE_KEYBOARD);
        step.proceed(new TestContext(this));
        assertEquals("un texte", textField1.getText());
        assertTrue("KeyListener a été appelé", nbKeyListenerCalled > 0);
    }


    public void test_nok_notFound() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("Branche");
        try {
            step.proceed(context);
            fail("Le composant n'existe pas.");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
    }


    public void test_nok_unknownComponent() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName(parasite.getName());
        try {
            step.proceed(context);
            fail("Type de composant non supporté.");
        }
        catch (GuiConfigurationException ex) {
            ; // Cas normal
        }
    }


    public void test_ok_dynamicProperties() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        context.setProperty("val05", "05");

        // JTextField
        step.setName(textField1.getName());
        step.setValue("${val05}");
        step.proceed(context);
        assertEquals("05", textField1.getText());
    }


    private void showFrame() {
        JFrame frame = new JFrame();

        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        addTextField1(panel);
        addTextField2(panel);
        addTextArea(panel);
        addComboBox(panel);
        addCheckBox(panel);
        addTable(panel);
        addParasite(panel);
        addMyComponents(panel);
        addSpinner(panel);
        addSlider(panel);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }

    private void addSpinner(JPanel panel) {
        spinner = new JSpinner();
        spinner.setModel(new SpinnerListModel(Arrays.asList("", "200801", "200802", "200803")));
        spinner.setName("spinner");
        panel.add(spinner);
    }


    private void addSlider(JPanel panel) {
        slider = new JSlider(1, 5);
        Dictionary<Integer, JLabel> hash = new Hashtable<Integer, JLabel>();
        hash.put(1, new JLabel("label1"));
        hash.put(2, new JLabel("label2"));
        hash.put(3, new JLabel("label3"));
        hash.put(4, new JLabel("label4"));
        hash.put(5, new JLabel("label5"));

        slider.setName("slider");
        slider.setLabelTable(hash);
        slider.setPaintLabels(true);
        slider.setValue(0);
        panel.add(slider);
    }


    private void addTextField1(JPanel panel) {
        textField1 = new JTextField(10);
        textField1.setName("PortfolioCode");
        textField1.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent event) {
                ; // rien
            }


            public void focusLost(FocusEvent event) {
                nbCalledFocusLostTextField1++;
            }
        });
        panel.add(textField1);
    }


    private void addTextField2(JPanel panel) {
        textField2 = new JTextField(8);
        textField2.setName("ChampCalculé");
        textField2.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent event) {
                nbCalledFocusGainedTextField2++;
            }


            public void focusLost(FocusEvent event) {
                ; // rien
            }
        });
        panel.add(textField2);
    }


    private void addTextArea(JPanel panel) {
        textArea = new JTextArea(4, 10);
        textArea.setName("TextArea");
        panel.add(textArea);
    }


    private void addComboBox(JPanel panel) {
        comboBox = new JComboBox(new String[]{"Rouge", "Vert", "Bleu"});
        comboBox.setName("ComboBox");
        panel.add(comboBox);
    }


    private void addMyComponents(JPanel panel) {
        myComponent = new MyComponentMock();
        myComponent.setName("myComponent");
        panel.add(myComponent);

        anotherComponent = new AnotherComponentMock();
        anotherComponent.setName("anotherComponent");
        panel.add(anotherComponent);

        yetAnotherComponent = new YetAnotherComponentMock();
        yetAnotherComponent.setName("yetAnotherComponent");
        panel.add(yetAnotherComponent);
    }


    private void addCheckBox(JPanel panel) {
        checkBox = new JCheckBox();
        checkBox.setName("activated");
        panel.add(checkBox);
    }


    private void addTable(JPanel panel) {
        table =
              new JTable(new String[][]{
                    {"0,0", "0,1", "a"},
                    {"1,0", "1,1", "b"},
                    {"2,0", "2,1", "c"},
              }, new String[]{"Col", "Col 1", "15"});
        table.setName("PortfolioList");
        panel.add(new JScrollPane(table));
    }


    private void addParasite(JPanel panel) {
        parasite = new JPanel();
        parasite.setBackground(Color.yellow);
        parasite.setPreferredSize(new Dimension(40, 30));
        parasite.setName("Parasite");
        panel.add(parasite);
    }


    @Override
    protected void setUp() {
        TestHelper.setKeyMapping(new FrenchKeyMapping());
        step = new SetValueStep();
        step.setTimeout(1);
        nbCalledFocusLostTextField1 = 0;
        nbCalledFocusGainedTextField2 = 0;
        nbKeyListenerCalled = 0;
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }
}
