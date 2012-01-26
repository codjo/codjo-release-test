package net.codjo.test.release.util.comparisonstrategy;
import java.io.File;
import java.io.FileNotFoundException;
import org.apache.log4j.Logger;
import org.apache.tools.ant.taskdefs.condition.FilesMatch;

import static junit.framework.Assert.fail;
/**
 *
 */
public class BinaryComparisonStrategy implements ComparisonStrategy {
    private final static Logger LOGGER = Logger.getLogger(BinaryComparisonStrategy.class);
    static final String ERROR_MESSAGE = "Comparaison des fichiers binaires en erreur.";
    private String errorMessage = ERROR_MESSAGE;
    private File expectedFile;
    private File actualFile;


    public BinaryComparisonStrategy(File expectedFile, File actualFile) {
        this.expectedFile = expectedFile;
        this.actualFile = actualFile;
    }


    public boolean compare() throws FileNotFoundException {
        if (expectedFile == null || actualFile == null) {
            throw new FileNotFoundException(
                  "BinaryComparisonStrategy must be build with existing files");
        }

        FilesMatch filesMatch = new FilesMatch();
        filesMatch.setFile1(actualFile);
        filesMatch.setFile2(expectedFile);

        if (filesMatch.eval()) {
            return true;
        }

        LOGGER.info(errorMessage);
        fail(errorMessage);

        return false;
    }


    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
