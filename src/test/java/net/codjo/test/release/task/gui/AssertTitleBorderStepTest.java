package net.codjo.test.release.task.gui;
import net.codjo.test.release.task.gui.matcher.MatcherFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
/**
 *
 */
public class AssertTitleBorderStepTest extends JFCTestCase {
    private AssertTitleBorderStep step;
    private static final String BORDER_TITLE = "My Title Border";
    private static final String PANEL_TITLE_NAME = "titlePanel";
    private static final String PANEL_NO_BORDER_NAME = "noBorderPanel";
    private static final String PANEL_ANOTHER_BORDER_NAME = "anotherBorderPanel";


    @Override
    protected void setUp() throws Exception {
        step = new AssertTitleBorderStep();
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
        step.setName(PANEL_TITLE_NAME);
        step.setExpected(BORDER_TITLE);
        step.proceed(context);
    }


    public void test_assertOKWithMatching() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName(PANEL_TITLE_NAME);
        step.setMatching(MatcherFactory.CONTAINS_MATCHING);
        step.setExpected("Title");

        step.proceed(context);
    }


    public void test_assertKOBadTitle() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName(PANEL_TITLE_NAME);
        step.setExpected("bad expected title");
        try {
            step.proceed(context);
            fail("Pas d'erreur alors que l'expected ne correspond pas à l'actual !");
        }
        catch (Exception e) {
            // Cas normal
        }
    }


    public void test_assertKOBadBorder() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName(PANEL_ANOTHER_BORDER_NAME);
        step.setExpected("bad border");
        try {
            step.proceed(context);
            fail("Pas d'erreur alors que la bordure n'est pas une TitleBorder !");
        }
        catch (Exception e) {
            // Cas normal
        }
    }


    public void test_assertKONoBorder() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName(PANEL_NO_BORDER_NAME);
        step.setExpected("no border");
        try {
            step.proceed(context);
            fail("Pas d'erreur alors que le composant n'a pas de bordure !");
        }
        catch (Exception e) {
            // Cas normal
        }
    }


    private void showFrame() {
        JFrame frame = new JFrame();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createTitleBorderPanel(), BorderLayout.CENTER);
        mainPanel.add(createNoBorderPanel(), BorderLayout.SOUTH);
        mainPanel.add(createAnotherBorderPanel(), BorderLayout.NORTH);

        frame.setContentPane(mainPanel);
        frame.setSize(300, 200);
        frame.setVisible(true);
        flushAWT();
    }


    private JPanel createTitleBorderPanel() {
        JPanel titlePanel = createNoBorderPanel();
        titlePanel.setName(PANEL_TITLE_NAME);
        titlePanel.setBorder(new TitledBorder(BORDER_TITLE));
        return titlePanel;
    }


    private JPanel createNoBorderPanel() {
        JPanel panel = new JPanel();
        panel.setName(PANEL_NO_BORDER_NAME);
        return panel;
    }


    private JPanel createAnotherBorderPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new LineBorder(Color.BLACK, 1));
        panel.setName(PANEL_ANOTHER_BORDER_NAME);
        return panel;
    }
}
