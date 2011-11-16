package net.codjo.test.release.task.imports;
import net.codjo.test.release.util.ssh.ChannelSftpMock;
import net.codjo.test.release.util.ssh.SecureCommandTestCase;
import com.jcraft.jsch.ChannelSftp;
import org.junit.Test;
import static org.mockito.Mockito.verify;
/**
 *
 */
public class CopyToInboxSecureCommandTest extends SecureCommandTestCase {

    @Override
    protected CopyToInboxSecureCommand createSecureCommand() {
        return new CopyToInboxSecureCommand("my-user", "my-host", "my-source", "my-destination");
    }


    @Test
    public void test_execute() throws Exception {
        getSessionFactoryMock().mockChannel("sftp", new ChannelSftpMock(getLog()));

        getSecureCommand().execute();

        getLog().assertContent("connect()",
                               "cd(my-destination)",
                               "put(my-source, ., " + ChannelSftp.OVERWRITE + ")",
                               "disconnect()");

        verify(getSessionFactoryMock().getSessionMock()).connect();
        verify(getSessionFactoryMock().getSessionMock()).disconnect();
    }
}
