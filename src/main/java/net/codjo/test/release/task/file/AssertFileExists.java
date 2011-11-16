package net.codjo.test.release.task.file;
import net.codjo.test.release.task.gui.GuiAssertException;

public class AssertFileExists extends AbstractFileAssert {
    private boolean expected;
    private String filename;


    @Override
    public void execute() {
        info("AssertFileExists expected >" + expected + "< actual >" + filename + "<...");

        copyFromRemote();

        if (expected && !getActualFile().exists()) {
            throw new GuiAssertException("Le fichier \'" + filename + "\' est introuvable");
        }
        else if (!expected && getActualFile().exists()) {
            throw new GuiAssertException("Le fichier \'" + filename + "\' ne devrait pas exister");
        }

        info("....AssertFileExists OK <");
    }


    @Override
    protected String getFileName() {
        return filename;
    }


    public void setExpected(boolean expected) {
        this.expected = expected;
    }


    public void setFilename(String filename) {
        this.filename = filename;
    }
}
