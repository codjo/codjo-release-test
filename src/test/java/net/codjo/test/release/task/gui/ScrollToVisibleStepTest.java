package net.codjo.test.release.task.gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
/**
 *
 */
public class ScrollToVisibleStepTest extends JFCTestCase {
    private static final Rectangle ZERO = new Rectangle(0, 0, 0, 0);
    private ScrollToVisibleStep step;
    private JScrollPane scrollPane;
    private JTree tree;
    private JComboBox comboBox;
    private JTextField textField;
    private JButton button;


    @Override
    protected void setUp() throws Exception {
        tree = new JTree();
        tree.setName("tree");
        JScrollPane treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setPreferredSize(new Dimension(50, 30));
        comboBox = new JComboBox(new String[]{"alpha", "bravo", "charlie"});
        comboBox.setName("combobox");
        textField = new JTextField();
        textField.setName("textfield");
        textField.setPreferredSize(new Dimension(60, 25));
        button = new JButton("foo");
        button.setName("button");
        JPanel southeast = new JPanel(new BorderLayout());
        southeast.add(new JPanel(), BorderLayout.CENTER);
        southeast.add(button, BorderLayout.SOUTH);
        JPanel north = new JPanel(new BorderLayout());
        north.add(new JPanel(), BorderLayout.CENTER);
        north.add(treeScrollPane, BorderLayout.WEST);
        north.add(textField, BorderLayout.EAST);
        JPanel south = new JPanel(new BorderLayout());
        south.add(new JPanel(), BorderLayout.CENTER);
        south.add(comboBox, BorderLayout.WEST);
        south.add(southeast, BorderLayout.EAST);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(800, 1200));
        panel.add(new JPanel(), BorderLayout.CENTER);
        panel.add(north, BorderLayout.NORTH);
        panel.add(south, BorderLayout.SOUTH);
        scrollPane = new JScrollPane(panel);
        scrollPane.setName("scrollpane");

        step = new ScrollToVisibleStep();
        step.setTimeout(1);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    public void test_scrollOK() throws Exception {
        showFrame();
        TestContext context = new TestContext(this);
        step.setScrollPane("scrollpane");
        step.setName("textfield");

        step.proceed(context);
        assertFalse(ZERO.equals(textField.getVisibleRect()));

        step.setName("combobox");
        step.proceed(context);
        assertFalse(ZERO.equals(comboBox.getVisibleRect()));

        step.setName("button");
        step.proceed(context);
        assertFalse(ZERO.equals(button.getVisibleRect()));

        step.setName("tree");
        step.proceed(context);
        assertFalse(ZERO.equals(tree.getVisibleRect()));
    }


    public void test_scrollAndClick() throws Exception {
        showFrame();

        final String clickOkValue = "Click OK";
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                textField.setText(clickOkValue);
            }
        });

        TestContext context = new TestContext(this);
        step.setScrollPane("scrollpane");
        step.setName("button");
        step.proceed(context);
        assertFalse(ZERO.equals(button.getVisibleRect()));

        ClickStep clickStep = new ClickStep();
        clickStep.setTimeout(1);
        clickStep.setName("button");
        clickStep.proceed(context);

        step.setName("textfield");
        step.proceed(context);
        assertFalse(ZERO.equals(textField.getVisibleRect()));

        AssertValueStep assertValueStep = new AssertValueStep();
        assertValueStep.setName("textfield");
        assertValueStep.setExpected(clickOkValue);
        assertValueStep.proceed(context);
    }


    public void test_componentNotFound() throws Exception {
        showFrame();
        TestContext context = new TestContext(this);
        step.setScrollPane("scrollpane");
        step.setName("notFound");

        try {
            step.proceed(context);
            fail();
        }
        catch (GuiFindException e) {
            assertEquals("Le composant 'notFound' est introuvable.", e.getMessage());
        }
    }


    public void test_scrollPaneNotFound() throws Exception {
        showFrame();
        TestContext context = new TestContext(this);
        step.setScrollPane("notFound");
        step.setName("textfield");

        try {
            step.proceed(context);
            fail();
        }
        catch (GuiFindException e) {
            assertEquals("Le JScrollPane 'notFound' est introuvable.", e.getMessage());
        }
    }


    private void showFrame() {
        JFrame frame = new JFrame();
        frame.setContentPane(scrollPane);
        frame.setSize(300, 200);
        frame.setVisible(true);
        flushAWT();
    }
}
