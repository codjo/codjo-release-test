package net.codjo.test.release.task.gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
import net.codjo.test.release.ImageManager;
import net.codjo.test.release.task.AgfTask;
import net.codjo.test.release.util.comparisonstrategy.BinaryComparisonStrategy;
import org.apache.log4j.Logger;
/**
 *
 */
public class AssertComponentImageStep extends AbstractAssertStep {
    private String name;
    private String expected;


    @Override
    protected void proceedOnce(TestContext context) {
        JComponent component = getComponentFromName(context);
        File expectedFile = new File(context.getProperty(AgfTask.TEST_DIRECTORY), expected);
        File actualFile = new File(System.getProperty("java.io.tmpdir"), "saved.bmp");
        try {
            exportImageFromComponent(component, actualFile);
            if (!expectedFile.exists()) {
                throw new GuiAssertException("Le fichier etalon est introuvable : " + expectedFile.getAbsolutePath());
            }

            proceedComparison(expectedFile, actualFile);
        }
        finally {
            actualFile.delete();
        }
    }


    private void proceedComparison(File expectedFile, File actualFile) {
        try {
            BinaryComparisonStrategy comparisonStrategy = new BinaryComparisonStrategy(expectedFile, actualFile);
            comparisonStrategy.setErrorMessage("Comparaison du component avec le fichier étalon en erreur.");
            if (!comparisonStrategy.compare()) {
                throw new GuiAssertException("");
            }
        }
        catch (FileNotFoundException ex) {
            throw new GuiAssertException("Unable to compare : " + expectedFile + " and " + actualFile, ex);
        }
    }


    private void exportImageFromComponent(JComponent component, File generatedFile) {
        BufferedImage image = ImageManager.getScreenCapture(component);
        Graphics2D g2 = image.createGraphics();
        component.paint(g2);
        g2.dispose();

        try {
            ImageIO.write(image, "bmp", generatedFile);
        }
        catch (IOException e) {
            throw new GuiAssertException("Impossible de créer le fichier image à partir du component.", e);
        }
    }


    private JComponent getComponentFromName(TestContext context) {
        NamedComponentFinder finder = new NamedComponentFinder(JComponent.class, name);
        JComponent component = (JComponent)findOnlyOne(finder, context, 0);
        if (component == null) {
            throw new GuiFindException("Le composant '" + getName() + "' est introuvable.");
        }
        return component;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getExpected() {
        return expected;
    }


    public void setExpected(String expected) {
        this.expected = expected;
    }
}
