/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
/**
 *
 */
public class AssertEditableStep extends AbstractAssertStep {
    private String name;
    private String expected;
    private String row = "-1";
    private String column = "-1";


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getExpected() {
        return expected;
    }


    public void setExpected(String expected) {
        this.expected = expected;
    }


    @Override
    protected void proceedOnce(TestContext context) {
        NamedComponentFinder finder = new NamedComponentFinder(JComponent.class, name);
        Component component = findOnlyOne(finder, context, 0);
        if (component == null) {
            throw new GuiFindException("Le composant '" + getName() + "' est introuvable.");
        }
        if (component instanceof JTable) {
            proceed((JTable)component);
        }
        else {
            try {
                Method method = component.getClass().getMethod("isEditable");
                if (method != null) {
                    assertExpected(method.invoke(component).toString());
                }
            }
            catch (NoSuchMethodException e) {
                if (component instanceof JCheckBox) {

                    proceed((JCheckBox)component);
                }
                else {
                    proceed((JComponent)component);
                }
            }
            catch (IllegalAccessException e) {
                throw new GuiAssertException(
                      "La méthode isEditable est inaccessible sur le composant " + component.getName());
            }
            catch (InvocationTargetException e) {
                throw new GuiAssertException(
                      "Une exception est survenue lors de l'exécution de la methode 'isEditable' ");
            }
        }
    }


    private void proceed(JTable table) {
        int realRow = -1;
        try {
            realRow = Integer.parseInt(row);
        }
        catch (NumberFormatException nfe) {
            ;
        }
        if (table == null) {
            throw new GuiFindException("La table '" + getName() + "' est introuvable.");
        }
        int realColumn = TableTools.searchColumn(table, column);

        TableTools.checkTableCellExists(table, realRow, realColumn);

        boolean isEditable = table.isCellEditable(realRow, realColumn);

        assertExpected(String.valueOf(isEditable));
    }


    private void proceed(JCheckBox component) {
        String actualValue;
        if (component.isEnabled()) {
            actualValue = "true";
        }
        else {
            actualValue = "false";
        }
        assertExpected(actualValue);
    }


    private void proceed(JComponent component) {
        throw new GuiAssertException(component.getName()
                                     + " : Composant non supporté par la balise assertEditable ("
                                     + component.getClass().getName()
                                     + ")");
    }


    private void assertExpected(String actualValue) {
        if (!expected.equals(actualValue)) {
            throw new GuiAssertException("Composant '" + getName() + "' : attendu='" + expected
                                         + "' obtenu='" + actualValue + "'");
        }
    }


    public void setRow(String rowIndex) {
        this.row = rowIndex;
    }


    public void setColumn(String columnNameOrIndex) {
        this.column = columnNameOrIndex;
    }
}
