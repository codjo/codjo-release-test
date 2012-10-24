package net.codjo.test.release;
import java.util.List;
import junit.framework.Test;

/**
 *
 */
public interface XTest extends Test {
    /**
     * Get the list of ignored tests.
     * @return
     */
    List<ReleaseTest> getIgnoredTests();
}
