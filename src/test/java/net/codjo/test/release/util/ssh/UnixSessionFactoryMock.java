package net.codjo.test.release.util.ssh;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
/**
 *
 */
public class UnixSessionFactoryMock extends UnixSessionFactory {
    private String channelType;
    private Channel mockedChannel;
    private Session sessionMock;
    private boolean invalidSession = false;
    private boolean commandError = false;


    public UnixSessionFactoryMock(String user, String host, int port) {
        super(user, host, port);
    }


    @Override
    public Session createSession() throws JSchException {
        if (invalidSession) {
            throw new JSchException("Exception in createSession");
        }

        sessionMock = mock(Session.class);

        Channel channel;
        if (commandError) {
            channel = new ChannelInError();
        }
        else {
            channel = this.mockedChannel;
        }

        when(sessionMock.openChannel(channelType)).thenReturn(channel);
        return sessionMock;
    }


    public Session getSessionMock() {
        return sessionMock;
    }


    public void mockChannel(String type, Channel channel) {
        this.channelType = type;
        this.mockedChannel = channel;
    }


    public void mockInvalidSession() {
        invalidSession = true;
    }


    public void mockCommandError() {
        commandError = true;
    }


    private static class ChannelInError extends ChannelExec {
        @Override
        public void connect() throws JSchException {
            throw new JSchException("Exception in connect");
        }
    }
}
