package net.codjo.test.release.util.comparisonstrategy;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
/**
 *
 */
public class PDFComparisonStrategyTest extends ComparisonStrategyTestCase {

    @Test
    public void test_compare() throws Exception {
        PDFComparisonStrategy comparator = new PDFComparisonStrategy(
              toFile("PDFComparisonStrategyTest_expected.pdf"),
              toFile("PDFComparisonStrategyTest_expected.pdf"));
        assertTrue("Comparaison de fichier PDF en erreur", comparator.compare());
    }


    @Test
    public void test_compareWithImage() throws Exception {
        PDFComparisonStrategy comparator = new PDFComparisonStrategy(
              toFile("PDFComparisonStrategyTestWithImage_expected.pdf"),
              toFile("PDFComparisonStrategyTestWithImage_expected.pdf"));
        assertTrue("Comparaison de fichier PDF en erreur", comparator.compare());
    }


    @Test
    public void test_compareWithImageError() throws Exception {
        PDFComparisonStrategy comparator = new PDFComparisonStrategy(
              toFile("PDFComparisonStrategyTestWithImage_expected.pdf"),
              toFile("PDFComparisonStrategyTestWithImage_notSameAsExpected.pdf"));
        assertFalse("Comparaison de fichier PDF en erreur", comparator.compare());
    }


    @Test
    public void test_notEquals() throws Exception {
        PDFComparisonStrategy comparator = new PDFComparisonStrategy(
              toFile("PDFComparisonStrategyTest_notSameAsExpected.pdf"),
              toFile("PDFComparisonStrategyTest_expected.pdf"));
        assertFalse("Comparaison OK => Erreur", comparator.compare());
    }
}
