package net.codjo.test.release.util.ssh;
import static net.codjo.test.release.util.ssh.SshTestConstants.SAM_HOST;
import static net.codjo.test.release.util.ssh.SshTestConstants.SAM_LOGIN;
import static net.codjo.test.release.util.ssh.SshTestConstants.SAM_PROJECT_DIR;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
/**
 *
 */
public class ChannelExecSecureCommandTest {
    private ChannelExecSecureCommand channelExecSecureCommand
          = new ChannelExecSecureCommand(SAM_LOGIN, SAM_HOST, SAM_PROJECT_DIR, "return 5") {
    };


    @Test
    public void test_returnCode() throws Exception {
        assertEquals(5, channelExecSecureCommand.execute());
    }
}
