package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
/**
 *
 */
public class AssertTable implements WebStep {
    private String content = "";
    private String excludedColumns;
    private String excludedRows;
    private String id;
    private int position = -1;


    public void proceed(WebContext context) {
        HtmlTable table;
        if (id != null) {
            table = (HtmlTable)context.getHtmlPage().getHtmlElementById(id);
        }
        else {
            table = (HtmlTable)context.getHtmlPage().getByXPath("//table").get(position);
        }
        assertEquals(toArray(content), toArray(table, excludedColumns, excludedRows));
    }


    static List<List<String>> toArray(String description) {
        List<List<String>> rows = new ArrayList<List<String>>();
        for (String line : description.trim().split("\n")) {
            if (line.trim().length() == 0) {
                continue;
            }
            List<String> row = new ArrayList<String>();
            String inside = line.substring(line.indexOf("|") + 1, line.lastIndexOf("|"));
            for (String cell : inside.split("\\|")) {
                row.add(cell.trim());
            }
            rows.add(row);
        }
        return rows;
    }


    private static List<Integer> getColumnIndexes(String description) {
        List<Integer> result = new ArrayList<Integer>();
        if (description != null) {
            for (String item : description.split(",")) {
                result.add(Integer.parseInt(item.trim()));
            }
        }
        return result;
    }


    private List<List<String>> toArray(HtmlTable table, String someExcludedColumns, String someExcludedRows) {
        List<Integer> excludedColumnIndexes = getColumnIndexes(someExcludedColumns);
        List<Integer> excludedRowIndexes = getColumnIndexes(someExcludedRows);

        try {
            List<List<String>> rows = new ArrayList<List<String>>();
            List htmlRows = table.getHtmlElementsByTagName("tr");
            for (int indexRow = 0; indexRow < htmlRows.size(); indexRow++) {
                if (!excludedRowIndexes.contains(indexRow)) {
                    List<String> row = new ArrayList<String>();
                    HtmlTableRow htmlTableRow = (HtmlTableRow)htmlRows.get(indexRow);
                    List cells = htmlTableRow.getByXPath(
                          htmlTableRow.getCanonicalXPath() + "//th | " + htmlTableRow.getCanonicalXPath() + "//td");
                    for (int indexCell = 0; indexCell < cells.size(); indexCell++) {
                        if (!excludedColumnIndexes.contains(indexCell)) {
                            row.add(((HtmlTableCell)cells.get(indexCell)).asText().trim());
                        }
                    }
                    rows.add(row);
                }
            }
            return rows;
        }
        catch (Exception e) {
            throw new WebException("Impossible de déterminer les cellules de la table '" + id + "'", e);
        }
    }


    public void addText(String msg) {
        this.content += msg;
    }


    private void assertEquals(List<List<String>> expected, List<List<String>> actual) {
        if (expected.size() != actual.size()) {
            throw new WebException(
                  "Nombre de lignes incorrect pour la table '" + id + "' : attendu : " + expected.size() +
                  ", obtenu : " + actual.size());
        }
        for (int i = 0; i < expected.size(); i++) {
            assertEquals("Erreur table '" + id + "'ligne " + i + ":", expected.get(i), actual.get(i));
        }
    }


    private void assertEquals(String message, List<String> expected, List<String> actual) {
        String prefix = message != null ? message + "\n" : "";
        Assert.assertEquals(prefix, expected, actual);
    }


    public void setExcludedColumns(String excludedColumns) {
        this.excludedColumns = excludedColumns;
    }


    public void setExcludedRows(String excludedRows) {
        this.excludedRows = excludedRows;
    }


    public void setId(String id) {
        this.id = id;
    }


    public void setPosition(int position) {
        this.position = position;
    }
}