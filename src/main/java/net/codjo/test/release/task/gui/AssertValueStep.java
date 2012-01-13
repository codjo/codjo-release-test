/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import chrriis.dj.nativeswing.swtimpl.components.JHTMLEditor;
import java.awt.Component;
import java.io.IOException;
import java.io.StringReader;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.ListCellRenderer;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;
import junit.extensions.jfcunit.finder.DialogFinder;
import junit.extensions.jfcunit.finder.Finder;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 * Classe permettant de vérifier la valeur d'un {@link javax.swing.JComponent}.
 */
public class AssertValueStep extends AbstractMatchingStep {
    private String name;
    private String mode;
    private String dialogTitle;


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getMode() {
        return mode;
    }


    public void setMode(String mode) {
        this.mode = mode;
    }


    public String getDialogTitle() {
        return dialogTitle;
    }


    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }


    @Override
    protected void proceedOnce(TestContext context) {
        expected = context.replaceProperties(expected);
        if (getMode() == null) {
            setMode(AUTO_MODE);
        }

        Finder finder;
        if (dialogTitle == null) {
            finder = new NamedComponentFinder(JComponent.class, name);
        }
        else {
            finder = new DialogFinder(getDialogTitle());
        }

        Component component = findOnlyOne(finder, context);
        if (component == null) {
            throw new GuiFindException("Le composant '" + getComponentName() + "' est introuvable.");
        }

        if (component instanceof JEditorPane) {
            proceed((JEditorPane)component);
        }
        else if (component instanceof JTextComponent) {
            proceed((JTextComponent)component);
        }
        else if (component instanceof JComboBox) {
            proceed((JComboBox)component);
        }
        else if (component instanceof JButton) {
            proceed((JButton)component);
        }
        else if (component instanceof AbstractButton) {
            proceed((AbstractButton)component);
        }
        else if (component instanceof JDialog) {
            proceed((JDialog)component);
        }
        else if (component instanceof JLabel) {
            proceed((JLabel)component);
        }
        else if (component instanceof JTabbedPane) {
            proceed((JTabbedPane)component);
        }
        else if (component instanceof JSpinner) {
            proceed((JSpinner)component);
        }
        else if (component instanceof JSlider) {
            proceed((JSlider)component);
        }
        else if (component instanceof JHTMLEditor) {
            proceed(context, (JHTMLEditor)component);
        }
        else {
            throw new GuiConfigurationException("Type de composant non supporté : "
                                                + component.getClass().getName());
        }
    }


    private void proceed(TestContext context, final JHTMLEditor htmlEditor) {
        final String[] actualValue = new String[1];

        try {
            runAwtCode(context, new Thread() {
                public void run() {
                    actualValue[0] = String.valueOf(htmlEditor.getHTMLContent());
                }
            });
        }
        catch (Exception e) {
            throw new GuiAssertException("Impossible de récuperer le contenu html du htmlEditor", e);
        }
        assertExpected(actualValue[0]);
    }


    private void proceed(JSlider slider) {
        String actualValue = String.valueOf(slider.getValue());
        assertExpected(actualValue);
    }


    private void proceed(JSpinner spinner) {
        String actualValue = String.valueOf(spinner.getValue());
        assertExpected(actualValue);
    }


    private void proceed(JTabbedPane tabbedPane) {
        int selectedIndex = tabbedPane.getSelectedIndex();
        String title = tabbedPane.getTitleAt(selectedIndex);
        assertExpected(title);
    }


    private void proceed(JEditorPane editorPane) {
        String actualValue = editorPane.getText();

        if ("text/html".equals(editorPane.getContentType())) {
            if (AUTO_MODE.equals(getMode()) || DISPLAY_MODE.equals(getMode())) {
                actualValue = removeHtmlTags(actualValue);
            }
            else if (MODEL_MODE.equals(getMode())) {
                actualValue = extractHtmlBody(actualValue);
            }

            actualValue = removeLastNewLine(actualValue);
        }

        assertExpected(actualValue);
    }


    private void proceed(JTextComponent textComponent) {
        String actualValue = textComponent.getText();
        assertExpected(actualValue);
    }


    private void proceed(JLabel label) {
        String actualValue = label.getText();
        assertExpected(actualValue);
    }


    private void proceed(AbstractButton radioButton) {
        String actualValue = radioButton.isSelected() ? "true" : "false";
        assertExpected(actualValue);
    }


    private void proceed(JButton button) {
        String actualValue = button.getText();
        assertExpected(actualValue);
    }


    private void proceed(JComboBox comboBox) {
        if (!AUTO_MODE.equals(getMode()) && !DISPLAY_MODE.equals(getMode()) && !MODEL_MODE
              .equals(getMode())) {
            throw new GuiAssertException("Invalid value of 'mode' attribute : must be in {\"" + AUTO_MODE
                                         + "\", \"" + DISPLAY_MODE + "\", \"" + MODEL_MODE + "\"}.");
        }

        String actualValue = "";
        if (comboBox.getSelectedItem() != null) {
            final ListCellRenderer renderer = comboBox.getRenderer();
            if (renderer != null) {
                final Component rendererComponent =
                      renderer.getListCellRendererComponent(new JList(), comboBox.getSelectedItem(),
                                                            comboBox.getSelectedIndex(), false, false);
                if (rendererComponent instanceof JLabel) {
                    actualValue = ((JLabel)rendererComponent).getText();
                }
                else {
                    throw new GuiAssertException("Unexpected renderer type for ComboBox");
                }
            }

            if (!expected.equals(actualValue)
                && !DISPLAY_MODE.equals(getMode())
                || MODEL_MODE.equals(getMode())
                || "".equals(actualValue)) {
                actualValue = comboBox.getSelectedItem().toString();
            }
        }

        assertExpected(actualValue);
    }


    private void proceed(JDialog dialog) {
        Component[] components = dialog.getContentPane().getComponents();
        String message = null;
        for (Component component : components) {
            if (component instanceof JOptionPane) {
                message = (String)((JOptionPane)component).getMessage();
                break;
            }
        }
        assertExpected(message);
    }


    @Override
    protected String getComponentName() {
        return "'" + (getDialogTitle() != null ? getDialogTitle() : getName()) + "'";
    }


    private String removeHtmlTags(String text) {
        StringBuilder textWithoutTags = new StringBuilder();
        parseHtmlText(text, new RemoveHtmlTagsParserCallback(textWithoutTags));
        return textWithoutTags.toString();
    }


    private String extractHtmlBody(String text) {
        StringBuilder htmlBody = new StringBuilder();
        parseHtmlText(text, new ExtractHtmlBodyParserCallback(htmlBody));
        return htmlBody.toString();
    }


    private void parseHtmlText(String text, ParserCallback callback) {
        try {
            new ParserDelegator().parse(new StringReader(text), callback, false);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private String removeLastNewLine(String actualValue) {
        if (actualValue.length() > 0) {
            char lastChar = actualValue.charAt(actualValue.length() - 1);
            if (lastChar == '\n') {
                actualValue = actualValue.substring(0, actualValue.length() - 1);
            }
        }
        return actualValue;
    }


    private static class RemoveHtmlTagsParserCallback extends ParserCallback {
        private final StringBuilder textWithoutTags;


        private RemoveHtmlTagsParserCallback(StringBuilder textWithoutTags) {
            this.textWithoutTags = textWithoutTags;
        }


        @Override
        public void handleText(char[] data, int pos) {
            textWithoutTags.append(data);
        }


        @Override
        public void handleSimpleTag(Tag tag, MutableAttributeSet attributeSet, int pos) {
            if (Tag.BR.equals(tag)) {
                textWithoutTags.append("\n");
            }
        }


        @Override
        public void handleEndTag(Tag tag, int pos) {
            if (Tag.P.equals(tag)) {
                textWithoutTags.append("\n");
            }
        }
    }

    private static class ExtractHtmlBodyParserCallback extends ParserCallback {
        private final StringBuilder htmlBody;
        private boolean insideBodyTag = false;


        private ExtractHtmlBodyParserCallback(StringBuilder htmlBody) {
            this.htmlBody = htmlBody;
        }


        @Override
        public void handleText(char[] data, int pos) {
            if (insideBodyTag) {
                htmlBody.append(data);
            }
        }


        @Override
        public void handleStartTag(Tag tag, MutableAttributeSet attributeSet, int pos) {
            if (Tag.BODY.equals(tag)) {
                insideBodyTag = true;
            }
            else if (insideBodyTag) {
                htmlBody.append("<").append(tag).append(">");
            }
        }


        @Override
        public void handleEndTag(Tag tag, int pos) {
            if (Tag.BODY.equals(tag)) {
                insideBodyTag = false;
            }
            else if (insideBodyTag) {
                htmlBody.append("</").append(tag).append(">");
            }
        }
    }
}
