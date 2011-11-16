/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 * Cette classe encapsule un fichier XML de test release dans un TestCase JUnit.
 *
 * @version $Revision: 1.10 $
 */
public class ReleaseTest extends TestCase {
    public static final String RELEASE_TEST_EXTENSION = ".xml";
    private static final String ENABLED = "enabled";
    private static final String TRUE = "true";
    private final File file;
    private final File baseDir;
    private TestReport testResport = null;


    public ReleaseTest(File baseDir, File file) {
        this.baseDir = baseDir;
        this.file = file;

        setName(computeTestName());
    }


    private String computeTestName() {
        String name = file.getName();
        if (name.endsWith(RELEASE_TEST_EXTENSION)) {
            name = name.substring(0, name.length() - RELEASE_TEST_EXTENSION.length());
        }
        return file.getParentFile().getName() + "__" + name;
    }


    @Override
    protected void runTest() throws IOException {
        if (testResport != null) {
            ReleaseTestRunner.executeTestFile(baseDir, file, testResport);
        }
        else {
            ReleaseTestRunner.executeTestFile(baseDir, file);
        }
    }


    public boolean isEnabled() {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);

            Element root = document.getDocumentElement();
            String enabled = root.getAttribute(ENABLED);
            return enabled == null || TRUE.equals(enabled.trim()) || enabled.trim().length() == 0;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }


    public void setTestResport(TestReport testResport) {
        this.testResport = testResport;
    }
}
