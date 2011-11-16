package net.codjo.test.release.task.file;
import net.codjo.test.release.util.comparisonstrategy.ComparisonStrategy;
import net.codjo.test.release.util.comparisonstrategy.ExcelComparisonStrategy;
import net.codjo.test.release.util.comparisonstrategy.PDFComparisonStrategy;
import net.codjo.test.release.util.comparisonstrategy.PlainFileComparisonStrategy;
import net.codjo.test.release.util.comparisonstrategy.XmlComparisonStrategy;
import java.io.File;
import org.apache.tools.ant.BuildException;

public class FileAssert extends AbstractFileAssert {
    private String actual;
    private String expected;
    private boolean order = false;
    private boolean compareStyle = false;
    private String comparisonType;


    @Override
    public void execute() {
        info("FileAssert expected >" + getExpected() + "< actual >" + getActual() + "<...");

        copyFromRemote();

        try {
            doExecute();
        }
        catch (Throwable ex) {
            info("Comparaison en erreur:");
            info("\texpected : " + getExpectedFile().getName() + " (" + getExpectedFile() + ")");
            info("\tactual   : " + getActualFile().getName() + " (" + getActualFile() + ")");
            if (ex instanceof BuildException) {
                if (ex.getCause() != null) {
                    info("\t-> " + ex.getCause().getMessage());
                }
                throw (BuildException)ex;
            }
            info("\t-> " + ex.getMessage());
            throwComparisonFailure(ex);
        }

        info("....FileAssert OK <");
    }


    public String getExpected() {
        return expected;
    }


    public void setExpected(String expected) {
        this.expected = expected;
    }


    public String getActual() {
        return actual;
    }


    public void setActual(String actual) {
        this.actual = actual;
    }


    public boolean isOrder() {
        return order;
    }


    public void setOrder(boolean order) {
        this.order = order;
    }


    public void setComparisonType(String comparisonType) {
        this.comparisonType = comparisonType;
    }


    @Override
    protected String getFileName() {
        return getActual();
    }


    private void doExecute() throws Exception {
        if (isOfType("xls")) {
            info("\tUse Excel comparison strategy...");
            assertFile(new ExcelComparisonStrategy(getExpectedFile(), getActualFile(), compareStyle));
        }
        else if (isOfType("pdf")) {
            info("\tUse PDF comparison strategy...");
            assertFile(new PDFComparisonStrategy(getExpectedFile(), getActualFile()));
        }
        else if (isOfType("xml")) {
            info("\tUse XML comparison strategy...");
            assertFile(new XmlComparisonStrategy(getExpectedFile(), getActualFile()));
        }
        else if (comparisonType != null) {
            throw new BuildException(String.format("Valeur incorrecte pour l'attribut comparisonType (%s).",
                                                   comparisonType));
        }
        else {
            assertFile(new PlainFileComparisonStrategy(getExpectedFile(), getActualFile(), order));
        }
    }


    private boolean isOfType(String type) {
        return type.equals(comparisonType) || expected.endsWith("." + type);
    }


    private File getExpectedFile() {
        return toAbsoluteFile(getExpected());
    }


    void setCompareStyle(boolean compareStyle) {
        this.compareStyle = compareStyle;
    }


    private void assertFile(ComparisonStrategy comparisonStrategy) throws Exception {
        if (!comparisonStrategy.compare()) {
            throwComparisonFailure(null);
        }
    }


    private void throwComparisonFailure(Throwable cause) {
        throw new BuildException("Fichier produit en erreur : " + getActualFile(), cause);
    }
}
