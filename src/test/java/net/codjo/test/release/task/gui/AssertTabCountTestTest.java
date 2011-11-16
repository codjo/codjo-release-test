package net.codjo.test.release.task.gui;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;

public class AssertTabCountTestTest extends JFCTestCase {
    private AssertTabCountStep tabCountStep;


    @Override
    public void setUp() {
        tabCountStep = new AssertTabCountStep();
        tabCountStep.setTimeout(1);
        tabCountStep.setDelay(5);
        tabCountStep.setWaitingNumber(10);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    private void buildFrame() {

        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setName("MyTabbedPane");
        tabbedPane.add("Onglet0", new JPanel());
        tabbedPane.add("Onglet1", new JPanel());
        tabbedPane.add("Onglet2", new JPanel());

        panel.add(tabbedPane);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }


    public void testAssertTabCountOK() {
        buildFrame();

        tabCountStep.setName("MyTabbedPane");
        tabCountStep.setTabCount(3);
        tabCountStep.proceedOnce(new TestContext(this));
    }

    public void testAssertTabCountKO() {
        buildFrame();

        tabCountStep.setName("MyTabbedPane");
        tabCountStep.setTabCount(2);
               try {
            tabCountStep.proceedOnce(new TestContext(this));
            fail();
        }
        catch (GuiFindException e) {
            assertEquals("Le conteneur d'onglets MyTabbedPane contient 3 onglet(s) au lieu de 2", e.getMessage());
        }
    }

    public void testAssertByNameKO() {
        buildFrame();

        tabCountStep.setName("TabbedPaneBidon");
        tabCountStep.setTabCount(3);

        try {
            tabCountStep.proceedOnce(new TestContext(this));
            fail();
        }
        catch (GuiFindException e) {
            assertEquals(
                  "Le conteneur d'onglets (JTabbedPane) portant le nom TabbedPaneBidon est introuvable",
                  e.getMessage());
        }
    }
    
}
