package net.codjo.test.release.util.ssh;
import static net.codjo.test.release.util.ssh.SshTestConstants.SAM_HOST;
import static net.codjo.test.release.util.ssh.SshTestConstants.SAM_LOGIN;
import static net.codjo.test.release.util.ssh.SshTestConstants.SAM_PROJECT_DIR;
import static org.junit.Assert.assertEquals;
import net.codjo.test.release.util.ssh.SshTestConstants.SAMChecker;

import org.junit.Test;
import org.junit.runner.RunWith;

import sun.security.action.GetLongAction;

import com.googlecode.junit.ext.JunitExtRunner;
import com.googlecode.junit.ext.RunIf;
import com.googlecode.junit.ext.checkers.OSChecker;
/**
 *
 */
@RunWith(JunitExtRunner.class)
public class ChannelExecSecureCommandTest {
    private ChannelExecSecureCommand channelExecSecureCommand
          = new ChannelExecSecureCommand(SAM_LOGIN, SAM_HOST, SAM_PROJECT_DIR, "return 5") {
    };


    @Test
    @RunIf(value = SAMChecker.class)
    public void test_returnCode() throws Exception {
        assertEquals(5, channelExecSecureCommand.execute());
    }
}
