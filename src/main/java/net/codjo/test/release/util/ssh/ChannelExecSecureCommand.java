package net.codjo.test.release.util.ssh;
import com.jcraft.jsch.ChannelExec;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.tools.ant.BuildException;

public abstract class ChannelExecSecureCommand extends SecureCommand<ChannelExec> {
    private final String workingDir;
    private final String command;
    private int timeout = 60 * 1000;


    protected ChannelExecSecureCommand(String user, String host, String workingDir, String command) {
        super("exec", user, host);
        this.workingDir = workingDir;
        this.command = command;
    }


    public String getWorkingDir() {
        return workingDir;
    }


    public String getCommand() {
        return command;
    }


    public int getTimeout() {
        return timeout;
    }


    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }


    @Override
    protected void configure(ChannelExec channel) {
        String shellCommand = ". ~/.profile;cd " + workingDir + ";" + command;
        info("[ssh] " + shellCommand);

        channel.setCommand(shellCommand);
    }


    @Override
    protected void execute(final ChannelExec channel) throws Exception {
        final StringBuilder error = new StringBuilder();
        channel.setErrStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                error.append((char)b);
            }
        });
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (!channel.isClosed()) {
                    try {
                        sleep(500);
                    }
                    catch (Exception e) {
                        // ignored
                    }
                }
            }
        };
        thread.start();
        thread.join(timeout);

        if (thread.isAlive()) {
            // ran out of time
            throw new BuildException("Remote command timeout");
        }
        else {
            int ec = channel.getExitStatus();
            if (ec != 0) {
                error(String.format("Remote command failed with exit status %s and error message %s",
                                    ec,
                                    error));
            }
        }
    }
}