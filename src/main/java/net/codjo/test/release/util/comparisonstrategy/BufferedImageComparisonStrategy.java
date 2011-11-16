package net.codjo.test.release.util.comparisonstrategy;
import org.apache.log4j.Logger;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import static junit.framework.Assert.fail;
/**
 *
 */
public class BufferedImageComparisonStrategy implements ComparisonStrategy {
    private final static Logger logger = Logger.getLogger(BufferedImageComparisonStrategy.class);
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

        if (convertImageToPixelList(expectedImage).equals(convertImageToPixelList(actualImage))) {
            return true;
        }

        logger.info("Comparaison du component avec le fichier étalon en erreur.");
        fail("Comparaison du component avec le fichier étalon en erreur.");

        return false;
    }


    public static List<List<Integer>> convertImageToPixelList(BufferedImage image) {
        List<List<Integer>> all = new ArrayList<List<Integer>>();
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        int[] rgbs = new int[width * height];
        int index = 0;
        image.getRGB(0, 0, width, height, rgbs, 0, width);

        for (int i = 0; i < width; i++) {
            List<Integer> line = new ArrayList<Integer>();
            for (int j = 0; j < height; j++) {
                line.add(rgbs[index]);
                index++;
            }
            all.add(line);
        }
        return all;
    }
}
