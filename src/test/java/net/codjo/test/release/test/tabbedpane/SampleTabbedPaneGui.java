package net.codjo.test.release.test.tabbedpane;
import net.codjo.test.release.test.AbstractSampleGui;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class SampleTabbedPaneGui extends AbstractSampleGui {

    protected SampleTabbedPaneGui(String title) {
        super(title);
    }


    @Override
    protected void buildGui() {
        JPanel mainPanel = new JPanel();
        mainPanel.setName("mainPanel");
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("tab0", new JPanel());
        tabbedPane.addTab("tab1", new JPanel());
        tabbedPane.addTab("tab2", new JPanel());
        tabbedPane.addTab("tab3", new JPanel());
        tabbedPane.setEnabledAt(3, false);
        tabbedPane.setName("tabbedPane");
        mainPanel.add(tabbedPane);
        this.add(mainPanel);
    }


    public static void main(String[] args) {
        JFrame frame = new SampleTabbedPaneGui("test");
        frame.setVisible(true);
    }
}
