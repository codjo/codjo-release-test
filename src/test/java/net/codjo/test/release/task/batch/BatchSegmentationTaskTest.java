package net.codjo.test.release.task.batch;
import net.codjo.test.release.ReleaseTestRunner;
import net.codjo.test.release.task.Arg;
import net.codjo.test.release.task.util.RemoteCommandMock;
import java.io.File;
import java.util.Arrays;
import org.apache.tools.ant.BuildException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
/**
 *
 */
public class BatchSegmentationTaskTest extends BatchTestCase<BatchSegmentationTask> {

    @Override
    protected BatchSegmentationTask createBatchTask() {
        BatchSegmentationTask segmentationTask = new BatchSegmentationTask();
        segmentationTask.setInitiator("myUser");
        return segmentationTask;
    }


    @Test
    public void test_antGeneration() throws Exception {
        File releaseTestFile = new File(getClass().getResource("BatchSegmentationTask.xml").getFile());

        try {
            ReleaseTestRunner.executeTestFile(releaseTestFile.getParentFile(), releaseTestFile);
            fail();
        }
        catch (BuildException e) {
            assertEquals("manque argument batch.configuration dans le fichier de configuration",
                         e.getMessage());
        }
    }


    @Test
    public void test_getRemoteCommand() throws Exception {
        project.setProperty("variable2", "valeur2");

        batchTask.setSegmentations("1, 2");
        batchTask.setDate("myDate");
        batchTask.addExtraArg(new Arg("-arg1 value1 -arg2 ${variable2}"));
        batchTask.addExtraArg(new Arg("-arg3 valeur3"));

        assertTrue(batchTask.getRemoteCommand() instanceof BatchSegmentationSecureCommand);
        assertEquals("myProjectDir/APP1/BATCH",
                     ((BatchSegmentationSecureCommand)batchTask.getRemoteCommand()).getWorkingDir());

        assertEquals(
              "./segmentation.ksh myUser \"1, 2\" myDate -arg1 value1 -arg2 valeur2 -arg3 valeur3",
              ((BatchSegmentationSecureCommand)batchTask.getRemoteCommand()).getCommand());
    }


    @Test
    public void test_getRemoteCommand_withoutExtraArgs() throws Exception {
        project.setProperty("variable2", "valeur2");

        batchTask.setSegmentations("1, 2");
        batchTask.setDate("myDate");

        assertTrue(batchTask.getRemoteCommand() instanceof BatchSegmentationSecureCommand);
        assertEquals("./segmentation.ksh myUser \"1, 2\" myDate",
                     ((BatchSegmentationSecureCommand)batchTask.getRemoteCommand()).getCommand());
    }


    @Test
    public void test_executeLocal() throws Exception {
        project.setProperty("variable2", "value2");

        batchTask.setSegmentations("1, 2");
        batchTask.setDate("myDate");
        batchTask.addExtraArg(new Arg("-arg1 value1 -arg2 ${variable2}"));
        batchTask.addExtraArg(new Arg("-arg3 value3"));

        batchTask.execute();

        BatchMainMock.assertLog(
              "main("
              + "-initiator, myUser, "
              + "-type, segmentation, "
              + "-segmentations, 1, 2, "
              + "-configuration, myBatchConfiguration, "
              + "-date, myDate, "
              + "-arg1, value1, "
              + "-arg2, value2, "
              + "-arg3, value3)");
    }


    @Test
    public void test_executeLocal_noExtraArgs() throws Exception {
        batchTask.setSegmentations("1, 2");
        batchTask.setDate("myDate");

        batchTask.execute();

        BatchMainMock.assertLog(
              "main("
              + "-initiator, myUser, "
              + "-type, segmentation, "
              + "-segmentations, 1, 2, "
              + "-configuration, myBatchConfiguration, "
              + "-date, myDate)");
    }


    @Test
    public void test_executeRemote() throws Exception {
        project.setProperty("agf.test.remote", "YES");
        batchTask.setRemoteCommand(new RemoteCommandMock(log));

        batchTask.execute();

        log.assertContent("execute()");
    }


    @Test
    public void test_buildExtraLocalArguments() throws Exception {
        project.setProperty("variable", "uneValeur");
        batchTask.addExtraArg(new Arg(
              "-qqchose ${variable} -file toto.txt -user \"jean michel\"   -c'est_fini"));

        assertEquals(Arrays.asList("-qqchose",
                                   "uneValeur",
                                   "-file",
                                   "toto.txt",
                                   "-user",
                                   "jean michel",
                                   "-c'est_fini"),
                     batchTask.argumentsToList(batchTask.argumentsToString(batchTask.getArgs())));
    }


    @Test
    public void test_buildExtraLocalArguments_mustReturnEmptyList() throws Exception {
        assertTrue(batchTask.argumentsToList(batchTask.argumentsToString(batchTask.getArgs())).isEmpty());
    }


    @Test
    public void test_buildExtraRemoteArguments() throws Exception {
        project.setProperty("variable", "uneValeur");

        batchTask.addExtraArg(new Arg(
              "-qqchose ${variable} -file toto.txt -user \"jean michel\"   -c'est_fini"));

        assertEquals("-qqchose uneValeur -file toto.txt -user \"jean michel\"   -c'est_fini",
                     batchTask.argumentsToString(batchTask.getArgs()));
    }


    @Test
    public void test_buildExtraRemoteArguments_mustReturnEmpty() throws Exception {
        assertEquals("", batchTask.argumentsToString(batchTask.getArgs()));
    }


    @Test
    public void test_executeRemote_mustFail() throws Exception {
        project.setProperty("agf.test.remote", "YES");
        RemoteCommandMock commandMock = new RemoteCommandMock(log);
        commandMock.mockFailure();
        batchTask.setRemoteCommand(commandMock);

        batchTask.setInitiator("myUser");

        try {
            batchTask.execute();
        }
        catch (Exception e) {
            assertEquals("batch-segmentation initiator >myUser< segmentations >null<... en erreur",
                         e.getMessage());
        }
    }
}
