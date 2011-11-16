package net.codjo.test.release.task.web;
/**
 *
 */
public class AssertTableTest extends WebStepTestCase {
    public void test_textToTable() throws Exception {
        String input =
              "\n"
              + "   | Aa | 1 |   \n"
              + "     | B b | 2  |  \n"
              + "     | C  c |3|\n";

        assertEquals("[[Aa, 1], [B b, 2], [C  c, 3]]", AssertTable.toArray(input).toString());
    }


    public void test_ok() throws Exception {
        WebContext context = loadPage(wrapHtml(
              "<table id='tbl'>"
              + "<tr>"
              + "<th>6</th><th>28</th><th>496</th>"
              + "</tr>"
              + "<tr>"
              + "<td>1.618</td><td>2.718</td><td>1.414</td>"
              + "</tr>"
              + "</table>"));

        String expected = "|6|28|496|\n|1.618|2.718|1.414|";
        runStep("tbl", expected, null, null, context);
    }


    public void test_notOk() throws Exception {
        WebContext context = loadPage(wrapHtml(
              "<table id='tbl'>"
              + "<tr>"
              + "<th>6</th><th>28</th><th>496</th>"
              + "</tr>"
              + "<tr>"
              + "<td>1.618</td><td>3.142</td><td>1.414</td>"
              + "</tr>"
              + "</table>"));

        String expected = "|6|28|496|\n|1.618|2.718|1.414|";
        try {
            runStep("tbl", expected, null, null, context);
            fail();
        }
        catch (Throwable e) {
            ;
        }
    }


    public void test_okExcludedRows() throws Exception {
        WebContext context = loadPage(wrapHtml(
              "<table id='tbl'>"
              + "<tr>"
              + "<th>6</th><th>28</th><th>496</th>"
              + "</tr>"
              + "<tr>"
              + "<td>1.618</td><td>2.718</td><td>1.414</td>"
              + "</tr>"
              + "</table>"));

        String expected = "|6|28|496|";
        runStep("tbl", expected, null, "1", context);
    }


    public void test_okExcludedCols() throws Exception {
        WebContext context = loadPage(wrapHtml(
              "<table id='tbl'>"
              + "<tr>"
              + "<th>6</th><th>28</th><th>496</th>"
              + "</tr>"
              + "<tr>"
              + "<td>1.618</td><td>2.718</td><td>1.414</td>"
              + "</tr>"
              + "</table>"));

        String expected = "|6|";
        runStep("tbl", expected, "1,2", "1", context);
    }


    public void test_() throws Exception {
        WebContext context = loadPage(wrapHtml(
                "<table class='content' id='tbl'>"
              + "  <thead>"
              + "    <tr class='header'>"
              + "      <th>"
              + "        ID"
              + "      </th><th>"
              + "        Name"
              + "      </th><th>"
              + "        Sensitive"
              + "      </th>"
              + "    </tr>"
              + "  </thead>"
              + "  <tbody>"
              + "    <form action='blah' method='post' id='waitingForAdmin_0'>"
              + "      <div style='display:none'>"
              + "        <input type='hidden' name='myDiv'"
              + "               id='tabs_panel_waitingForAdmin_table_rows_0_rowContent_form:hf:0' />"
              + "      </div>"
              + "      <tr>"
              + "        <td>"
              + "          lskywalk"
              + "        </td><td>"
              + "          Luke"
              + "        </td><td>"
              + "          <input type='checkbox' name='sensitive_0'/>"
              + "        </td>"
              + "      </tr>"
              + "    </form>"
              + "  </tbody>"
              + "</table>"));

        String expected = "|ID|Name|Sensitive|\n|lskywalk|Luke|unchecked|";
        runStep("tbl", expected, null, null, context);
    }


    public void test_tablePosition() throws Exception {
        WebContext context = loadPage(wrapHtml(
              "<div>"
              + "<table><tr><td>09</td></tr></table>"
              + "<div><table><tr><td>10</td></tr></table></div>"
              + "</div>"));
        runStep(0, "|09|", context);
        runStep(1, "|10|", context);
    }

    private void runStep(String id,
                         String expected,
                         String excludedColumns,
                         String excludedRows,
                         WebContext context)
          throws Exception {
        AssertTable step = new AssertTable();
        step.setId(id);
        step.addText(expected);
        step.setExcludedRows(excludedRows);
        step.setExcludedColumns(excludedColumns);
        step.proceed(context);
    }

    private void runStep(int position,
                         String expected,
                         WebContext context)
          throws Exception {
        AssertTable step = new AssertTable();
        step.setPosition(position);
        step.addText(expected);
        step.proceed(context);
    }
}
