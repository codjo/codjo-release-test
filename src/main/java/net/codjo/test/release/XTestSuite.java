package net.codjo.test.release;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestSuite;
/**
 * Extended {@link TestSuite} providing additional informations by implementing the {@link XTest} interface.
 */
public class XTestSuite extends TestSuite implements XTest {
    private List<ReleaseTest> ignoredTests = new ArrayList<ReleaseTest>();


    public List<ReleaseTest> getIgnoredTests() {
        return ignoredTests;
    }


    public void addIgnoredTest(ReleaseTest releaseTest) {
        ignoredTests.add(releaseTest);
    }
}
