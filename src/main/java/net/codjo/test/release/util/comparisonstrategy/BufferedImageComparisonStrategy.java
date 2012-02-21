package net.codjo.test.release.util.comparisonstrategy;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import org.apache.log4j.Logger;

import static junit.framework.Assert.fail;
/**
 *
 */
public class BufferedImageComparisonStrategy implements ComparisonStrategy {
    private final static Logger LOGGER = Logger.getLogger(BufferedImageComparisonStrategy.class);
    private BufferedImage expectedImage;
    private BufferedImage actualImage;


    public BufferedImageComparisonStrategy(BufferedImage expectedFile, BufferedImage actualFile) {
        this.expectedImage = expectedFile;
        this.actualImage = actualFile;
    }


    public boolean compare() throws FileNotFoundException {
        if (expectedImage == null || actualImage == null) {
            throw new FileNotFoundException(
                  "BufferedImageComparisonStrategy must be build with existing files");
        }

        if (comparePixelPerPixel(expectedImage, actualImage)) {
            return true;
        }

        LOGGER.info("Comparaison du component avec le fichier étalon en erreur.");
        fail("Comparaison du component avec le fichier étalon en erreur.");

        return false;
    }


    private boolean comparePixelPerPixel(BufferedImage expectedBufferedImage, BufferedImage actualBufferedImage) {
        int wExpected = expectedBufferedImage.getWidth(null);
        int hExpected = expectedBufferedImage.getHeight(null);
        int[] rgbsExpected = new int[wExpected * hExpected];
        expectedBufferedImage.getRGB(0, 0, wExpected, hExpected, rgbsExpected, 0, wExpected);

        int wActual = actualBufferedImage.getWidth(null);
        int hActual = actualBufferedImage.getHeight(null);
        int[] rgbsActual = new int[wActual * hActual];
        actualBufferedImage.getRGB(0, 0, wActual, hActual, rgbsActual, 0, wActual);

        if (wExpected != wActual || hExpected != hActual) {
            return false;
        }

        for (int i = 0; i < wExpected * hExpected; i++) {
            if (rgbsExpected[i] != rgbsActual[i]) {
                return false;
            }
        }
        return true;
    }
}
