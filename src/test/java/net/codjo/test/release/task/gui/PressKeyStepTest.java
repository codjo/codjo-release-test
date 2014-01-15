package net.codjo.test.release.task.gui;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JTextField;
import junit.extensions.jfcunit.JFCTestCase;
import net.codjo.test.common.LogString;

public class PressKeyStepTest extends JFCTestCase {
    private final LogString log = new LogString();
    private PressKeyStep step;
    private JTextField textField;


    public void test_pressKeyUsingRobot() throws Exception {
        showFrame();

        step.setValue("D");
        step.proceed(new TestContext(this));

        assertPressKeyUsingRobot("D");
    }


    public void test_pressKeyUsingRobot_ko() throws Exception {
        showFrame();

        step.setValue("a");
        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (IllegalStateException e) {
            assertTrue(e.getLocalizedMessage().startsWith("La value 'a' est incorrecte."));
        }
    }


    public void test_pressKeyUsingRobot_shift() throws Exception {
        showFrame();

        step.setValue("shift D");
        step.proceed(new TestContext(this));

        assertPressKeyUsingRobot("Maj D");
    }


    public void test_pressKeyUsingRobot_enter() throws Exception {
        showFrame();

        step.setValue("ENTER");
        step.proceed(new TestContext(this));

        assertPressKeyUsingRobot("Entrée");
    }


    public void test_pressKeyUsingRobot_ctrlA() throws Exception {
        showFrame();

        step.setValue("ctrl A");
        step.proceed(new TestContext(this));

        assertPressKeyUsingRobot("Ctrl A");
    }


    public void test_textFieldOkWithName() throws Exception {
        String textfieldName = "textfield1";
        textField.setName(textfieldName);
        step.setName(textfieldName);

        showFrame();

        step.setValue("D");
        step.proceed(new TestContext(this));
        assertUntilEquals("d", textField.getText());
    }


    public void test_textFieldOkWithName_ctrlA() throws Exception {
        String textfieldName = "textfield2";
        textField.setName(textfieldName);
        step.setName(textfieldName);

        showFrame();

        textField.setText("Db");
        step.setValue("ctrl A");
        step.proceed(new TestContext(this));
        assertUntilEquals("Db", textField.getText());
        assertUntilEquals("Db", textField.getSelectedText());
    }


    // bug jfc?
    public void textFieldOkWithName_shift() throws Exception {
        String textfieldName = "textfield3";
        textField.setName(textfieldName);
        step.setName(textfieldName);

        showFrame();

        step.setValue("shift D");
        step.proceed(new TestContext(this));
        assertUntilEquals("D", textField.getText());
    }


    public void test_textFieldOkWithName_enter() throws Exception {
        String textfieldName = "textfield4";
        textField.setName(textfieldName);
        step.setName(textfieldName);

        showFrame();

        step.setValue("ENTER");
        step.proceed(new TestContext(this));
        assertUntilEquals(Color.RED, textField.getBackground());
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createTextField();

        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            public void eventDispatched(AWTEvent event) {
                String[] params = event.paramString().split(",");
                log.info(String.format("%s, %s", params[0], params[2]));
            }
        }, KeyEvent.KEY_EVENT_MASK);

        step = new PressKeyStep();
    }


    private void createTextField() {
        textField = new JTextField(10);
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                textField.setBackground(Color.RED);
            }
        });
    }


    private void showFrame() {
        JFrame frame = new JFrame();
        frame.add(textField);
        frame.pack();
        frame.setVisible(true);
        frame.toFront();

        flushAWT();
    }


    private void assertPressKeyUsingRobot(String expectedText) {
        List<String> expectedList = Arrays.asList(expectedText.split(" "));

        StringBuilder expectedBuffer = new StringBuilder();
        for (String expected : expectedList) {
            if (expectedBuffer.length() > 0) {
                expectedBuffer.append(", ");
            }
            expectedBuffer.append("KEY_PRESSED, keyText=").append(expected);
        }

        expectedBuffer.append(", KEY_TYPED, keyText=Unknown keyCode: 0x0");

        Collections.reverse(expectedList);
        for (String expected : expectedList) {
            expectedBuffer.append(", KEY_RELEASED, keyText=").append(expected);
        }

        assertUntilEquals(expectedBuffer.toString(), log);
    }


    private void assertUntilEquals(Object expected, Object actual) {
        AssertionError exception;
        long startTime = System.currentTimeMillis();
        do {
            try {
                assertEquals(expected, actual);
                return;
            }
            catch (AssertionError e) {
                exception = e;
                flushAWT();
            }
        }
        while (System.currentTimeMillis() - startTime < 1000);

        throw exception;
    }


    private void assertUntilEquals(String expected, LogString logString) {
        AssertionError exception;
        long startTime = System.currentTimeMillis();
        do {
            try {
                logString.assertContent(expected);
                return;
            }
            catch (AssertionError e) {
                exception = e;
                flushAWT();
            }
        }
        while (System.currentTimeMillis() - startTime < 2000);

        throw exception;
    }
}
