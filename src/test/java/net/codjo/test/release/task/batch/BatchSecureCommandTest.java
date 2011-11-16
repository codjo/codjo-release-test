package net.codjo.test.release.task.batch;
import net.codjo.test.release.util.ssh.ChannelExecMock;
import net.codjo.test.release.util.ssh.SecureCommandTestCase;
import org.junit.Test;
import static org.mockito.Mockito.verify;
/**
 *
 */
public class BatchSecureCommandTest extends SecureCommandTestCase<BatchSecureCommand> {

    @Override
    protected BatchSecureCommand createSecureCommand() {
        return new BatchSecureCommand("my-user",
                                      "my-host",
                                      "my-batch-directory",
                                      "my-script.ksh",
                                      "-arg1 value1 -arg2 value2");
    }


    @Test
    public void test_execute() throws Exception {
        getSessionFactoryMock().mockChannel("exec", new ChannelExecMock(getLog()));

        getSecureCommand().execute();

        getLog().assertContent(
              "setCommand(. ~/.profile;cd my-batch-directory;./my-script.ksh -arg1 value1 -arg2 value2)",
              "connect()",
              "disconnect()");

        verify(getSessionFactoryMock().getSessionMock()).connect();
        verify(getSessionFactoryMock().getSessionMock()).disconnect();
    }
}
