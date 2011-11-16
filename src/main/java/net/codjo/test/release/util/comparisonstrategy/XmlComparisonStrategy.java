package net.codjo.test.release.util.comparisonstrategy;
import net.codjo.test.common.XmlUtil;
import net.codjo.util.file.FileUtil;
import java.io.File;
/**
 *
 */
public class XmlComparisonStrategy implements ComparisonStrategy {
    private File expectedFile;
    private File acutalFile;


    public XmlComparisonStrategy(File expectedFile, File acutalFile) {
        this.expectedFile = expectedFile;
        this.acutalFile = acutalFile;
    }


    public boolean compare() throws Exception {
        return XmlUtil.areEquivalent(FileUtil.loadContent(expectedFile), FileUtil.loadContent(acutalFile));
    }
}
