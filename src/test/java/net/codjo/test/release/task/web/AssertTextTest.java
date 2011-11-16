package net.codjo.test.release.task.web;
import org.apache.tools.ant.BuildException;
/**
 *
 */
public class AssertTextTest extends WebStepTestCase {

    public void test_ok() throws Exception {
        assertText("blah blah <h1>bloh bloh</h1> <a href='toto.html'>bluh bluh</a>", "blah");
    }


    public void test_linkText() throws Exception {
        assertText("blah blah <h1>bloh bloh</h1> <a href='toto.html'>bluh bluh</a>", "bluh");
    }


    public void test_h1Text() throws Exception {
        assertText("blah blah <h1>bloh bloh</h1> <a href='toto.html'>bluh bluh</a>", "bloh");
    }


    public void test_textNotFound() throws Exception {
        assertTextNotFound("blah blah <h1>bloh bloh</h1> <a href='toto.html'>bluh bluh</a>", "not found",
                           null);
    }


    public void test_containerOK() throws Exception {
        assertText("blah blah <div id='parent'>bloh bloh</div> <a href='toto.html'>bluh bluh</a>", "bloh",
                   "parent");
    }


    public void test_containerKO() throws Exception {
        try {
            assertText("blah blah <div id='parent'>bloh bloh</div> <a href='toto.html'>bluh bluh</a>", "blah",
                       "parent");
            fail();
        }
        catch (Exception e) {
            assertEquals(
                  "Texte 'blah' non présent dans le container 'parent' de la page "
                  + "'http://localhost:8181/test_containerKO.html'",
                  e.getMessage());
        }
    }


    public void test_containerNotFound() throws Exception {
        try {
            assertText("blah blah <div id='parent'>bloh bloh</div> <a href='toto.html'>bluh bluh</a>", "blah",
                       "papa");
            fail();
        }
        catch (Exception e) {
            assertEquals(
                  "Container 'papa' non présent dans la page 'http://localhost:8181/test_containerNotFound.html'",
                  e.getMessage());
        }
    }


    public void test_textNotFoundInContainer() throws Exception {
        assertTextNotFound("blah blah <div id='parent'>bloh bloh</div> <a href='toto.html'>bluh bluh</a>",
                           "blah", "parent");
    }


    public void test_textNotFoundWithContainerError() throws Exception {
        try {
            assertTextNotFound("blah blah <div id='parent'>bloh bloh</div> <a href='toto.html'>bluh bluh</a>",
                               "bloh", "parent");
            fail();
        }
        catch (Exception e) {
            assertEquals("Texte 'bloh' présent dans le container 'parent' de la page"
                         + " 'http://localhost:8181/test_textNotFoundWithContainerError.html'",
                         e.getMessage());
        }
    }


    public void test_error() throws Exception {
        try {
            assertText("blah", "unknown", null);
            fail();
        }
        catch (BuildException e) {
            assertEquals(
                  "Texte 'unknown' non présent dans la page '' (http://localhost:8181/test_error.html)",
                  e.getMessage());
        }
    }


    public void test_replaceProperties() throws Exception {
        project.setProperty("myProp", "myValue");
        assertText("value=myValue", "value=${myProp}");
    }


    private void assertText(String actual, String expected) throws Exception {
        runStep(actual, null, expected, null);
    }


    private void assertText(String actual, String expected, String containerId) throws Exception {
        runStep(actual, containerId, expected, null);
    }


    private void assertTextNotFound(String actual, String expected, String containerId) throws Exception {
        runStep(actual, containerId, expected, false);
    }


    private void runStep(String actual, String containerId, String expected, Boolean present)
          throws Exception {
        WebContext context = loadPage(wrapHtml(actual));

        AssertText step = new AssertText();
        step.setContainerId(containerId);
        step.addText(expected);
        step.setPresent(present);
        step.proceed(context);
    }
}
