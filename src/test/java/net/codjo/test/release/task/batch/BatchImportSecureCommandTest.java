package net.codjo.test.release.task.batch;
import net.codjo.test.release.util.ssh.ChannelExecMock;
import net.codjo.test.release.util.ssh.SecureCommand;
import net.codjo.test.release.util.ssh.SecureCommandTestCase;
import org.junit.Test;
import static org.mockito.Mockito.verify;
/**
 *
 */
public class BatchImportSecureCommandTest extends SecureCommandTestCase {

    @Override
    protected SecureCommand createSecureCommand() {
        return new BatchImportSecureCommand("my-user",
                                            "my-host",
                                            "my-batch-directory",
                                            "my-initiator",
                                            "my-file-to-import");
    }


    @Test
    public void test_execute() throws Exception {
        getSessionFactoryMock().mockChannel("exec", new ChannelExecMock(getLog()));

        getSecureCommand().execute();

        getLog().assertContent(
              "setCommand(. ~/.profile;cd my-batch-directory;./import.ksh my-initiator my-file-to-import)",
              "connect()",
              "disconnect()");

        verify(getSessionFactoryMock().getSessionMock()).connect();
        verify(getSessionFactoryMock().getSessionMock()).disconnect();
    }
}
