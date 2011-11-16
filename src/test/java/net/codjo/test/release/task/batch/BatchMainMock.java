package net.codjo.test.release.task.batch;
import net.codjo.test.common.LogString;
/**
 *
 */
public class BatchMainMock {
    private static final LogString log = new LogString();
    @SuppressWarnings({"StaticNonFinalField"})
    private static int returnCode = 0;


    private BatchMainMock() {
    }


    public static void main(String[] args) {
        log.clear();
        log.call("main", args);

        int currentReturnCode = returnCode;
        returnCode = 0;

        System.exit(currentReturnCode);
    }


    public static void assertLog(String expectedContent) {
        log.assertContent(expectedContent);
    }


    @SuppressWarnings({"ParameterHidesMemberVariable"})
    public static void mockReturnCode(int returnCode) {
        BatchMainMock.returnCode = returnCode;
    }
}
