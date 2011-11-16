/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release;
import java.io.File;
import java.net.URL;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.tools.ant.DirectoryScanner;
/**
 * Classe utilitaire pour l'exécution des tests release
 *
 * @version $Revision: 1.10 $
 */
public class ReleaseTestHelper {
    private final File baseDir;
    private final File srcDir;


    public ReleaseTestHelper(Class allReleaseTestsClass) {
        this.srcDir = getSrcRoot(allReleaseTestsClass);
        this.baseDir = srcDir.getParentFile();
    }


    public ReleaseTestHelper(File baseDir, File testDirectory) {
        this.baseDir = baseDir;
        this.srcDir = testDirectory;
    }


    /**
     * Récupère un {@link Test} agrégeant tous les tests release.
     *
     * @return le test agrégé
     */
    public Test getAllTests() {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir(srcDir);
        ds.setCaseSensitive(false);
        ds.setIncludes(new String[]{"**/*.xml"});
        ds.setExcludes(new String[]{"**/xdocs/*"});
        ds.scan();

        String[] relativeFilePathArray = ds.getIncludedFiles();

        TestSuite suite = new TestSuite();

        for (String relativeFilePath : relativeFilePathArray) {
            File file = new File(srcDir, relativeFilePath);
            final ReleaseTest releaseTest = new ReleaseTest(baseDir, file);
            if (releaseTest.isEnabled()) {
                suite.addTest(releaseTest);
            }
        }

        return suite;
    }


    /**
     * Récupère le {@link Test} release passé en paramètre.
     *
     * @param testRelativeFilePath chemin relatif du test release au format XML
     *
     * @return le test
     */
    public Test getTest(String testRelativeFilePath) {
        TestSuite suite = new TestSuite();
        suite.addTest(new ReleaseTest(srcDir.getParentFile(), new File(srcDir, testRelativeFilePath)));
        return suite;
    }


    private File getSrcRoot(Class allReleaseTestsClass) {
        URL resource = allReleaseTestsClass.getResource(allReleaseTestsClass.getName() + ".class");
        ReleaseTestHelper.assertNotNull(resource);

        File classFile = new File(resource.getFile());
        return new File(classFile.getParentFile().getParentFile().getParentFile(), "src");
    }


    private static void assertNotNull(Object obj) {
        if (obj == null) {
            throw new InternalError("Impossible de trouver le repertoire source");
        }
    }
}
