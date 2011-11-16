/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
/**
 * Classe de test de {@link SelectTabStep}.
 */
public class SelectTabStepTest extends JFCTestCase {
    private SelectTabStep step;
    private JTabbedPane tabbedPane;


    @Override
    protected void setUp() throws Exception {
        step = new SelectTabStep();
        step.setTimeout(1);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }


    public void test_defaults() throws Exception {
        assertNull(step.getName());
        assertNull(step.getTabLabel());
    }


    public void test_tabbPane_ok_by_index() throws Exception {
        showFrame();

        checkTabSelection(0,
                          new Functor() {
                              public void selectTab(SelectTabStep step) {
                                  step.setTabIndex(0);
                              }
                          });

        checkTabSelection(2,
                          new Functor() {
                              public void selectTab(SelectTabStep step) {
                                  step.setTabIndex(2);
                              }
                          });

        checkTabSelection(1,
                          new Functor() {
                              public void selectTab(SelectTabStep step) {
                                  step.setTabLabel("1");
                              }
                          });
    }


    public void test_tabbPane_ok_by_tabName() throws Exception {
        showFrame();

        checkTabSelection(0,
                          new Functor() {
                              public void selectTab(SelectTabStep step) {
                                  step.setTabLabel("page 1");
                              }
                          });

        checkTabSelection(2,
                          new Functor() {
                              public void selectTab(SelectTabStep step) {
                                  step.setTabLabel("page 3");
                              }
                          });
    }


    public void test_nok_unknownComponent() throws Exception {
        showFrame();

        step.setName("unknown");
        step.setTabLabel("1");

        try {
            step.proceed(new TestContext(this));
            fail("Le composant ne devrait pas être trouvé.");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
    }


    public void test_nok_badTypeComponent() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("Parasite");
        step.setTabLabel("2");

        try {
            step.proceed(context);
            fail("Type de composant non supporté.");
        }
        catch (GuiConfigurationException ex) {
            ; // Cas normal
        }
    }


    public void test_nok_tab_notFound() throws Exception {
        showFrame();

        checkTabSelectionNotFound(new Functor() {
            public void selectTab(SelectTabStep step) {
                step.setTabLabel("unknown");
            }
        });

        checkTabSelectionNotFound(new Functor() {
            public void selectTab(SelectTabStep step) {
                step.setTabLabel("12");
            }
        });

        checkTabSelectionNotFound(new Functor() {
            public void selectTab(SelectTabStep step) {
                step.setTabLabel("-1");
            }
        });

        checkTabSelectionNotFound(new Functor() {
            public void selectTab(SelectTabStep step) {
                step.setTabIndex(15);
            }
        });

        checkTabSelectionNotFound(new Functor() {
            public void selectTab(SelectTabStep step) {
                step.setTabIndex(-1);
            }
        });
    }


    public void test_nok_tab_notFound_by_index() throws Exception {
        showFrame();

        TestContext context = new TestContext(this);
        step.setName("MyJTabbedPane");
        step.setTabLabel("3");

        try {
            step.proceed(context);
            fail("TabIndex index out of range");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
    }


    public void test_usageOfTwoAttributesThrowsAnException()
          throws Exception {
        showFrame();

        step.setName("MyJTabbedPane");
        step.setTabIndex(2);
        step.setTabLabel("page 3");

        try {
            step.proceed(new TestContext(this));
            fail();
        }
        catch (GuiConfigurationException e) {
            assertEquals(SelectTabStep.computeIllegalUsageOfAttributes("MyJTabbedPane"), e.getMessage());
        }
    }


    private void showFrame() {
        JFrame frame = new JFrame();

        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        addTabbedPane(panel);
        addParasite(panel);

        frame.pack();
        frame.setVisible(true);
        flushAWT();
    }


    private void checkTabSelection(int expectedSelection, Functor functor) {
        step = new SelectTabStep();
        step.setTimeout(1);
        step.setName("MyJTabbedPane");
        functor.selectTab(step);
        step.proceed(new TestContext(this));
        assertEquals(expectedSelection, tabbedPane.getSelectedIndex());
    }


    private void checkTabSelectionNotFound(Functor functor) {
        step = new SelectTabStep();
        step.setTimeout(1);
        step.setName("MyJTabbedPane");
        functor.selectTab(step);

        try {
            step.proceed(new TestContext(this));
            fail("Le composant ne devrait pas être trouvé.");
        }
        catch (GuiFindException ex) {
            ; // Cas normal
        }
    }


    private void addTabbedPane(JPanel panel) {
        tabbedPane = new JTabbedPane();
        tabbedPane.setName("MyJTabbedPane");
        tabbedPane.addTab("page 1", new JPanel());
        tabbedPane.addTab("page 2", new JPanel());
        tabbedPane.addTab("page 3", new JPanel());
        panel.add(tabbedPane);
    }


    private void addParasite(JPanel panel) {
        JPanel parasite = new JPanel();
        parasite.setBackground(Color.yellow);
        parasite.setPreferredSize(new Dimension(40, 30));
        parasite.setName("Parasite");
        panel.add(parasite);
    }


    private interface Functor {
        void selectTab(SelectTabStep step);
    }
}
