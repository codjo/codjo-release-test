package net.codjo.test.release.task.gui;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.eventdata.KeyEventData;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class PressKeyStep extends AbstractGuiStep {
    private static final Logger LOG = Logger.getLogger(PressKeyStep.class);
    private String value;
    private String name;


    public void proceed(TestContext context) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(value);
        if (keyStroke == null) {
            throw new IllegalStateException("La value '" + value + "' est incorrecte.\n"
                                            + "Utilisation : modifiers (<typedID> | <pressedReleasedID>)\n"
                                            + "     *\n"
                                            + "     *    modifiers := shift | control | ctrl | meta | alt | altGraph \n"
                                            + "     *    typedID := typed <typedKey>\n"
                                            + "     *    typedKey := string of length 1 giving Unicode character.\n"
                                            + "     *    pressedReleasedID := (pressed | released) key\n"
                                            + "     *    key := KeyEvent key code name, i.e. the name following \"VK_\".\n"
                                            + "Exemples :\n"
                                            + "\"ENTER\", \"INSERT\", \"control DELETE\", \"alt shift X\", \"alt shift released X\", \"typed a\"");
        }

        if (StringUtils.isEmpty(name)) {
            pressKeyWithRobot(keyStroke);
        }
        else {
            pressKeyWithSendKeyAction(context, keyStroke);
        }
    }


    private void pressKeyWithSendKeyAction(TestContext context, KeyStroke keyStroke) {
        NamedComponentFinder finder = new NamedComponentFinder(JComponent.class, name);
        final Component component = findOnlyOne(finder, context, getTimeout());
        if (component == null) {
            throw new GuiFindException("Le composant '" + getName() + "' est introuvable.");
        }

        if (!component.isEnabled()) {
            throw new GuiConfigurationException(computeUneditableComponent(name));
        }

        JFCTestCase testCase = context.getTestCase();
        KeyEventData keyEventData = new KeyEventData(testCase,
                                                     component,
                                                     keyStroke.getKeyCode());
        keyEventData.setModifiers(keyStroke.getModifiers());

        testCase.getHelper().sendKeyAction(keyEventData);
    }


    private void pressKeyWithRobot(KeyStroke keyStroke) {
        try {
            Robot robot = new Robot();
            int modifiers = keyStroke.getModifiers();
            pressModifierKeys(modifiers, robot);

            robot.keyPress(keyStroke.getKeyCode());
            robot.keyRelease(keyStroke.getKeyCode());

            releaseModifierKeys(modifiers, robot);

            robot.delay(10);
        }
        catch (AWTException e) {
            LOG.error("Impossible d'instancier Robot", e);
        }
    }


    private void releaseModifierKeys(int modifiers, Robot robot) {
        if ((modifiers & KeyEvent.SHIFT_MASK) != 0) {
            robot.keyRelease(KeyEvent.VK_SHIFT);
        }
        if ((modifiers & KeyEvent.CTRL_MASK) != 0) {
            robot.keyRelease(KeyEvent.VK_CONTROL);
        }
        if ((modifiers & KeyEvent.ALT_MASK) != 0) {
            robot.keyRelease(KeyEvent.VK_ALT);
        }
        if ((modifiers & KeyEvent.META_MASK) != 0) {
            robot.keyRelease(KeyEvent.VK_META);
        }
        if ((modifiers & KeyEvent.ALT_GRAPH_MASK) != 0) {
            robot.keyRelease(KeyEvent.VK_ALT_GRAPH);
        }
    }


    private void pressModifierKeys(int modifiers, Robot robot) {
        if ((modifiers & KeyEvent.SHIFT_MASK) != 0) {
            robot.keyPress(KeyEvent.VK_SHIFT);
        }
        if ((modifiers & KeyEvent.CTRL_MASK) != 0) {
            robot.keyPress(KeyEvent.VK_CONTROL);
        }
        if ((modifiers & KeyEvent.ALT_MASK) != 0) {
            robot.keyPress(KeyEvent.VK_ALT);
        }
        if ((modifiers & KeyEvent.META_MASK) != 0) {
            robot.keyPress(KeyEvent.VK_META);
        }
        if ((modifiers & KeyEvent.ALT_GRAPH_MASK) != 0) {
            robot.keyPress(KeyEvent.VK_ALT_GRAPH);
        }
    }


    static String computeUneditableComponent(String componentName) {
        return "Le composant '" + componentName + "' n'est pas éditable.";
    }


    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }
}
