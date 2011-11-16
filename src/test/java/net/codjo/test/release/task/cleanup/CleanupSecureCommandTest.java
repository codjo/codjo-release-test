package net.codjo.test.release.task.cleanup;
import net.codjo.test.release.util.ssh.ChannelExecMock;
import net.codjo.test.release.util.ssh.SecureCommand;
import net.codjo.test.release.util.ssh.SecureCommandTestCase;
import org.junit.Test;
import static org.mockito.Mockito.verify;
/**
 *
 */
public class CleanupSecureCommandTest extends SecureCommandTestCase {

    @Override
    protected SecureCommand createSecureCommand() {
        return new CleanupSecureCommand("my-user", "my-host", "my-destination");
    }


    @Test
    public void test_execute() throws Exception {
        getSessionFactoryMock().mockChannel("exec", new ChannelExecMock(getLog()));

        getSecureCommand().execute();

        getLog().assertContent("setCommand(. ~/.profile;cd my-destination;rm -rf *)",
                               "connect()",
                               "disconnect()");

        verify(getSessionFactoryMock().getSessionMock()).connect();
        verify(getSessionFactoryMock().getSessionMock()).disconnect();
    }
}
