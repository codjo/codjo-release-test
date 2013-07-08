package net.codjo.test.release.task.web;
import java.io.IOException;
import net.codjo.util.file.FileUtil;

import static net.codjo.test.common.matcher.JUnitMatchers.*;

public class DragAndDropTest extends WebStepTestCase {

    public void test_noParameters() throws Exception {
        try {
            DragAndDrop step = new DragAndDrop();
            step.proceed(loadPage(""));
            fail();
        }
        catch (WebException e) {
            assertThat(e.getMessage(), is("Le champ 'cssClass', 'id' ou 'xpath' doit être spécifié"));
        }
    }


    public void test_destinationNotSpecified() throws Exception {
        try {
            DragAndDrop step = new DragAndDrop();
            step.addOrigin(new DragAndDropArg("firstBox", null, null, 0));
            step.addDestination(null);
            step.proceed(loadPage(""));
            fail();
        }
        catch (WebException e) {
            assertThat(e.getMessage(), is("Aucun élément trouvé avec la clé: firstBox"));
        }
    }


    public void test_originNotSpecified() throws Exception {
        try {
            DragAndDrop step = new DragAndDrop();
            step.addOrigin(null);
            step.addDestination(new DragAndDropArg(null, "destinationXpath", null, 0));

            step.proceed(loadPage(""));
            fail();
        }
        catch (WebException e) {
            assertThat(e.getMessage(), is("Le champ 'cssClass', 'id' ou 'xpath' doit être spécifié"));
        }
    }


    public void test_dragAndDropWithId() throws Exception {
        addPage("dragAndDropImplementation.js", getDragAndSropJavascript());
        addPage("dragAndDrop.html", wrapHtml("Target title", "Target content"));
        WebContext context = loadPage(wrapHtml(buildPage()));

        dragAndDrop("originBox", "destinationBox", context);

        assertEquals("originBox has moved: true", context.getAlerts().get(0));
    }


    public void test_dragAndDropWithMixedIdAndXpath() throws Exception {
        addPage("dragAndDropImplementation.js", getDragAndSropJavascript());
        addPage("dragAndDrop.html", wrapHtml("Target title", "Target content"));
        WebContext context = loadPage(wrapHtml(buildPage()));

        dragAndDropWithIdAndXpath("originBox", "//div[2]", context);

        assertEquals("originBox has moved: true", context.getAlerts().get(0));
    }


    public void test_dragAndDropWithXpath() throws Exception {
        addPage("dragAndDrop.html", wrapHtml("Target title", "Target content"));
        addPage("dragAndDropImplementation.js", getDragAndSropJavascript());
        WebContext context = loadPage(wrapHtml(buildPage()));

        dragAndDropWithXpath("(//div)[2]", null, "(//div)[3]", null, context);

        assertEquals("originBox has moved: true", context.getAlerts().get(0));
    }


    public void test_dragAndDropWithCssClassAndIndex() throws Exception {
        addPage("dragAndDrop.html", wrapHtml("Target title", "Target content"));
        addPage("dragAndDropImplementation.js", getDragAndSropJavascript());
        WebContext context = loadPage(wrapHtml(buildPage()));

        dragAndDropWithCss("originBoxClass", 2, "destinationBoxClass", 1, context);

        assertEquals("originBox has moved: true", context.getAlerts().get(0));
    }


    private String buildPage() throws IOException {
        return
              "<!-- la boîte -->\n"
              + "<div id=\"emptyBox\" class='originBoxClass'>\n"
              + "<div id=\"originBox\" class='originBoxClass'>\n"
              + "    <h5 id=\"handle\">originBox</h5> <!-- la poignée -->\n"
              + "    <p>cette boîte-ci doit être saisie par son titre</p>\n"
              + "</div>\n"
              + "\n"
              + "\n"
              + "<div id=\"destinationBox\" class=\"destinationBoxClass\">\n"
              + "    <h5 id=\"handle\">originBox</h5> <!-- la poignée -->\n"
              + "    <p>cette boîte-ci doit être saisie par son titre</p>\n"
              + "</div>\n"
              + "\n"
              + "\n"
              + "<!-- on charge le script \"dragOn\" -->\n"
              + "<script src=\"/dragAndDropImplementation.js\" type=\"text/javascript\"></script>\n"
              + "\n"
              + "<script type=\"text/javascript\">\n"
              + "    var draggable = document.getElementById('originBox');\n"
              + "    originBoxPosition =  getPos(draggable).r;"
              + "\n"
              + "    var options = {};\n"
              + "    dragOn.apply(draggable, options);\n"
              + "\n"
              + "    addEvent(document,'mouseup', \n"
              + "     function() {\n"
              + "         var dragged = document.getElementById('originBox');\n"
              + "         alert('originBox has moved: ' + ((originBoxPosition - getPos(dragged).r)!=0));\n"
              + "     }\n"
              + ");\n"
              + "</script>"
              + "</div>\n";
    }


    private String getDragAndSropJavascript() throws IOException {
        return FileUtil.loadContent(getClass().getResource(
              "/net/codjo/test/release/javascript/dragAndDropImplementation.js"));
    }


    private void dragAndDrop(String originId, String destinationId, WebContext context) throws IOException {
        DragAndDrop step = new DragAndDrop();
        step.addOrigin(new DragAndDropArg(originId, null, null, null));
        step.addDestination(new DragAndDropArg(destinationId, null, null, null));
        step.proceed(context);
    }


    private void dragAndDropWithIdAndXpath(String originId,
                                           String destinationXpath,
                                           WebContext context) throws IOException {
        DragAndDrop step = new DragAndDrop();
        step.addOrigin(new DragAndDropArg(originId, null, null, null));
        step.addDestination(new DragAndDropArg(null, destinationXpath, null, null));
        step.proceed(context);
    }


    private void dragAndDropWithXpath(String originId,
                                      Integer originIndex,
                                      String destinationId,
                                      Integer destinationIndex,
                                      WebContext context) throws IOException {
        DragAndDrop step = new DragAndDrop();
        step.addOrigin(new DragAndDropArg(null, originId, null, originIndex));
        step.addDestination(new DragAndDropArg(null, destinationId, null, destinationIndex));
        step.proceed(context);
    }


    private void dragAndDropWithCss(String originCssClass,
                                    Integer originIndex,
                                    String destinationCssClass,
                                    Integer destinationIndex,
                                    WebContext context) throws IOException {
        DragAndDrop step = new DragAndDrop();
        step.addOrigin(new DragAndDropArg(null, null, originCssClass, originIndex));
        step.addDestination(new DragAndDropArg(null, null, destinationCssClass, destinationIndex));
        step.proceed(context);
    }
}
