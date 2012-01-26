package net.codjo.test.release.util.comparisonstrategy;
import java.io.FileNotFoundException;
import junit.framework.AssertionFailedError;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
/**
 *
 */
public class BinaryComparisonStrategyTest extends ComparisonStrategyTestCase {

    @Test
    public void test_compare_ok() throws Exception {
        BinaryComparisonStrategy strategy =
              new BinaryComparisonStrategy(toFile("FileBinaryComparisonStrategyTest_expected.bmp"),
                                           toFile("FileBinaryComparisonStrategyTest_expected.bmp"));

        assertTrue("Comparaison de fichier binaire en erreur", strategy.compare());
    }


    @Test
    public void test_compare_nok() throws Exception {
        BinaryComparisonStrategy strategy =
              new BinaryComparisonStrategy(toFile("FileBinaryComparisonStrategyTest_expected.bmp"),
                                           toFile("FileBinaryComparisonStrategyTest_notSameAsExpected.bmp"));
        try {
            strategy.compare();
            fail("Pas d'erreur alors que les deux fichiers sont différents.");
        }
        catch (AssertionFailedError cf) {
            ;
        }
    }


    @Test
    public void test_compare_file_not_found() throws Exception {
        BinaryComparisonStrategy strategy =
              new BinaryComparisonStrategy(toFile("FileBinaryComparisonStrategyTest_expected.bmp"),
                                           null);

        try {
            strategy.compare();
            fail("BinaryComparisonStrategy must be build with existing files");
        }
        catch (FileNotFoundException e) {
            ;
        }
    }
}
