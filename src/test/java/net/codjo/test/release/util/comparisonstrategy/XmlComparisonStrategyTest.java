package net.codjo.test.release.util.comparisonstrategy;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
/**
 *
 */
public class XmlComparisonStrategyTest extends ComparisonStrategyTestCase {
    private XmlComparisonStrategy comparisonStrategy;


    @Test
    public void test_compare() throws Exception {
        comparisonStrategy = new XmlComparisonStrategy(
              toFile("FileAssertXml_expected.xml"), toFile("FileAssertXml.xml"));

        assertTrue(comparisonStrategy.compare());
    }


    @Test
    public void test_compare_notOk() throws Exception {
        comparisonStrategy = new XmlComparisonStrategy(
              toFile("FileAssertXml_expected.xml"), toFile("FileAssertXml_notSameAsExpected.xml"));

        assertFalse(comparisonStrategy.compare());
    }
}
