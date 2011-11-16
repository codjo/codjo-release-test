package net.codjo.test.release.util.ssh;
import net.codjo.test.common.LogString;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
/**
 *
 */
public class ChannelSftpMock extends ChannelSftp {
    private LogString log;
    private String commandInError;


    public ChannelSftpMock(LogString log) {
        this.log = log;
    }


    @Override
    public void connect() throws JSchException {
        log.call("connect");
    }


    @Override
    public void disconnect() {
        log.call("disconnect");
    }


    @Override
    public void cd(String destination) throws SftpException {
        logCallWithError("cd", destination);
    }


    @Override
    public void get(String source, String destination) throws SftpException {
        logCallWithError("get", source, destination);
    }


    @Override
    public void put(String source, String destination, int mode) throws SftpException {
        logCallWithError("put", source, destination, mode);
    }


    @Override
    public void rm(String file) throws SftpException {
        logCallWithError("rm", file);
    }


    public void mockCommandInError(String command) {
        commandInError = command;
    }


    private void logCallWithError(String command, Object... parameters) throws SftpException {
        log.call(command, parameters);
        if (command.equals(commandInError)) {
            throw new SftpException(1, String.format("Exception with %s sftp command", commandInError));
        }
    }
}
