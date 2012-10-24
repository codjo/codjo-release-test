/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import junit.framework.TestResult;
import net.codjo.test.release.ant.AntRunner;
import net.codjo.test.release.task.tokio.TokioInsertListener;
import net.codjo.test.release.task.tokio.TokioLoadListener;
import net.codjo.util.file.FileUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.tools.ant.Project;
import org.xml.sax.SAXException;
/**
 * Lanceur de test release. Cette classe remplace la mécanique de génération via Maven et Jelly.<p><b>NB</b> : A
 * remplacer par la mécanique construction DOM en mémoire.</p>
 *
 * @noinspection UseOfSystemOutOrSystemErr, CallToPrintStackTrace
 */
public final class ReleaseTestRunner {
    /**
     * Maximum number of nested calls for methods.
     * It's used to detect infinite loop.
     */
    private static final int MAX_NUMBER_OF_NESTED_CALLS = 10;

    private final PrintStream out;

    private ReleaseTestRunner() {
        this(System.out);
    }

    ReleaseTestRunner(PrintStream out) {
        this.out = out;
    }

    public static void main(String[] args) {
        File pathFile = new File(args[0]);

        try {
            if (pathFile.isDirectory() && pathFile.exists()) {
                new ReleaseTestRunner().executeAllReleaseTest(pathFile);
            }
            else {
                executeTestFile(new File(".").getCanonicalFile(), pathFile);
            }
        }
        catch (Throwable error) {
            error.printStackTrace();
            System.exit(1);
        }

        // Fin sans erreur : System.exit car parfois reste en 'l'air' (thread IHM ?)
        System.exit(0);
    }


    public void executeAllReleaseTest(File releaseTestDirectory) {
        info("Execution des test contenus dans " + releaseTestDirectory.getAbsolutePath());

        SuiteBuilder suiteBuilder = new SuiteBuilder();
        XTest testSuite = suiteBuilder.createSuite(new File("."), releaseTestDirectory);

        info("" + testSuite.countTestCases() + " tests release trouvés ! ");

        TestResult testResult = junit.textui.TestRunner.run(testSuite);

        out.print(buildMessageIgnoredTests(testSuite));

        int errorCount = testResult.errorCount();
        if (errorCount != 0) {
            System.exit(-errorCount);
        }
    }

    private String buildMessageIgnoredTests(XTest testSuite) {
        List<String> tests = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(testSuite.getIgnoredTests())) {
            for (ReleaseTest test : testSuite.getIgnoredTests()) {
                tests.add(test.getName());
            }
        }
        return buildMessageIgnoredTests(tests);
    }

    static String buildMessageIgnoredTests(List<String> ignoredTests) {
        StringBuilder result = new StringBuilder();

        if (CollectionUtils.isNotEmpty(ignoredTests)) {
            result.append("***************************************\n");
            result.append("** WARNING : ").append(ignoredTests.size()).append(" tests release ignorés :\n");
            for (String test : ignoredTests) {
                result.append("** - ").append(test).append('\n');
            }
            result.append("***************************************\n");
        }

        return result.toString();
    }

    public static void executeTestFile(File baseDir, File releaseTestFile)
          throws IOException {
        String testDataDirectory = releaseTestFile.getParent();

        info("");
        info("---------------------------------------------------------------");
        info("Lancement du test release : " + releaseTestFile.getAbsolutePath());
        info("\tRepertoire des donnees du test : " + testDataDirectory);
        info("\tRepertoire de lancement : " + baseDir);

        // Surcharge du comportement de ANT
        System.setProperty("test.dir", testDataDirectory);
        System.setProperty("basedir", baseDir.getAbsolutePath());

        // Lancement du test
        try {
            AntRunner.start(new AntGenerator().generateAntFile(releaseTestFile));
        }
        catch (IOException e) {
            error("[TEST EN ECHEC :: " + releaseTestFile.getName() + "]", e);
            throw e;
        }
        finally {
            System.clearProperty("test.dir");
            System.clearProperty("basedir");
        }
    }


    public static void executeTestFile(File baseDir, File releaseTestFile, TestReport testReport)
          throws IOException {
        String testDataDirectory = releaseTestFile.getParent();

        info("");
        info("---------------------------------------------------------------");
        info("Lancement du test release : " + releaseTestFile.getAbsolutePath());
        info("\tRepertoire des donnees du test : " + testDataDirectory);
        info("\tRepertoire de lancement : " + baseDir);

        // Surcharge du comportement de ANT
        System.setProperty("test.dir", testDataDirectory);
        System.setProperty("basedir", baseDir.getAbsolutePath());

        // Lancement du test
        try {
            Project project = new Project();
            project.addBuildListener(new TokioLoadListener(testReport));
            project.addBuildListener(new TokioInsertListener(testReport));
            AntRunner.start(project, new AntGenerator().generateAntFile(releaseTestFile));
        }
        catch (IOException e) {
            error("[TEST EN ECHEC :: " + releaseTestFile.getName() + "]", e);
            throw e;
        }
        finally {
            System.clearProperty("test.dir");
            System.clearProperty("basedir");
        }
    }


    private static void info(String message) {
        System.out.println(message);
    }


    private static void error(String message, Exception error) {
        System.out
              .println("§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§");
        System.out.println(message);
        error.printStackTrace();
        System.out
              .println("§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§");
    }

    static String getTooManyLevelsMessage() {
        return "Too many levels in nested calls (maximum=" + MAX_NUMBER_OF_NESTED_CALLS + "). There might be a cycle in method calls (-> infinite loop).";
    }

    static class AntGenerator {
        public static final String XML_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

        // PREFIX et POSTFIX sont identique au fichier JELLY
        public static final String PREFIX =
              " <project name='Ant' default='test' basedir='.'>                                               "
              + "    <taskdef name='release-test' classname='net.codjo.test.release.task.ReleaseTask'/>         "
              + "    <taskdef name='copy-to-inbox' classname='net.codjo.test.release.task.imports.CopyToInbox'/>"
              + "    <taskdef name='cleanup' classname='net.codjo.test.release.task.cleanup.CleanupTask'/>"
              + "    <taskdef name='batch-import' classname='net.codjo.test.release.task.batch.BatchImportTask'/>                        "
              + "    <taskdef name='batch-export' classname='net.codjo.test.release.task.batch.BatchExportTask'/>                        "
              + "    <taskdef name='batch-segmentation' classname='net.codjo.test.release.task.batch.BatchSegmentationTask'/>                        "
              + "    <taskdef name='batch' classname='net.codjo.test.release.task.batch.BatchTask'/>                        "
              + "    <taskdef name='file-assert' classname='net.codjo.test.release.task.file.FileAssert'/>           "
              + "    <taskdef name='assert-file-exists' classname='net.codjo.test.release.task.file.AssertFileExists'/>           "
              + "    <taskdef name='tokio-load' classname='net.codjo.test.release.task.tokio.Load'/>            "
              + "    <taskdef name='tokio-set-db' classname='net.codjo.test.release.task.tokio.SetDb'/>         "
              + "    <taskdef name='tokio-assert' classname='net.codjo.test.release.task.tokio.Assert'/>        "
              + "    <taskdef name='gui-test' classname='net.codjo.test.release.task.gui.GuiTask'/>             "
              + "    <taskdef name='clear-cookies' classname='net.codjo.test.release.task.web.ClearCookiesTask'/>             "
              + "    <taskdef name='web-test' classname='net.codjo.test.release.task.web.WebTask'/>             "
              + "    <taskdef name='security-model' classname='net.codjo.test.release.task.security.SecurityModelTask'/>             "
              + "    <taskdef name='start-mail-server' classname='net.codjo.test.release.task.mail.StartMailServerTask'/>"
              + "    <taskdef name='assert-inbox' classname='net.codjo.test.release.task.mail.AssertInboxTask'/>"
              + "    <taskdef name='send-mail' classname='net.codjo.test.release.task.mail.SendMailTask'/>      "
              + "    <taskdef name='assert-excel' classname='net.codjo.test.release.task.excel.AssertExcelTask'/> "
              + "    <taskdef name='set-property' classname='net.codjo.test.release.task.SetPropertyTask'/> "
              + "    <property file='${basedir}/target/config/test-release.config'/>                          "
              + "    <target name='test'>                                                                     "
              + "        <condition property='broadcast.output' value='${broadcast.output.remote.dir}'>       "
              + "            <equals arg1='YES' arg2='${agf.test.remote}'/>                                   "
              + "        </condition>                                                                         "
              + "        <condition property='broadcast.output' value='${broadcast.output.dir}'>              "
              + "            <not>                                                                            "
              + "                <isset property='broadcast.output'/>                                         "
              + "            </not>                                                                           "
              + "        </condition>                                                                         ";
        public static final String POSTFIX =
              "      </target>                                                                             "
              + " </project>";


        public File getTemporaryDirectory() {
            return new File(System.getProperty("java.io.tmpdir"));
        }


        public void generateAnt(String testFileContent, Writer result)
              throws IOException {
            result.write(XML_PREFIX);
            result.write(PREFIX);
            result.write(testFileContent);
            result.write(POSTFIX);
        }


        public String loadContent(Reader reader) {
            String content = null;
            try {
                content = FileUtil.loadContent(reader);
            }
            catch (IOException e) {
                e.printStackTrace();  // Todo
            }
            return removeSchemaAttributes(removeHeader(content));
        }


        public String loadContent(File file) throws IOException {
            return removeSchemaAttributes(removeHeader(FileUtil.loadContent(file)));
        }


        private String removeHeader(String fileContent) {
            int headerEndIndex = fileContent.indexOf("?>");
            if (headerEndIndex != -1) {
                return fileContent.substring(headerEndIndex + 2);
            }
            return fileContent;
        }


        private String removeSchemaAttributes(String fileContent) {
            String result =
                  removeString(fileContent, " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
            result = removeString(result, " xsi:noNamespaceSchemaLocation=\"test-release.xsd\"");
            result = removeString(result, " xsi:noNamespaceSchemaLocation=\"http://test-release.xsd\"");
            result = removeString(result,
                                  " xsi:noNamespaceSchemaLocation=\"Z:/maven/repository/agf-test/xsds/test-release.xsd\"");
            result = removeString(result,
                                  " xsi:noNamespaceSchemaLocation=\"Z:\\maven\\repository\\agf-test\\xsds\\test-release.xsd\"");
            return result;
        }


        private String removeString(String source, String toFind) {
            int pos = source.indexOf(toFind);
            if (pos == -1) {
                return source;
            }

            return source.substring(0, pos) + source.substring(pos + toFind.length());
        }


        public File generateAntFile(File releaseTestFile)
              throws IOException {
            File antFile = new File(getTemporaryDirectory(), releaseTestFile.getName());

            Writer writer =
                  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(antFile), "UTF-8"));
            try {
                String testFileContent = loadContent(releaseTestFile);
                String testFileContentManaged =
                      manageMethods(releaseTestFile.getParentFile(), testFileContent, releaseTestFile.getAbsolutePath());
                generateAnt(testFileContentManaged, writer);
            }
            catch (ParserConfigurationException e) {
                throwIOException(e);
            }
            catch (SAXException e) {
                throwIOException(e);
            }
            finally {
                writer.close();
            }
            return antFile;
        }

        private void throwIOException(Exception rootCause) throws IOException {
            IOException ioe = new IOException(rootCause.getMessage());
            ioe.initCause(rootCause);
            throw ioe;
        }

        private String manageMethods(File currentDir, String testFileContent, String releaseTestFile)
              throws IOException, ParserConfigurationException, SAXException {

            String result = testFileContent;
            int nbMethodCalls;
            int nbLevels = 0;
            do {
                XmfManager xmfManager = new XmfManager(currentDir, releaseTestFile);
                result = xmfManager.parse(result);
                nbMethodCalls = xmfManager.getNbMethodCalls();

                nbLevels++;
                if (nbLevels > MAX_NUMBER_OF_NESTED_CALLS) {
                    throw new IOException(getTooManyLevelsMessage());
                }
            } while (nbMethodCalls > 0);

            return result;
        }
    }
}
