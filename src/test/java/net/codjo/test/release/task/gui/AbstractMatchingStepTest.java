package net.codjo.test.release.task.gui;
import net.codjo.test.release.task.gui.matcher.MatcherFactory;
import junit.framework.TestCase;
/**
 *
 */
public class AbstractMatchingStepTest extends TestCase {

    public void test_compareWithExpectedValue() {
        MyMatchingStep step = new MyMatchingStep();
        String actualValue = "Salut toto";

        step.setExpected("toto");
        step.setMatching(MatcherFactory.EQUALS_MATCHING);
        assertFalse(step.compareWithExpectedValue(actualValue));
        step.setMatching(MatcherFactory.CONTAINS_MATCHING);
        assertTrue(step.compareWithExpectedValue(actualValue));
        step.setMatching(MatcherFactory.ENDS_WITH_MATCHING);
        assertTrue(step.compareWithExpectedValue(actualValue));
        step.setMatching(MatcherFactory.STARTS_WITH_MATCHING);
        assertFalse(step.compareWithExpectedValue(actualValue));

        step.setExpected("Salut");
        step.setMatching(MatcherFactory.EQUALS_MATCHING);
        assertFalse(step.compareWithExpectedValue(actualValue));
        step.setMatching(MatcherFactory.CONTAINS_MATCHING);
        assertTrue(step.compareWithExpectedValue(actualValue));
        step.setMatching(MatcherFactory.ENDS_WITH_MATCHING);
        assertFalse(step.compareWithExpectedValue(actualValue));
        step.setMatching(MatcherFactory.STARTS_WITH_MATCHING);
        assertTrue(step.compareWithExpectedValue(actualValue));

        step.setExpected("t t");
        step.setMatching(MatcherFactory.EQUALS_MATCHING);
        assertFalse(step.compareWithExpectedValue(actualValue));
        step.setMatching(MatcherFactory.CONTAINS_MATCHING);
        assertTrue(step.compareWithExpectedValue(actualValue));
        step.setMatching(MatcherFactory.ENDS_WITH_MATCHING);
        assertFalse(step.compareWithExpectedValue(actualValue));
        step.setMatching(MatcherFactory.STARTS_WITH_MATCHING);
        assertFalse(step.compareWithExpectedValue(actualValue));

        step.setExpected("chat");
        step.setMatching(MatcherFactory.EQUALS_MATCHING);
        assertFalse(step.compareWithExpectedValue(actualValue));
        step.setMatching(MatcherFactory.CONTAINS_MATCHING);
        assertFalse(step.compareWithExpectedValue(actualValue));
        step.setMatching(MatcherFactory.ENDS_WITH_MATCHING);
        assertFalse(step.compareWithExpectedValue(actualValue));
        step.setMatching(MatcherFactory.STARTS_WITH_MATCHING);
        assertFalse(step.compareWithExpectedValue(actualValue));
    }


    public void test_assertExpected() {
        MyMatchingStep step = new MyMatchingStep();
        String actualValue = "Salut toto";

        String expected = "toto";
        step.setExpected(expected);
        step.setMatching(MatcherFactory.EQUALS_MATCHING);
        checkException(step, actualValue, expected);
        step.setMatching(MatcherFactory.CONTAINS_MATCHING);
        step.assertExpected(actualValue);
        step.setMatching(MatcherFactory.ENDS_WITH_MATCHING);
        step.assertExpected(actualValue);
        step.setMatching(MatcherFactory.STARTS_WITH_MATCHING);
        checkException(step, actualValue, expected);

        expected = "Salut";
        step.setExpected(expected);
        step.setMatching(MatcherFactory.EQUALS_MATCHING);
        checkException(step, actualValue, expected);
        step.setMatching(MatcherFactory.CONTAINS_MATCHING);
        step.assertExpected(actualValue);
        step.setMatching(MatcherFactory.ENDS_WITH_MATCHING);
        checkException(step, actualValue, expected);
        step.setMatching(MatcherFactory.STARTS_WITH_MATCHING);
        assertTrue(step.compareWithExpectedValue(actualValue));

        expected = "t t";
        step.setExpected(expected);
        step.setMatching(MatcherFactory.EQUALS_MATCHING);
        checkException(step, actualValue, expected);
        step.setMatching(MatcherFactory.CONTAINS_MATCHING);
        step.assertExpected(actualValue);
        step.setMatching(MatcherFactory.ENDS_WITH_MATCHING);
        checkException(step, actualValue, expected);
        step.setMatching(MatcherFactory.STARTS_WITH_MATCHING);
        checkException(step, actualValue, expected);

        expected = "chat";
        step.setExpected(expected);
        step.setMatching(MatcherFactory.EQUALS_MATCHING);
        checkException(step, actualValue, expected);
        step.setMatching(MatcherFactory.CONTAINS_MATCHING);
        checkException(step, actualValue, expected);
        step.setMatching(MatcherFactory.ENDS_WITH_MATCHING);
        checkException(step, actualValue, expected);
        step.setMatching(MatcherFactory.STARTS_WITH_MATCHING);
        checkException(step, actualValue, expected);
    }


    private void checkException(MyMatchingStep step, String actualValue, String expected) {
        try {
            step.assertExpected(actualValue);
            fail("Exception attendue.");
        }
        catch (GuiAssertException ex) {
            assertEquals("Composant MyComponent : attendu='" + expected + "' obtenu='" + actualValue + "'",
                         ex.getMessage());
        }
    }


    private class MyMatchingStep extends AbstractMatchingStep {

        @Override
        protected String getComponentName() {
            return "MyComponent";
        }


        @Override
        protected void proceedOnce(TestContext context) {

        }
    }
}
