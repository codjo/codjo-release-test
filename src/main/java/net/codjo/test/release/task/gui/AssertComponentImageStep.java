package net.codjo.test.release.task.gui;

import java.awt.Graphics;
import net.codjo.test.release.ImageManager;
import net.codjo.test.release.task.AgfTask;
import net.codjo.test.release.util.comparisonstrategy.BufferedImageComparisonStrategy;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
import static junit.framework.Assert.fail;
/**
 *
 */
public class AssertComponentImageStep extends AbstractAssertStep {

    private String name;
    private String expected;


    @Override
    protected void proceedOnce(TestContext context) {
        JComponent component = getComponentFromName(context);
        try {
            BufferedImage componentBufferedImage = exportImageFromComponent(component);
            BufferedImage expectedBufferedImage;
            String parentPath = context.getProperty(AgfTask.TEST_DIRECTORY);
            File expectedFile = new File(parentPath, expected);
            if (!expectedFile.exists()) {
                expectedBufferedImage = exportImageFromComponent(component);
                ImageIO.write(expectedBufferedImage, "bmp", expectedFile);
            }
            else {
                expectedBufferedImage = ImageIO.read(expectedFile);
            }

            proceedComparison(expectedBufferedImage, componentBufferedImage);
        }
        catch (IOException e) {
            fail("Impossible d'ouvrir le fichier spécifié.");
        }
    }


    private void proceedComparison(BufferedImage expectedFile, BufferedImage actualFile) {
        try {
            BufferedImageComparisonStrategy comparisonStrategy =
                  new BufferedImageComparisonStrategy(expectedFile, actualFile);
            if (!comparisonStrategy.compare()) {
                throw new GuiAssertException("");
            }
        }
        catch (FileNotFoundException ex) {
            throw new GuiAssertException(
                  "Unable to compare : " + expectedFile + " and " + actualFile, ex);
        }
    }


    private BufferedImage exportImageFromComponent(JComponent component) {
       BufferedImage image = new BufferedImage(component.getWidth(),
                                              component.getHeight(),
                                              BufferedImage.TYPE_INT_RGB);
        Graphics gx = image.getGraphics();
        component.paint(gx);
        gx.dispose();

        return image;
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
