package net.codjo.test.release.task.web.finder;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import java.util.HashMap;
import net.codjo.test.common.LogString;
import net.codjo.test.release.task.web.WebContext;
import net.codjo.test.release.task.web.WebException;
import net.codjo.test.release.task.web.WebStepTestCase;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class ComponentFinderTest extends WebStepTestCase {
    private LogString logString = new LogString();


    @Test
    public void test_findByIdOk() throws Exception {
        final String htmlToTest
              = "blah blah <div id='parent'>bloh bloh</div> <div id='parent2'>bloh bloh</div> <a href='toto.html'>bluh bluh</a>";

        final HtmlElement htmlElement = findElement(htmlToTest, "parent");

        assertThat(htmlElement.asText(), is("bloh bloh"));
        logString.assertAndClear("handleElementFound()");
    }


    @Test
    public void test_findByIdElementNotFound() throws Exception {
        final String htmlToTest
              = "blah blah <div id='parent'>bloh bloh</div> <div id='parent2'>bloh bloh</div> <a href='toto.html'>bluh bluh</a>";

        final HtmlElement htmlElement = findElement(htmlToTest, "unknownElement");

        assertThat(htmlElement, nullValue());
        logString.assertAndClear("handleElementNotFound()");
    }


    @Test
    public void test_findByXpathOk() throws Exception {
        final String htmlToTest
              = "blah blah <div id='parent'>bloh bloh</div> <div id='parent2'>bluh bluh</div> <a href='toto.html'>bluh bluh</a>";

        final HtmlElement htmlElement = findElementByXpath(htmlToTest, "//div[@id='parent2']", null);

        assertThat(htmlElement.asText(), is("bluh bluh"));
        logString.assertAndClear("handleElementFound()");
    }


    @Test
    public void test_findByXpathElementNotFound() throws Exception {
        final String htmlToTest
              = "blah blah <div id='parent'>bloh bloh</div> <div id='parent2'>bloh bloh</div> <a href='toto.html'>bluh bluh</a>";

        HtmlElement htmlElement = findElementByXpath(htmlToTest, "//table", null);

        assertThat(htmlElement, nullValue());
        logString.assertAndClear("handleElementNotFound()");
    }


    @Test
    public void test_findByXpathAmbiguousExpression() throws Exception {
        final String htmlToTest
              = "blah blah <div id='parent'>bloh bloh</div> <div id='parent2'>bloh bloh</div> <a href='toto.html'>bluh bluh</a>";

        HtmlElement htmlElement = null;
        try {
            htmlElement = findElementByXpath(htmlToTest, "//div", null);
            fail();
        }
        catch (WebException e) {
            assertThat(e.getLocalizedMessage(),
                       is("Ambiguité, plusieurs éléments ont été trouvé avec l'expression xpath:'//div'\n"
                          + "\tobject : HtmlDivision[<div id=\"parent\">]\n"
                          + "\tobject : HtmlDivision[<div id=\"parent2\">]\n"));
        }

        assertThat(htmlElement, nullValue());
        logString.assertAndClear("");
    }


    @Test
    public void test_findByXpathWithIndex() throws Exception {
        final String htmlToTest
              = "blah blah <div id='parent'>bloh bloh</div> <div id='parent2'>bloh bloh deux</div> <a href='toto.html'>bluh bluh</a>";

        HtmlElement htmlElement = findElementByXpath(htmlToTest, "//div", 2);
        assertThat(htmlElement.asText(), is("bloh bloh deux"));
        logString.assertAndClear("handleElementFound()");
    }


    @Test
    public void test_findByXpathWithIndexNotfound() throws Exception {
        final String htmlToTest
              = "blah blah <div id='parent'>bloh bloh</div><a href='toto.html'>bluh bluh</a>";

        HtmlElement htmlElement = null;
        try {
            htmlElement = findElementByXpath(htmlToTest, "//div", 8);
            fail();
        }
        catch (Exception e) {
            assertThat(e.getLocalizedMessage(),
                       is("Impossible de trouver l'index 8 dans la liste des résultats de taille 1, pour l'expression xpath: //div "));
        }

        assertThat(htmlElement, nullValue());
        logString.assertAndClear("");
    }


    @Test
    public void test_findByTextOk() throws Exception {
        final String htmlToTest
              = "blah blah <div id='parent'>bloh bloh</div> <div id='parent2'>bloh bloh</div> <a href='toto.html'>bluh bluh</a>";

        final HtmlElement htmlElement = findElementByText(htmlToTest, "bluh bluh");

        assertThat(htmlElement.asText(), is("bluh bluh"));
        logString.assertAndClear("handleElementFound()");
    }


    @Test
    public void test_findByTextElementNotFound() throws Exception {
        final String htmlToTest
              = "blah blah <div id='parent'>bloh bloh</div> <div id='parent2'>bloh bloh</div> <a href='toto.html'>bluh bluh</a>";

        final HtmlElement htmlElement = findElementByText(htmlToTest, "youWillNeverfindIt");

        assertThat(htmlElement, nullValue());
        logString.assertAndClear("handleElementNotFound()");
    }


    @Test
    public void test_checkParametersAllNull() {
        try {
            new ComponentFinder().checkParameters(null, null, null);
        }
        catch (WebException e) {
            assertThat(e.getMessage(), is("Le champ 'id', 'text' ou 'xpath' doit être spécifié"));
        }
    }


    @Test
    public void test_checkParametersAllNotNull() {
        assertParametersError("text", "id", "xpath",
                              "Les champs 'id', 'text' et 'xpath' ne doivent pas être utilisés en même temps");
    }


    @Test
    public void test_checkParametersIdAndTextNotNull() {
        assertParametersError("text", "id", null,
                              "Les champs 'id' et 'text' ne doivent pas être utilisés en même temps");
    }


    @Test
    public void test_checkParametersIdAndXpathNotNull() {
        assertParametersError(null, "id", "xpath",
                              "Les champs 'id' et 'xpath' ne doivent pas être utilisés en même temps");
    }


    @Test
    public void test_checkParametersTextAndxpathNotNull() {
        assertParametersError("text", null, "xpath",
                              "Les champs 'text' et 'xpath' ne doivent pas être utilisés en même temps");
    }


    private void assertParametersError(String text, String id, String xpath, String expectedErrorMessage) {
        try {
            new ComponentFinder().checkParameters(text, id, xpath);
            fail();
        }
        catch (WebException e) {
            assertThat(e.getMessage(), is(expectedErrorMessage));
        }
    }


    private HtmlElement findElement(String htmlToTest, String id) throws Exception {
        WebContext context = loadPage(wrapHtml(htmlToTest));
        final HashMap<String, String> result = new HashMap<String, String>();
        result.put("text", null);
        result.put("id", (id != null ? id : null));
        result.put("xpath", null);
        result.put("cssClass", null);
        result.put("index", null);

        ComponentFinder<HtmlElement> finder = new ComponentFinder<HtmlElement>(result);
        return finder.find(context, mockResultHandler(logString));
    }


    private HtmlElement findElementByXpath(String htmlToTest, String xpath, Integer index) throws Exception {
        WebContext context = loadPage(wrapHtml(htmlToTest));
        final HashMap<String, String> result = new HashMap<String, String>();
        result.put("text", null);
        result.put("id", null);
        result.put("xpath", (xpath != null ? xpath : null));
        result.put("cssClass", null);
        result.put("index", (index != null ? String.valueOf(index) : null));

        ComponentFinder<HtmlElement> finder = new ComponentFinder<HtmlElement>(result);
        return finder.find(context, mockResultHandler(logString));
    }


    private HtmlElement findElementByText(String htmlToTest, String text) throws Exception {
        WebContext context = loadPage(wrapHtml(htmlToTest));
        final HashMap<String, String> result = new HashMap<String, String>();
        result.put("text", (text != null ? text : null));

        ComponentFinder<HtmlElement> finder = new ComponentFinder<HtmlElement>(result);
        return finder.find(context, mockResultHandler(logString));
    }


    private ResultHandler mockResultHandler(final LogString logString) {
        return new ResultHandler() {
            public void handleElementFound(HtmlElement element, String key) throws WebException {
                logString.call("handleElementFound");
            }


            public void handleElementNotFound(ElementNotFoundException e, String id) throws WebException {
                logString.call("handleElementNotFound");
            }


            public String getErrorMessage(String key) {
                return "Erreur avec  la key:" + key;
            }
        };
    }
}
