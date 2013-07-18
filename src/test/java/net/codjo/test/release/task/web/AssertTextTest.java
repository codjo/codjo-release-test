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


    public void test_containerOk() throws Exception {
        assertText("blah blah <div id='parent'>bloh bloh</div> <a href='toto.html'>bluh bluh</a>", "bloh",
                   "parent", (String)null);
    }


    public void test_containerOkWithXpath() throws Exception {
        assertText("blah blah <div id='parent'>bloh bloh</div> <a href='toto.html'>bluh bluh</a>", "bloh",
                   null, "//div[@id='parent']");
    }


    public void test_containerOkWithCssClass() throws Exception {
        assertText(
              "blah blah <div class='classTwo'></div> <div id='parent' class='classOne classTwo'>bloh bloh</div> <a href='toto.html'>bluh bluh</a>",
              "bloh",
              "classTwo",
              2);
    }


    public void test_containerKo() throws Exception {
        try {
            assertText("blah blah <div id='parent'>bloh bloh</div> <a href='toto.html'>bluh bluh</a>", "blah",
                       "parent", (String)null);
            fail();
        }
        catch (Exception e) {
            assertEquals(
                  "Texte 'blah' non présent dans le container 'parent' de la page "
                  + "'http://localhost:8181/test_containerKo.html'",
                  e.getMessage());
        }
    }


    public void test_containerKoWithXpath() throws Exception {
        try {
            assertText("blah blah <div id='parent'>bloh bloh</div> <a href='toto.html'>bluh bluh</a>", "blah",
                       null, "//div[@id='parent']");
            fail();
        }
        catch (Exception e) {
            assertEquals(
                  "Texte 'blah' non présent dans le container '//div[@id='parent']' de la page "
                  + "'http://localhost:8181/test_containerKoWithXpath.html'",
                  e.getMessage());
        }
    }

    public void test_containerKoWithCssClass() throws Exception {
        try {
            assertText("blah blah <div id='parent' class='classOne'>bloh bloh</div> <a href='toto.html'>bluh bluh</a>", "blah",
                       "classOne", (Integer)null);
            fail();
        }
        catch (Exception e) {
            assertEquals(
                  "Texte 'blah' non présent dans le container 'classOne' de la page "
                  + "'http://localhost:8181/test_containerKoWithCssClass.html'",
                  e.getMessage());
        }
    }


    public void test_containerNotFound() throws Exception {
        try {
            assertText("blah blah <div id='parent'>bloh bloh</div> <a href='toto.html'>bluh bluh</a>", "blah",
                       "papa", (String)null);
            fail();
        }
        catch (Exception e) {
            assertEquals(
                  "Container 'papa' non présent dans la page 'http://localhost:8181/test_containerNotFound.html'",
                  e.getMessage());
        }
    }

    public void test_containerNotFoundWithCssClass() throws Exception {
        try {
            assertText("blah blah <div id='parent'>bloh bloh</div> <a href='toto.html'>bluh bluh</a>", "blah",
                       "classDoesntExist", (String)null);
            fail();
        }
        catch (Exception e) {
            assertEquals(
                  "Container 'classDoesntExist' non présent dans la page 'http://localhost:8181/test_containerNotFoundWithCssClass.html'",
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
            assertText("blah", "unknown", null, (String)null);
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
        runStep(actual, null, (String)null, expected, null);
    }


    private void assertText(String actual, String expected, String containerId, String containerXpath)
          throws Exception {
        runStep(actual, containerId, containerXpath, expected, null);
    }


    private void assertText(String actual, String expected, String cssClass, Integer index) throws Exception {
        runStep(actual, cssClass, index, expected, null);
    }


    private void assertTextNotFound(String actual, String expected, String containerId) throws Exception {
        runStep(actual, containerId, (String)null, expected, false);
    }


    private void runStep(String actual, String containerId, String containerXpath, String expected, Boolean present)
          throws Exception {
        WebContext context = loadPage(wrapHtml(actual));

        AssertText step = new AssertText();
        step.setContainerId(containerId);
        step.setContainerXpath(containerXpath);
        step.addText(expected);
        step.setPresent(present);
        step.proceed(context);
    }


    private void runStep(String actual,
                         String containerCssClass,
                         Integer containerIndex,
                         String expected,
                         Boolean present)
          throws Exception {
        WebContext context = loadPage(wrapHtml(actual));

        AssertText step = new AssertText();
        step.addText(expected);
        step.setContainerCssClass(containerCssClass);
        step.setContainerIndex(containerIndex);
        step.setPresent(present);
        step.proceed(context);
    }
}
