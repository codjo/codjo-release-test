/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JHTMLEditor;
import java.awt.Container;
import java.awt.Frame;
import java.awt.IllegalComponentStateException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.TestHelper;
/**
 * Classe de test de {@link net.codjo.test.release.task.gui.SetValueStep}.
 */
public class DebugSetValueStepTest extends JFCTestCase {
    private SetValueStep step;
    private JHTMLEditor htmlEditor;


    public void test_setValue_jHtmlEditor() throws Exception {
        NativeInterface.open();
        try {
            JFrame frame = new JFrame();
            addJHtmlEditorPane(frame);
            flushAWT();

            step.setName(htmlEditor.getName());
            step.setValue("Simple text with <strong>bold</strong>");
            TestContext context = new TestContext(this);
            step.proceed(context);

            final String[] htmlContent = new String[1];
            step.runAwtCode(context, new Runnable() {
                public void run() {
                    htmlContent[0] = htmlEditor.getHTMLContent();
                }
            });
            assertEquals("<p>Simple text with <strong>bold</strong></p>", htmlContent[0]);
        }
        finally {
            NativeInterface.close();
        }
    }


    private void addJHtmlEditorPane(final JFrame frame) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    Map<String, String> optionMap = new HashMap<String, String>();
                    optionMap.put("theme_advanced_buttons1",
                                  "'bold,italic,underline,|,charmap,|,justifyleft,justifycenter,justifyright,justifyfull,|,formatselect,fontselect,fontsizeselect,|,hr,removeformat'");
                    optionMap.put("theme_advanced_buttons2",
                                  "'undo,redo,|,link,unlink,anchor,image,cleanup,code,|,cut,copy,paste,pastetext,pasteword,|,search,replace,|,forecolor,backcolor,bullist,numlist,|,outdent,indent,blockquote,|,table'");
                    optionMap.put("theme_advanced_buttons3", "''");
                    optionMap.put("theme_advanced_toolbar_location", "'top'");
                    optionMap.put("theme_advanced_toolbar_align", "'left'");

                    optionMap.put("plugins", "'table,paste,contextmenu'");
                    htmlEditor = new JHTMLEditor(JHTMLEditor.HTMLEditorImplementation.TinyMCE,
                                                 JHTMLEditor.TinyMCEOptions.setOptions(optionMap)
                    );
                    htmlEditor.setName("jHtmlEditor");
                    frame.getContentPane().add(htmlEditor);
                    frame.pack();
                    frame.setVisible(true);

                }
            });
        }
        catch (Exception e) {
            throw new IllegalComponentStateException("Impossible d'initialiser le composant JHtmlEditor");
        }
    }


    @Override
    protected void setUp() {
        TestHelper.setKeyMapping(new FrenchKeyMapping());
        step = new SetValueStep();
        step.setTimeout(1);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestHelper.cleanUp(this);
    }
}
