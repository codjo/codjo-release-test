package net.codjo.test.release.util.ssh;
import com.googlecode.junit.ext.JunitExtRunner;
import com.googlecode.junit.ext.RunIf;
import net.codjo.test.release.util.ssh.SshTestConstants.SAMChecker;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.codjo.test.release.util.ssh.ChannelExecSecureCommand.DEFAULT_SSH_PORT;
import static net.codjo.test.release.util.ssh.SshTestConstants.SAM_HOST;
import static net.codjo.test.release.util.ssh.SshTestConstants.SAM_LOGIN;
import static net.codjo.test.release.util.ssh.SshTestConstants.SAM_PROJECT_DIR;
import static org.junit.Assert.assertEquals;
/**
 *
 */
@RunWith(JunitExtRunner.class)
public class ChannelExecSecureCommandTest {
    private ChannelExecSecureCommand channelExecSecureCommand
          = new ChannelExecSecureCommand(SAM_LOGIN, SAM_HOST, DEFAULT_SSH_PORT, SAM_PROJECT_DIR, "return 5") {
    };


    @Test
    public void test_magicEuropean() throws Exception {
        ChannelExecSecureCommand toto = new ChannelExecSecureCommand("magdev",
                                                                     "afzda120.intradit.net",
                                                                     2222,
                                                                     "/global/dmig/MAGIC/",
                                                                     "$(exit 5)") {
        };
        assertEquals(5, toto.execute());
    }


    @Test
    @RunIf(value = SAMChecker.class)
    public void test_returnCode() throws Exception {
        assertEquals(5, channelExecSecureCommand.execute());
    }
}
