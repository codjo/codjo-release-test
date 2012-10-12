/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

public class SuiteBuilder {
    private static final Logger LOGGER = Logger.getLogger(SuiteBuilder.class);
    private TestReport testReport;


    public SuiteBuilder() {
        try {
            new File("./target").mkdirs();
            testReport = new TestReport("./target/testreleaseSuite.log.xls");
        }
        catch (FileNotFoundException fileNotFoundException) {
            LOGGER.warn("Impossible d'acceder à './target/testreleaseSuite.log.xls'");
        }
    }


    public Test createSuite(Class testsClass) {
        ReleaseTestHelper helper = new ReleaseTestHelper(testsClass);
        return new CatchErrorTest(helper.getAllTests());
    }


    /**
     * DOCUMENT ME!
     *
     * @param testDirectory TODO
     *
     * @return TODO
     *
     * @deprecated utiliser la version avec basedir {@link #createSuite(java.io.File, java.io.File)}
     */
    @Deprecated
    public Test createSuite(File testDirectory) {
        return createSuite(testDirectory.getParentFile(), testDirectory);
    }


    public XTest createSuite(File baseDir, File testDirectory) {
        ReleaseTestHelper helper = new ReleaseTestHelper(baseDir, testDirectory);
        return new CatchErrorTest(helper.getAllTests());
    }


    private static String buildTestFileName(Test test) {
        String testString = test.toString();
        String releaseTestName = testString.substring(0, testString.indexOf('('));
        return "./target/ERROR_IN_TEST__" + releaseTestName + ".txt";
    }


    private static void buildErrorTestFile(Test test, Throwable throwable) {
        try {
            PrintStream errorFileStream = new PrintStream(new FileOutputStream(buildTestFileName(test)));
            throwable.printStackTrace(errorFileStream);
            errorFileStream.flush();
        }
        catch (FileNotFoundException e) {
            LOGGER.debug("buildErrorTestFile", e);
        }
    }


    private class CatchErrorTest implements XTest {
        private TestSuite testSuite;
        private XTest extendedTest;


        CatchErrorTest(Test innerTest) {
            testSuite = new TestSuite();
            for (int testIndex = 0; testIndex < ((TestSuite)innerTest).testCount(); testIndex++) {
                Test test = ((TestSuite)innerTest).testAt(testIndex);
                testSuite.addTest(new LoggerTest(test));
            }

            if (innerTest instanceof XTest) {
                extendedTest = (XTest)innerTest;
            }
        }


        public int countTestCases() {
            return testSuite.countTestCases();
        }


        public void run(TestResult testResult) {
            try {
                testSuite.run(testResult);
            }
            catch (Throwable exception) {
                testReport.printExceptionInReport(exception);
                testResult.addError(this, exception);
                buildErrorTestFile(this, exception);
            }
            finally {
                testReport.close();
            }
        }


        public List<ReleaseTest> getIgnoredTests() {
            return (extendedTest == null) ? null : extendedTest.getIgnoredTests();
        }
    }

    private class LoggerTest implements Test {
        private final Test realTest;


        LoggerTest(Test realTest) {
            this.realTest = realTest;
            if (this.realTest != null && (this.realTest instanceof ReleaseTest)) {
                ((ReleaseTest)this.realTest).setTestResport(testReport);
            }
        }


        public int countTestCases() {
            return realTest.countTestCases();
        }


        public void run(TestResult testResult) {
            final long begin = System.currentTimeMillis();
            testReport.logTestName(realTest.toString());
            testReport.logMemoryBeforeTest((double)Runtime.getRuntime().freeMemory(),
                                           (double)Runtime.getRuntime().totalMemory());
            realTest.run(testResult);
            testReport.logMemoryAfterTest((double)Runtime.getRuntime().freeMemory(),
                                          (double)Runtime.getRuntime().totalMemory());
            testReport.logTime(System.currentTimeMillis() - begin);
            testReport.flush();

            Enumeration errors = testResult.errors();
            while (errors.hasMoreElements()) {
                TestFailure testFailure = (TestFailure)errors.nextElement();
                buildErrorTestFile(testFailure.failedTest(), testFailure.thrownException());
            }
        }
    }
}
