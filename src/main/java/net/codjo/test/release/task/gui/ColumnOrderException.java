package net.codjo.test.release.task.gui;
/**
 * Exception générée par un AssertTableExcel quand l'ordre des colonnes testées ne correspond pas à l'étalon.
 */
public class ColumnOrderException extends GuiException {
    public ColumnOrderException(String message) {
        super(message);
    }
}
