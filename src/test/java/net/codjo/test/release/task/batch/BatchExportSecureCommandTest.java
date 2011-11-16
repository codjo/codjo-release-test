package net.codjo.test.release.task.batch;
import net.codjo.test.release.util.ssh.ChannelExecMock;
import net.codjo.test.release.util.ssh.SecureCommand;
import net.codjo.test.release.util.ssh.SecureCommandTestCase;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.mockito.Mockito.verify;
/**
 *
 */
public class BatchExportSecureCommandTest extends SecureCommandTestCase {

    @Override
    protected SecureCommand createSecureCommand() {
        return new BatchExportSecureCommand("my-user",
                                            "my-host",
                                            "my-batch-directory",
                                            "my-initiator",
                                            "my-file-to-export",
                                            "my-date",
                                            false,
                                            "extra-arg1 extra-arg2");
    }


    @Test
    public void test_execute() throws Exception {
        getSessionFactoryMock().mockChannel("exec", new ChannelExecMock(getLog()));

        getSecureCommand().execute();

        getLog().assertContent(
              "setCommand(. ~/.profile;cd my-batch-directory;./export.ksh my-initiator my-file-to-export my-date sans_unix2dos extra-arg1 extra-arg2)",
              "connect()",
              "disconnect()");

        verify(getSessionFactoryMock().getSessionMock()).connect();
        verify(getSessionFactoryMock().getSessionMock()).disconnect();
    }


    @Test
    public void test_constructor() throws Exception {
        BatchExportSecureCommand command = new BatchExportSecureCommand("my-user",
                                                                        "my-host",
                                                                        "my-batch-directory",
                                                                        "my-initiator",
                                                                        "my-file-to-export",
                                                                        "my-date",
                                                                        false,
                                                                        null);

        assertEquals(
              "./export.ksh my-initiator my-file-to-export my-date sans_unix2dos",
              command.getCommand());

        command = new BatchExportSecureCommand("my-user",
                                               "my-host",
                                               "my-batch-directory",
                                               "my-initiator",
                                               "my-file-to-export",
                                               "my-date",
                                               true,
                                               null);

        assertEquals(
              "./export.ksh my-initiator my-file-to-export my-date unix2dos",
              command.getCommand());

        command = new BatchExportSecureCommand("my-user",
                                               "my-host",
                                               "my-batch-directory",
                                               "my-initiator",
                                               "my-file-to-export",
                                               "my-date",
                                               true,
                                               "extra-arg1 extra-arg2");

        assertEquals(
              "./export.ksh my-initiator my-file-to-export my-date unix2dos extra-arg1 extra-arg2",
              command.getCommand());
    }
}
