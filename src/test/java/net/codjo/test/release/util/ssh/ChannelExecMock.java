package net.codjo.test.release.util.ssh;
import net.codjo.test.common.LogString;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
/**
 *
 */
public class ChannelExecMock extends ChannelExec {
    private boolean closed = false;
    private LogString log;
    private boolean commandError = false;


    public ChannelExecMock(LogString log) {
        this.log = log;
    }


    @Override
    public void connect() throws JSchException {
        log.call("connect");
        closed = true;
        if (commandError) {
            throw new JSchException("Exception in ssh command");
        }
    }


    @Override
    public void disconnect() {
        log.call("disconnect");
    }


    @Override
    public void setCommand(String command) {
        log.call("setCommand", command);
    }


    @Override
    public void start() throws JSchException {
        log.call("start");
    }


    @Override
    public boolean isClosed() {
        return closed;
    }


    @Override
    public int getExitStatus() {
        if (commandError) {
            return 1;
        }
        return 0;
    }


    public void mockCommandError() {
        commandError = true;
    }
}
