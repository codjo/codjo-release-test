package net.codjo.test.release.task.gui;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
import junit.framework.AssertionFailedError;
import net.codjo.test.common.PathUtil;
import org.apache.tools.ant.Project;

import static net.codjo.test.release.task.AgfTask.TEST_DIRECTORY;
/**
 *
 */
public class AssertComponentImageStepTest extends JFCTestCase {
    private AssertComponentImageStep assertComponentImageStep;

    private JTextField textField1;
    private JPanel mainPanel;
    private Project project;


    @Override
    protected void setUp() {
        project = new Project();
        project.setProperty(
              TEST_DIRECTORY,
              PathUtil.findResourcesFileDirectory(getClass()).getPath());

        assertComponentImageStep = new AssertComponentImageStep();
        assertComponentImageStep.setTimeout(1);
        assertComponentImageStep.setDelay(5);
        assertComponentImageStep.setWaitingNumber(10);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    public void test_ok() throws Exception {
        showFrame();

        // JTextField
        assertComponentImageStep.setName(textField1.getName());
        assertComponentImageStep.setExpected("JTextField.bmp");
        assertComponentImageStep.proceed(new TestContext(this, project));

        // JPannel
        assertComponentImageStep.setName(mainPanel.getName());
        assertComponentImageStep.setExpected("JPanel.bmp");
        assertComponentImageStep.proceed(new TestContext(this, project));
    }


    public void test_nok() throws Exception {
        showFrame();
        assertComponentImageStep.setName(mainPanel.getName());
        assertComponentImageStep.setExpected("JTextField.bmp");

        try {
            assertComponentImageStep.proceed(new TestContext(this, project));
            fail("Pas d'erreur alors que la valeur n'est pas celle attendue pour un TextField.");
        }
        catch (AssertionFailedError cf) {
            ;
        }
    }


    private void showFrame() {
        JFrame frame = new JFrame();
        mainPanel = new JPanel();
        mainPanel.setName("mainPanel");
        frame.setContentPane(mainPanel);

        addTextField1(mainPanel);
        addTextField2(mainPanel);
        addTextArea(mainPanel);
        addLabel(mainPanel);
        addComboBox(mainPanel);
        addJButton(mainPanel);
        addCheckBox(mainPanel);
        addRadioButton(mainPanel);
        addSpinner(mainPanel);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }


    private void addSpinner(JPanel panel) {
        JSpinner spinner = new JSpinner();
        spinner.setName("spinner");
        panel.add(spinner);
    }


    private void addTextField1(JPanel panel) {
        textField1 = new JTextField(10);
        textField1.setName("PortfolioCode");
        textField1.setText("05");
        panel.add(textField1);
    }


    private void addTextField2(JPanel panel) {
        JTextField textField2 = new JTextField(8);
        textField2.setName("ChampCalculé");
        panel.add(textField2);
    }


    private void addTextArea(JPanel panel) {
        JTextArea textArea = new JTextArea(4, 10);
        textArea.setName("TextArea");
        textArea.setText("première ligne\ndeuxième ligne");
        textArea.setEditable(false);
        panel.add(textArea);
    }


    private void addLabel(JPanel panel) {
        JLabel label = new JLabel();
        label.setName("Label");
        label.setText("contenu du label");
        panel.add(label);
    }


    private void addComboBox(JPanel panel) {
        JComboBox comboBox = new JComboBox(new String[]{"Rouge", "Vert", "Bleu"});
        comboBox.setName("ComboBox");
        comboBox.setSelectedItem("Vert");
        panel.add(comboBox);
    }


    private void addJButton(JPanel panel) {
        JButton button = new JButton("Mon boutton");
        button.setName("Button");
        panel.add(button);
    }


    private void addCheckBox(JPanel panel) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setName("activated");
        checkBox.setSelected(false);
        panel.add(checkBox);
    }


    private void addRadioButton(JPanel panel) {
        JRadioButton radioButton = new JRadioButton();
        radioButton.setName("radioButton");
        radioButton.setSelected(false);
        panel.add(radioButton);
    }
}
