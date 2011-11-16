package net.codjo.test.release.task.gui;
import net.codjo.test.release.task.gui.matcher.Matcher;
import net.codjo.test.release.task.gui.matcher.MatcherFactory;
/**
 *
 */
public abstract class AbstractMatchingStep extends AbstractAssertStep {
    protected String expected;
    private String matching = MatcherFactory.EQUALS_MATCHING;

    private MatcherFactory matcherFactory = new MatcherFactory();


    public String getExpected() {
        return expected;
    }


    public void setExpected(String expected) {
        this.expected = expected;
    }


    public String getMatching() {
        return matching;
    }


    public void setMatching(String matching) {
        this.matching = matching;
    }

    protected boolean compareWithExpectedValue(String actualValue) {
        Matcher matcher = matcherFactory.get(matching, actualValue);
        return matcher.match(expected);
    }

    protected void assertExpected(String actualValue) {
        if (!compareWithExpectedValue(actualValue)) {
            throw new GuiAssertException("Composant " + getComponentName()
                                         + " : attendu='" + expected
                                         + "' obtenu='" + actualValue + "'");
        }
    }


    protected abstract String getComponentName();

}
