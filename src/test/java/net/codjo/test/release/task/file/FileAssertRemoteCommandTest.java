package net.codjo.test.release.task.file;
import net.codjo.test.release.util.ssh.ChannelSftpMock;
import net.codjo.test.release.util.ssh.SecureCommandTestCase;
import org.junit.Test;
/**
 *
 */
public class FileAssertRemoteCommandTest extends SecureCommandTestCase {

    @Override
    protected FileAssertRemoteCommand createSecureCommand() {
        return new FileAssertRemoteCommand("my-user",
                                           "my-host",
                                           "my-remote-directory",
                                           "my-local-directory",
                                           "my-file-to-get");
    }


    @Test
    public void test_execute() throws Exception {
        getSessionFactoryMock().mockChannel("sftp", new ChannelSftpMock(getLog()));

        assertExecuteOk("connect()",
                        "cd(my-remote-directory)",
                        "get(my-file-to-get, my-local-directory)",
                        "rm(my-file-to-get)",
                        "disconnect()");
    }
}
