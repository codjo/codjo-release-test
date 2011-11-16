/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import net.codjo.test.release.task.gui.metainfo.Introspector;
import net.codjo.test.release.task.gui.metainfo.SetValueDescriptor;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.Dictionary;
import java.util.Enumeration;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.eventdata.KeyEventData;
import junit.extensions.jfcunit.eventdata.StringEventData;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 * Classe permettant d'affecter la valeur d'un composant
 */
public class SetValueStep extends AbstractGuiStep {
    public static final String MODE_KEYBOARD = "keyboard";
    public static final String MODE_SETTER = "setter";
    public static final String BAD_BOOLEAN_VALUE_MESSAGE =
          "Seules les valeurs 'true' et 'false' sont autorisées pour ce composant.";
    public static final String BAD_NUMBER_VALUE_MESSAGE
          = "Seules les entiers positifs sont autorisées pour ce composant.";
    public static final String CHECKBOX_DISABLED_MESSAGE = "La checkBox est grisée.";
    private String value;
    private String mode = MODE_SETTER;
    private String name;
    private int row = -1;
    private String column = "-1";


    public void proceed(TestContext context) {
        value = context.replaceProperties(value);
        NamedComponentFinder finder = new NamedComponentFinder(JComponent.class, name);
        final Component component = findOnlyOne(finder, context, getTimeout());
        if (component == null) {
            throw new GuiFindException("Le composant '" + getName() + "' est introuvable.");
        }

        if (!component.isEnabled()) {
            throw new GuiConfigurationException(computeUneditableComponent(name));
        }

        try {
            final SetValueDescriptor descriptor =
                  Introspector.getTestBehavior(component.getClass(), SetValueDescriptor.class);
            if (descriptor != null) {
                runAwtCode(context,
                           new Runnable() {
                               public void run() {
                                   descriptor.setValue(component, SetValueStep.this);
                               }
                           });
            }
            else {
                if (component instanceof JTextComponent) {
                    proceed(context, (JTextComponent)component);
                }
                else if (component instanceof JComboBox) {
                    proceed(context, (JComboBox)component);
                }
                else if (component instanceof JCheckBox) {
                    proceed(context, (JCheckBox)component);
                }
                else if (component instanceof JTable) {
                    proceed(context, (JTable)component);
                }
                else if (component instanceof JSpinner) {
                    proceed(context, (JSpinner)component);
                }
                else if (component instanceof JSlider) {
                    proceed(context, (JSlider)component);
                }
                else {
                    throw new GuiConfigurationException("Type de composant non supporté : "
                                                        + component.getClass().getName());
                }
            }
        }
        catch (GuiException e) {
            throw e;
        }
        catch (Exception e) {
            throw new GuiActionException("Impossible de fixer la valeur.", e);
        }
    }


    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }


    public String getMode() {
        return mode;
    }


    public void setMode(String mode) {
        this.mode = mode;
    }


    public int getRow() {
        return row;
    }


    public void setRow(int row) {
        this.row = row;
    }


    public String getColumn() {
        return column;
    }


    public void setColumn(String column) {
        this.column = column;
    }


    private void proceed(TestContext context, final JSpinner jSpinner) throws Exception {
        if (!jSpinner.isEnabled()) {
            return;
        }

        runAwtCode(context,
                   new Runnable() {
                       public void run() {
                           jSpinner.requestFocus();
                       }
                   });

        runAwtCode(context,
                   new Runnable() {
                       public void run() {
                           jSpinner.setValue(value);
                       }
                   });
    }


    private void proceed(TestContext context, final JSlider jSlider) throws Exception {
        if (!jSlider.isEnabled()) {
            return;
        }

        runAwtCode(context,
                   new Runnable() {
                       public void run() {
                           jSlider.requestFocus();
                       }
                   });

        runAwtCode(context,
                   new Runnable() {
                       public void run() {
                           Dictionary dictionary = jSlider.getLabelTable();
                           Enumeration enumeration = dictionary.keys();
                           while (enumeration.hasMoreElements()) {
                               Object key = enumeration.nextElement();
                               if (key instanceof Integer
                                   && dictionary.get(key) instanceof JLabel
                                   && ((JLabel)dictionary.get(key)).getText().equals(value)) {
                                   jSlider.setValue(Integer.valueOf(key.toString()));
                               }
                           }
                       }
                   });
    }


    private void proceed(TestContext context, JTextComponent textComponent) throws Exception {
        if (MODE_KEYBOARD.equals(mode)) {
            proceedModeKeyboard(context, textComponent);
        }
        else if (MODE_SETTER.equals(mode)) {
            proceedModeSetter(context, textComponent);
        }
        else {
            throw new GuiConfigurationException("Mode invalide : " + mode + ". Les modes valides sont "
                                                + MODE_KEYBOARD + " et " + MODE_SETTER + ".");
        }
    }


    private void proceedModeSetter(final TestContext context, final JTextComponent textComponent)
          throws Exception {
        if (!textComponent.isEnabled()) {
            return;
        }

        runAwtCode(context,
                   new Runnable() {
                       public void run() {
                           textComponent.requestFocus();
                       }
                   });
        runAwtCode(context,
                   new Runnable() {
                       public void run() {
                           textComponent.setText(value);
                       }
                   });
    }


    private void proceedModeKeyboard(TestContext context, final JComponent textComponent)
          throws Exception {
        JFCTestCase testCase = context.getTestCase();

        // Se positionner sur le composant
        runAwtCode(context,
                   new Runnable() {
                       public void run() {
                           textComponent.requestFocus();
                       }
                   });

        // Sélectionner tout l'ancien texte
        KeyEventData keyEventData = new KeyEventData(testCase, textComponent, KeyEvent.VK_A);
        keyEventData.setModifiers(KeyEvent.CTRL_MASK);
        testCase.getHelper().sendKeyAction(keyEventData);

        // Ecraser l'ancien texte avec le nouveau
        if (value == null || value.length() == 0) {
            keyEventData = new KeyEventData(testCase, textComponent, KeyEvent.VK_BACK_SPACE);
            testCase.getHelper().sendKeyAction(keyEventData);
        }
        else {
            testCase.getHelper().sendString(new StringEventData(testCase, textComponent, value));
        }
    }


    private void proceed(TestContext context, final JComboBox comboBox)
          throws Exception {
        checkComponentExistence(comboBox);
        runAwtCode(context,
                   new Runnable() {
                       public void run() {
                           comboBox.setSelectedItem(value);
                           if (!comboBox.getSelectedItem().equals(value)) {
                               selectItem(comboBox);
                           }
                       }
                   });

        if (MODE_KEYBOARD.equals(mode) && comboBox.isEditable()) {
            proceedModeKeyboard(context, comboBox);
        }
    }


    private void selectItem(JComboBox comboBox) {
        for (int index = 0; index < comboBox.getItemCount(); index++) {
            if (comboBox.getItemAt(index).toString().equals(value)) {
                comboBox.setSelectedIndex(index);
            }
        }
    }


    private void checkComponentExistence(final JComboBox comboBox) {
        if (comboBox.isEditable()) {
            return;
        }
        boolean exists = false;
        for (int index = 0; index < comboBox.getItemCount(); index++) {
            if (comboBox.getItemAt(index).toString().equals(value)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            throw new GuiFindException("L'élément '" + value + "' n'existe pas dans le composant " + name
                                       + ".");
        }
    }


    private void proceed(TestContext context, final JCheckBox checkbox)
          throws Exception {
        if ("true".equalsIgnoreCase(value)) {
            selectCheckBox(context, checkbox);
        }
        else if ("false".equalsIgnoreCase(value)) {
            unselectCheckBox(context, checkbox);
        }
        else {
            throw new GuiConfigurationException(BAD_BOOLEAN_VALUE_MESSAGE);
        }
    }


    private void selectCheckBox(TestContext context, final JCheckBox checkBox)
          throws Exception {
        if (!checkBox.isSelected()) {
            clickOnCheckBox(context, checkBox);
        }
    }


    private void unselectCheckBox(TestContext context, JCheckBox checkBox)
          throws Exception {
        if (checkBox.isSelected()) {
            clickOnCheckBox(context, checkBox);
        }
    }


    private void clickOnCheckBox(TestContext context, final JCheckBox checkBox)
          throws Exception {
        runAwtCode(context,
                   new Runnable() {
                       public void run() {
                           checkBox.doClick();
                       }
                   });
    }


    private void proceed(TestContext context, final JTable table) throws Exception {
        final int realColumn = TableTools.searchColumn(table, column);
        TableTools.checkTableCellExists(table, row, realColumn);
        runAwtCode(context,
                   new Runnable() {
                       public void run() {
                           table.setValueAt(value, row, realColumn);
                       }
                   });
    }


    static String computeUneditableComponent(String componentName) {
        return "Le composant '" + componentName + "' n'est pas éditable.";
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }
}
