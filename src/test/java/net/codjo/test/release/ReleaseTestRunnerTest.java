package net.codjo.test.release;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;
import net.codjo.test.common.fixture.CompositeFixture;
import net.codjo.test.common.fixture.DirectoryFixture;
import net.codjo.test.common.fixture.SystemExitFixture;
import net.codjo.util.file.FileUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ReleaseTestRunnerTest {
    private static final String XML_PREFIX = ReleaseTestRunner.AntGenerator.XML_PREFIX;
    private static final String PREFIX = ReleaseTestRunner.AntGenerator.PREFIX;
    private static final String POSTFIX = ReleaseTestRunner.AntGenerator.POSTFIX;
    private ReleaseTestRunner.AntGenerator antGenerator;
    private DirectoryFixture directory = DirectoryFixture.newTemporaryDirectoryFixture();
    private SystemExitFixture exitFixture = new SystemExitFixture();
    private CompositeFixture fixture = new CompositeFixture(directory, exitFixture);


    @Before
    public void setUp() throws Exception {
        fixture.doSetUp();
        antGenerator = new ReleaseTestRunner.AntGenerator();
    }


    @After
    public void tearDown() throws Exception {
        fixture.doTearDown();
    }


    @Test
    public void test_executeAllTestRelease() throws Exception {
        File useCaseDir = new File(directory, "uneFonction");
        useCaseDir.mkdir();

        File firstCreatedFile = createCase(useCaseDir, "unCas");
        File secondCreatedFile = createCase(useCaseDir, "unAutreCas");

        try {
            ReleaseTestRunner.main(new String[]{directory.getAbsolutePath()});
            fail();
        }
        catch (SecurityException ex) {
        }

        assertTrue(firstCreatedFile.exists());
        assertTrue(secondCreatedFile.exists());
        assertScriptProperty(firstCreatedFile, new File("."), "basedir");
        exitFixture.getLog().assertContent("System.exit(0)");
    }


    @Test
    public void test_executeAllTestReleaseWith2Error()
          throws Exception {
        File useCaseDir = new File(directory, "uneFonction");
        useCaseDir.mkdir();

        createFailingCase(useCaseDir, "unCas");
        File firstTouchedFile = createCase(useCaseDir, "workingTest");
        createFailingCase(useCaseDir, "unAutreCas");

        try {
            ReleaseTestRunner.main(new String[]{directory.getAbsolutePath()});
            fail();
        }
        catch (SecurityException ex) {
        }

        assertTrue(firstTouchedFile.exists());
        exitFixture.getLog().assertContent("System.exit(-2), System.exit(1)");
    }


    @Test
    public void test_generateContent() throws Exception {
        String currentTest = "<release-test>...</release-test>";
        StringWriter result = new StringWriter();

        antGenerator.generateAnt(currentTest, result);

        assertEquals(XML_PREFIX + PREFIX + currentTest + POSTFIX, result.toString());
    }


    @Test
    public void test_loadContent_withHeader() throws Exception {
        String header = "<?xml version='1.0' encoding='ISO-8859-1'?>";
        String currentTest = "<release-test>...</release-test>";

        assertEquals(currentTest, antGenerator.loadContent(new StringReader(header + currentTest)));
    }


    @Test
    public void test_loadContent_withXsdReference()
          throws Exception {
        String cleanedContent = "<release-test>...</release-test>";

        String contentWithXsdFullReference =
              "<release-test xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"test-release.xsd\">...</release-test>";
        assertEquals(cleanedContent, antGenerator.loadContent(new StringReader(contentWithXsdFullReference)));
    }


    @Test
    public void test_generateFile() throws Exception {
        File resultAntFile = antGenerator.generateAntFile(getReleaseTestFile("ReleaseTestRunnerTest.xml"));

        assertEquals(getTemporaryDirectory(), resultAntFile.getParentFile());
        assertTrue(resultAntFile.exists());
        assertFlat(PREFIX
                   + "<release-test name=\"VL_cotebleu\"><echo>Un message accentué.</echo><copy-to-inbox file=\"ReleaseTestRunnerTest.xml\"/></release-test>"
                   + POSTFIX, loadContentResultFile(resultAntFile));
    }


    @Test
    public void test_callMethod() throws Exception {
        File resultAntFile =
              antGenerator.generateAntFile(getReleaseTestFile("ReleaseTestRunner_call-method.xml"));

        assertFlat(PREFIX
                   + "<release-test name=\"ApInvoiceAdmin\"><description><![CDATA[Ma description]]></description><setvalue name=\"monNom1\" value=\"&lt;\"/><gui-test user=\"GABI\"><setvalue name=\"monNom3\" value=\"&lt;\"/><group name=\"ReleaseTestRunner_call-method.xmf(@user@=GABI):::test\"/><setvalue name=\"monNom2\" value=\"&lt;\"/></gui-test></release-test>"
                   + POSTFIX, loadContentResultFile(resultAntFile));
    }

    @Test
    public void test_callMethod_emptyName() throws Exception {
        String fileName = "ReleaseTestRunner_call-method_emptyName.xml";
        try {
            antGenerator.generateAntFile(getReleaseTestFile(fileName));
            fail("must throw an IOException");
        } catch (IOException e) {
            assertMessageContains(e, fileName, "releaseTestFile's name");
        }
    }

    @Test
    public void test_callMethod_nestedCall() throws Exception {
        File resultAntFile =
              antGenerator.generateAntFile(getReleaseTestFile("ReleaseTestRunner_call-method_nestedCall.xml"));

        String expected = flatten("<release-test name=\"nestedCall\">\n"
                                  + "    <description><![CDATA[Test appels imbriques]]></description>\n"
                                  + "\t<gui-test>\n"
                                  + "\t\t<group name=\"ReleaseTestRunner_call-method_nestedCall_methodA.xmf(@parameterA@=valueA):::group-methodA\">\n"
                                  + "\t\t\t<click name=\"valueA\"/>\n"
                                  + "\t\t\t<group name=\"ReleaseTestRunner_call-method_nestedCall_methodB.xmf(@parameterB@=valueB):::group-methodB\">\n"
                                  + "\t\t\t\t<click name=\"valueB\"/>\n"
                                  + "\t\t\t</group>\t\t\t\n"
                                  + "\t\t</group>\n"
                                  + "\t</gui-test>\t\n"
                                  + "</release-test>");
        assertFlat(PREFIX + expected + POSTFIX, loadContentResultFile(resultAntFile));
    }

    @Test
    public void test_callMethod_nestedCallWithCycle() throws Exception {
        String fileName = "ReleaseTestRunner_call-method_nestedCallWithCycle.xml";
        try {
            antGenerator.generateAntFile(getReleaseTestFile(fileName));
            fail("must throw an IllegalStateException");
        } catch (IllegalStateException e) {
            assertMessageContains(e, "Too many levels in nested calls", "Too many levels in nested calls");
        }
    }

    @Test
    public void test_callMethod_noOptionalParameter() throws Exception {
        File resultAntFile =
              antGenerator.generateAntFile(getReleaseTestFile("ReleaseTestRunner_call-method_noOptionalParameter.xml"));

        assertFlat(PREFIX
                   + "<release-test name=\"NoOptionalParameter\"><description><![CDATA[Testavecunparametreoptionnelnonfourni]]></description><gui-test><group name=\"ReleaseTestRunner_call-method_OptionalAndRequiredParameters.xmf(@requiredParameter@=GABI@notRequiredParameter@=):::test-textdata-group-tag\"><click name=\"GABI\"/></group></gui-test></release-test>"
                   + POSTFIX, loadContentResultFile(resultAntFile));
    }

    @Test
    public void test_callMethod_noRequiredParameter() throws Exception {
        String fileName = "ReleaseTestRunner_call-method_noRequiredParameter.xml";
        try {
            antGenerator.generateAntFile(getReleaseTestFile(fileName));
            fail("must throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertMessageContains(e, fileName, "releaseTestFile's name");
            assertMessageContains(e, "requiredParameter", "parameter's name");
        }
    }

    private void assertMessageContains(Exception e, String messagePart, String messagePartDesc) {
        if (!e.getMessage().contains(messagePart)) {
            fail("must throw an " + e.getClass().getSimpleName() + " whose message contains " + messagePartDesc);
        }
    }

    @Test
    public void test_callMethod_noParameters() throws Exception {
        File resultAntFile =
              antGenerator.generateAntFile(getReleaseTestFile("ReleaseTestRunner_call-method_noParameters.xml"));

        assertFlat(PREFIX
                   + "<release-test name=\"ApInvoiceAdmin\"><description><![CDATA[Ma description]]></description><setvalue name=\"monNom1\" value=\"&lt;\"/><gui-test><setvalue name=\"monNom3\" value=\"&lt;\"/></gui-test><gui-test>No parameters 2</gui-test><gui-test user=\"GABI\"><setvalue name=\"monNom3\" value=\"&lt;\"/><group name=\"ReleaseTestRunner_call-method.xmf(@user@=GABI):::test\"/><setvalue name=\"monNom2\" value=\"&lt;\"/></gui-test></release-test>"
                   + POSTFIX, loadContentResultFile(resultAntFile));
    }


    @Test
    public void test_callMethod_noParameters_prefix_on_group() throws Exception {
        File resultAntFile =
              antGenerator.generateAntFile(getReleaseTestFile(
                    "ReleaseTestRunner_call-method_noParameters_group_prefix.xml"));

        assertFlat(PREFIX
                   + "<release-test name=\"ApInvoiceAdmin\"><description><![CDATA[Ma description]]></description><gui-test><group name=\"ReleaseTestRunner_call-method_noParameters_group_prefix.xmf(@age@=1@dateDeNaissance@=21/02/2002@nom@=toto):::unGroupe avec une seul balise et un nom\"/><group name=\"ReleaseTestRunner_call-method_noParameters_group_prefix.xmf(@age@=1@dateDeNaissance@=21/02/2002@nom@=toto):::unGroupe avec deux balises et un nom\"></group><group/><group></group></gui-test></release-test>"
                   + POSTFIX, loadContentResultFile(resultAntFile));
    }


    @Test
    public void test_callMethod_prefix_on_group() throws Exception {
        File resultAntFile =
              antGenerator.generateAntFile(getReleaseTestFile("ReleaseTestRunner_call-method_group_prefix.xml"));

        assertFlat(PREFIX
                   + "<release-test name=\"ApInvoiceAdmin\"><description><![CDATA[Ma description]]></description><gui-test><group name=\"ReleaseTestRunner_call-method_group_prefix.xmf(@age@=1@dateDeNaissance@=21/02/2002@nom@=toto):::unGroupe avec une seul balise et un nom\"/><group name=\"ReleaseTestRunner_call-method_group_prefix.xmf(@age@=1@dateDeNaissance@=21/02/2002@nom@=toto):::unGroupe avec deux balises et un nom\"></group><group/><group></group></gui-test></release-test>"
                   + POSTFIX, loadContentResultFile(resultAntFile));
    }


    @Test
    public void test_copyToInbox_withVariable_antGenerationOk() throws Exception {
        File releaseTestFile = getReleaseTestFile("ReleaseTestRunner_CopyToInbox_withVariable.xml");

        try {
            ReleaseTestRunner.executeTestFile(releaseTestFile.getParentFile(), releaseTestFile);
            fail();
        }
        catch (Exception e) {
            String errorMessage = e.getMessage();
            assertTrue(errorMessage.startsWith("Warning: Could not find file"));
            assertTrue(errorMessage.endsWith("my-file.txt to copy."));
        }
    }


    @Test
    public void test_tempFolder() throws Exception {
        assertEquals(getTemporaryDirectory(), antGenerator.getTemporaryDirectory());
    }


    private void assertFlat(String expected, String actual) {
        assertEquals(flatten(expected), flatten(actual));
    }


    private String loadContentResultFile(File resultAntFile) throws IOException {
        Reader testFileReader =
              new BufferedReader(new InputStreamReader(new FileInputStream(resultAntFile), "UTF-8"));
        return antGenerator.loadContent(testFileReader);
    }


    private File getReleaseTestFile(String name) {
        return new File(ReleaseTestRunnerTest.class.getResource(name).getFile());
    }


    private File getTemporaryDirectory() {
        return new File(System.getProperty("java.io.tmpdir"));
    }


    public static String flatten(String str) {
        return flatten(str, true);
    }


    public static String flatten(String str, boolean skipWhitespace) {
        StringBuilder buffer = new StringBuilder();
        boolean previousWhite = true;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '\r' || ch == '\n') {
                ; // On fait rien
            }
            else if (Character.isWhitespace(ch) || Character.isSpaceChar(ch)) {
                if (!skipWhitespace) {
                    if (!previousWhite) {
                        buffer.append(" ");
                    }
                    previousWhite = true;
                }
            }
            else {
                buffer.append(ch);
                previousWhite = false;
            }
        }

        return buffer.toString();
    }


    private File createCase(File useCaseDir, String id)
          throws IOException {
        File firstTouchedFile = new File(useCaseDir, id + ".txt");

        String firstCase;
        firstCase = "<release-test>";
        firstCase += "   <touch file='" + firstTouchedFile + "'/>";
        firstCase += "   <echoproperties destfile='" + firstTouchedFile + "'";
        firstCase += "                   failonerror='false'/>";
        firstCase += "</release-test>";

        FileUtil.saveContent(new File(useCaseDir, id + ".xml"), firstCase);
        return firstTouchedFile;
    }


    private void createFailingCase(File useCaseDir, String id)
          throws IOException {
        String firstCase;
        firstCase = "<release-test>";
        firstCase += "   <fail/>";
        firstCase += "</release-test>";
        FileUtil.saveContent(new File(useCaseDir, id + ".xml"), firstCase);
    }


    private void assertScriptProperty(File firstCreatedFile, File expected, String propertyName)
          throws IOException {
        Properties properties = new Properties();
        FileInputStream inStream = new FileInputStream(firstCreatedFile);
        try {
            properties.load(inStream);
        }
        finally {
            inStream.close();
        }
        assertEquals(expected.getCanonicalFile(), new File(properties.getProperty(propertyName)));
    }
}
