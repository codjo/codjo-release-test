/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import net.codjo.test.common.LogString;
import net.codjo.test.release.task.gui.matcher.MatcherFactory;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
import junit.extensions.jfcunit.finder.DialogFinder;
/**
 * Classe de test de {@link AssertValueStepTest}.
 */
public class AssertValueStepTest extends JFCTestCase {
    private AssertValueStep step;
    private JTextField textField1;
    private JTextField textField2;
    private JTextArea textArea;
    private JLabel label;
    private JComboBox comboBox;
    private JRadioButton radioButton;
    private JCheckBox checkBox;
    private JButton button;
    private JTabbedPane tabbedPane;
    private JPanel parasite;
    private JSpinner spinner;
    private JSlider slider;
    private JEditorPane htmlEditorPane;


    @Override
    protected void setUp() {
        step = new AssertValueStep();
        step.setTimeout(1);
        step.setDelay(5);
        step.setWaitingNumber(10);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    public void test_nok_badValue() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName(textField1.getName());
        textField1.setText("05");
        step.setExpected("08");

        try {
            step.proceed(context);
            fail("Pas d'erreur alors que la valeur n'est pas celle attendue pour un TextField.");
        }
        catch (GuiAssertException ex) {
            ; // Cas normal
        }

        step.setName(radioButton.getName());
        radioButton.setSelected(false);
        step.setExpected("true");

        try {
            step.proceed(context);
            fail("Pas d'erreur alors que la valeur n'est pas celle attendue pour un RadioButton.");
        }
        catch (GuiAssertException ex) {
            ; // Cas normal
        }
    }


    public void test_defaults() throws Exception {
        assertEquals(null, step.getName());
        assertEquals(null, step.getExpected());
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


    public void test_ok() throws Exception {
        showFrame();

        // JTextField
        textField1.setText("05");
        step.setName(textField1.getName());
        step.setExpected("05");
        step.proceed(new TestContext(this));

        // JComboBox
        comboBox.setSelectedItem("Vert");
        step.setName(comboBox.getName());
        step.setExpected("Vert");
        step.proceed(new TestContext(this));

        // JButton
        button.setText("button");
        step.setName(button.getName());
        step.setExpected("button");
        step.proceed(new TestContext(this));

        // JCheckBox
        checkBox.setSelected(false);
        step.setName(checkBox.getName());
        step.setExpected("false");
        step.proceed(new TestContext(this));

        // JRadioButton
        radioButton.setSelected(false);
        step.setName(radioButton.getName());
        step.setExpected("false");
        step.proceed(new TestContext(this));

        // JTextArea
        final String value = "première ligne\ndeuxième ligne";
        textArea.setText(value);
        step.setName(textArea.getName());
        step.setExpected(value);
        step.proceed(new TestContext(this));

        // JLabel
        final String labelValue = "contenu du label";
        label.setText(labelValue);
        step.setName(label.getName());
        step.setExpected(labelValue);
        step.proceed(new TestContext(this));

        // JSpinner
        final String spinnerValue = "100";
        spinner.setValue(Integer.parseInt(spinnerValue));
        step.setName(spinner.getName());
        step.setExpected(spinnerValue);
        step.proceed(new TestContext(this));

        // JSlider
        final String sliderValue = "5";
        slider.setValue(Integer.parseInt(sliderValue));
        step.setName(slider.getName());
        step.setExpected(sliderValue);
        step.proceed(new TestContext(this));

        step.setName(tabbedPane.getName());
        step.setExpected("tab2");
        step.proceed(new TestContext(this));
    }


    public void test_matchingTextfieldOk() throws Exception {
        showFrame();

        textField1.setText("Toto titi tutu Pouet");
        step.setName(textField1.getName());
        step.setExpected("Toto titi tutu Pouet");
        step.proceed(new TestContext(this));

        step.setMatching(MatcherFactory.EQUALS_MATCHING);
        step.setExpected("Toto titi tutu Pouet");
        step.proceed(new TestContext(this));

        step.setMatching(MatcherFactory.CONTAINS_MATCHING);
        step.setExpected("iti");
        step.proceed(new TestContext(this));

        step.setMatching(MatcherFactory.STARTS_WITH_MATCHING);
        step.setExpected("Toto");
        step.proceed(new TestContext(this));

        step.setMatching(MatcherFactory.ENDS_WITH_MATCHING);
        step.setExpected("Pouet");
        step.proceed(new TestContext(this));
    }


    public void test_matchingTextfieldKo() throws Exception {
        showFrame();

        textField1.setText("Toto titi tutu Pouet");
        step.setName(textField1.getName());
        step.setExpected("Toto titi tutu Pouet");
        step.proceed(new TestContext(this));

        step.setMatching(MatcherFactory.CONTAINS_MATCHING);
        step.setExpected("prisonnier");
        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (Exception e) {
            assertEquals("Composant 'PortfolioCode' : attendu='prisonnier' obtenu='Toto titi tutu Pouet'",
                         e.getMessage());
        }

        step.setMatching(MatcherFactory.STARTS_WITH_MATCHING);
        step.setExpected("oto");
        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (Exception e) {
            assertEquals("Composant 'PortfolioCode' : attendu='oto' obtenu='Toto titi tutu Pouet'",
                         e.getMessage());
        }

        step.setMatching(MatcherFactory.ENDS_WITH_MATCHING);
        step.setExpected("handsWith");
        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (Exception e) {
            assertEquals("Composant 'PortfolioCode' : attendu='handsWith' obtenu='Toto titi tutu Pouet'",
                         e.getMessage());
        }
    }


    public void test_matchingCombo() throws Exception {
        showFrame();

        comboBox.setSelectedIndex(0);
        step.setName(comboBox.getName());
        step.setMatching(MatcherFactory.CONTAINS_MATCHING);
        step.setExpected("ouge");
        step.proceed(new TestContext(this));

        step.setExpected("ruuuge");
        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (Exception e) {
            assertEquals("Composant 'ComboBox' : attendu='ruuuge' obtenu='Rouge'", e.getMessage());
        }
    }


    public void test_matchingDialogMessage() throws Exception {
        addDialog();
        step.setTimeout(2);
        step.setDialogTitle("mon titre");
        step.setMatching(MatcherFactory.CONTAINS_MATCHING);
        step.setExpected("age");
        step.proceed(new TestContext(this));

        step.setExpected("anti-age");
        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (Exception e) {
            assertEquals("Composant 'mon titre' : attendu='anti-age' obtenu='mon message'", e.getMessage());
            TestHelper.disposeWindow((Window)new DialogFinder("mon titre").find(), this);
        }
    }


    public void test_matchingTabbedPane() {
        showFrame();
        tabbedPane.setSelectedIndex(0);

        step.setName(tabbedPane.getName());
        step.setMatching(MatcherFactory.CONTAINS_MATCHING);
        step.setExpected("ab1");
        step.proceed(new TestContext(this));

        step.setExpected("ab4");
        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (Exception e) {
            assertEquals("Composant 'tabbedPane' : attendu='ab4' obtenu='tab1'", e.getMessage());
        }
    }


    public void test_proceed_JButton() {
        showFrame();
        step.setName(button.getName());
        step.setExpected("Mon button");
        try {
            step.proceed(new TestContext(this));
            fail("GuiAssertException attendue !");
        }
        catch (GuiAssertException e) {
            assertEquals("Composant 'Button' : attendu='Mon button' obtenu='Mon boutton'", e.getMessage());
        }
    }


    public void test_proceed_JSpinner() {
        showFrame();
        spinner.setValue(123);
        step.setName(spinner.getName());
        step.setExpected("100");
        try {
            step.proceed(new TestContext(this));
            fail("GuiAssertException attendue !");
        }
        catch (GuiAssertException e) {
            assertEquals("Composant 'spinner' : attendu='100' obtenu='123'", e.getMessage());
        }
    }


    public void test_proceed_JSlider() {
        showFrame();
        slider.setValue(5);
        step.setName(slider.getName());
        step.setExpected("100");
        try {
            step.proceed(new TestContext(this));
            fail("GuiAssertException attendue !");
        }
        catch (GuiAssertException e) {
            assertEquals("Composant 'slider' : attendu='100' obtenu='5'", e.getMessage());
        }
    }


    public void test_proceed_ComboBox() {
        showFrame();
        step.setName(comboBox.getName());

        final String expected = "Mon Label Vert";
        ComboBoxRendererMock comboBoxRendererMock = new ComboBoxRendererMock();
        comboBoxRendererMock.mockGetListCellRendererComponent(new JLabel(expected));
        comboBox.setRenderer(comboBoxRendererMock);
        comboBox.setSelectedItem("Vert");
        step.setExpected(expected);

        comboBoxRendererMock.log.clear();
        step.proceed(new TestContext(this));
        assertEndsWith("getListCellRendererComponent(Vert, 1, false)", comboBoxRendererMock.log);

        comboBoxRendererMock.mockGetListCellRendererComponent(new JLabel("Bad Label"));
        try {
            step.proceed(new TestContext(this));
            fail("GuiAssertException attendue !");
        }
        catch (GuiAssertException e) {
            assertEquals("Composant 'ComboBox' : attendu='Mon Label Vert' obtenu='Vert'", e.getMessage());
        }

        step.setMode(AbstractGuiStep.DISPLAY_MODE);
        try {
            step.proceed(new TestContext(this));
            fail("GuiAssertException attendue !");
        }
        catch (GuiAssertException e) {
            assertEquals("Composant 'ComboBox' : attendu='Mon Label Vert' obtenu='Bad Label'",
                         e.getMessage());
        }
    }


    public void test_proceed_dialog() throws Exception {
        addDialog();
        step.setTimeout(2);
        step.setDialogTitle("mon titre");
        step.setExpected("mon message");
        step.proceed(new TestContext(this));
        TestHelper.disposeWindow((Window)new DialogFinder("mon titre").find(), this);
    }


    public void test_mode_attribute() {
        showFrame();
        step.setName(comboBox.getName());
        assertNull(step.getMode());

        final String displayExpected = "Mon Label Vert";
        final String modelExpected = "Vert";
        final TestContext context = new TestContext(this);
        ComboBoxRendererMock comboBoxRendererMock = new ComboBoxRendererMock();
        comboBoxRendererMock.mockGetListCellRendererComponent(new JLabel(displayExpected));
        comboBox.setRenderer(comboBoxRendererMock);
        comboBox.setSelectedItem(modelExpected);

        step.setExpected(modelExpected);
        step.proceed(context);
        assertEquals(AbstractGuiStep.AUTO_MODE, step.getMode());

        step.setExpected(displayExpected);
        step.proceed(context);
        assertEquals(AbstractGuiStep.AUTO_MODE, step.getMode());

        step.setMode(AbstractGuiStep.DISPLAY_MODE);
        step.setExpected(displayExpected);
        step.proceed(context);
        step.setExpected(modelExpected);
        try {
            step.proceed(context);
            fail("GuiAssertException attendue !");
        }
        catch (GuiAssertException e) {
            ;
        }

        step.setMode(AbstractGuiStep.MODEL_MODE);
        step.setExpected(modelExpected);
        step.proceed(context);
        step.setExpected(displayExpected);
        try {
            step.proceed(context);
            fail("GuiAssertException attendue !");
        }
        catch (GuiAssertException e) {
            ;
        }

        step.setMode(AbstractGuiStep.AUTO_MODE);
        step.setExpected(modelExpected);
        step.proceed(context);
        step.setExpected(displayExpected);
        step.proceed(context);

        step.setMode("xxxx");
        try {
            step.proceed(context);
            fail("GuiAssertException attendue !");
        }
        catch (GuiAssertException e) {
            assertEquals("Invalid value of 'mode' attribute : must be in {\"auto\", \"display\", \"model\"}.",
                         e.getMessage());
        }

        comboBox.setRenderer(null);
        step.setExpected(modelExpected);
        step.setMode(AbstractGuiStep.DISPLAY_MODE);
        step.proceed(context);
        step.setMode(AbstractGuiStep.MODEL_MODE);
        step.proceed(context);
        step.setMode(AbstractGuiStep.AUTO_MODE);
        step.proceed(context);
    }


    public void test_ok_dynamicProperties() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        context.setProperty("val05", "05");

        // JTextField
        step.setName(textField1.getName());
        textField1.setText("05");
        step.setExpected("${val05}");
        step.proceed(context);
    }


    public void test_htmlEditor() throws Exception {
        showFrame();
        step.setName(htmlEditorPane.getName());
        htmlEditorPane.setText("Simple text with <b>bold</b>");

        step.setMode(AssertValueStep.AUTO_MODE);
        step.setExpected("Simple text with bold");
        step.proceed(new TestContext(this));

        step.setMode(AssertValueStep.DISPLAY_MODE);
        step.setExpected("Simple text with bold");
        step.proceed(new TestContext(this));

        step.setMode(AssertValueStep.MODEL_MODE);
        step.setExpected("Simple text with <b>bold</b>");
        step.proceed(new TestContext(this));
    }


    private void showFrame() {
        JFrame frame = new JFrame();

        JPanel panel = new JPanel();
        frame.add(panel);

        addTextField1(panel);
        addTextField2(panel);
        addTextArea(panel);
        addLabel(panel);
        addComboBox(panel);
        addJButton(panel);
        addCheckBox(panel);
        addRadioButton(panel);
        addParasite(panel);
        addTabbedPane(panel);
        addSpinner(panel);
        addSlider(panel);
        addHtmlEditorPane(panel);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }


    private void addSpinner(JPanel panel) {
        spinner = new JSpinner();
        spinner.setName("spinner");
        panel.add(spinner);
    }


    private void addSlider(JPanel panel) {
        slider = new JSlider();
        slider.setName("slider");
        panel.add(slider);
    }


    private void addTabbedPane(JPanel panel) {
        tabbedPane = new JTabbedPane();
        tabbedPane.setName("tabbedPane");
        tabbedPane.add("tab1", new JPanel());
        tabbedPane.add("tab2", new JPanel());
        tabbedPane.setSelectedIndex(1);
        panel.add(tabbedPane);
    }


    private void addTextField1(JPanel panel) {
        textField1 = new JTextField(10);
        textField1.setName("PortfolioCode");
        panel.add(textField1);
    }


    private void addTextField2(JPanel panel) {
        textField2 = new JTextField(8);
        textField2.setName("ChampCalculé");
        panel.add(textField2);
    }


    private void addTextArea(JPanel panel) {
        textArea = new JTextArea(4, 10);
        textArea.setName("TextArea");
        panel.add(textArea);
    }


    private void addLabel(JPanel panel) {
        label = new JLabel();
        label.setName("Label");
        panel.add(label);
    }


    private void addComboBox(JPanel panel) {
        comboBox = new JComboBox(new String[]{"Rouge", "Vert", "Bleu"});
        comboBox.setName("ComboBox");
        panel.add(comboBox);
    }


    private void addJButton(JPanel panel) {
        button = new JButton("Mon boutton");
        button.setName("Button");
        panel.add(button);
    }


    private void addCheckBox(JPanel panel) {
        checkBox = new JCheckBox();
        checkBox.setName("activated");
        panel.add(checkBox);
    }


    private void addRadioButton(JPanel panel) {
        radioButton = new JRadioButton();
        radioButton.setName("radioButton");
        panel.add(radioButton);
    }


    private void addParasite(JPanel panel) {
        parasite = new JPanel();
        parasite.setBackground(Color.yellow);
        parasite.setPreferredSize(new Dimension(40, 30));
        parasite.setName("Parasite");
        panel.add(parasite);
    }


    private void addHtmlEditorPane(JPanel panel) {
        htmlEditorPane = new JEditorPane();
        htmlEditorPane.setContentType("text/html");
        htmlEditorPane.setName("htmlEditorPane");
        panel.add(htmlEditorPane);
    }


    private void addDialog() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(null, "mon message", "mon titre", JOptionPane.ERROR_MESSAGE);
            }
        });
    }


    private void assertEndsWith(String expected, LogString log) {
        String content = log.getContent();
        assertTrue("Expected : '" + content + "' ends with '" + expected + "'", content.endsWith(expected));
    }


    private class ComboBoxRendererMock implements ListCellRenderer {
        private Component renderer;
        LogString log = new LogString();


        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            log.call("getListCellRendererComponent", value, String.valueOf(index),
                     String.valueOf(isSelected));
            return renderer;
        }


        public void mockGetListCellRendererComponent(Component rendererParam) {
            renderer = rendererParam;
        }
    }
}
