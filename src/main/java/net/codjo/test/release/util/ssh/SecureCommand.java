package net.codjo.test.release.util.ssh;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;
import org.apache.tools.ant.util.TaskLogger;
/**
 *
 */
public abstract class SecureCommand<T extends Channel> {
    private final String channelType;
    private final String user;
    private final String host;
    private TaskLogger log;
    private UnixSessionFactory unixSessionFactory;


    protected SecureCommand(String channelType, String user, String host) {
        this.channelType = channelType;
        this.user = user;
        this.host = host;
    }


    public String getUser() {
        return user;
    }


    public String getHost() {
        return host;
    }


    public TaskLogger getLog() {
        return log;
    }


    public void setLog(TaskLogger log) {
        this.log = log;
    }


    public UnixSessionFactory getUnixSessionFactory() {
        if (unixSessionFactory == null) {
            unixSessionFactory = new UnixSessionFactory(user, host);
        }
        return unixSessionFactory;
    }


    public void setUnixSessionFactory(UnixSessionFactory unixSessionFactory) {
        this.unixSessionFactory = unixSessionFactory;
    }


    public final int execute() throws Exception {
        Session session = getUnixSessionFactory().createSession();
        session.connect();
        try {
            @SuppressWarnings({"unchecked"}) T channel = (T)session.openChannel(channelType);
            configure(channel);
            channel.connect();
            try {
                execute(channel);
                return channel.getExitStatus();
            }
            finally {
                channel.disconnect();
            }
        }
        finally {
            session.disconnect();
        }
    }


    protected void configure(T channel) {
    }


    protected void execute(T channel) throws Exception {
    }


    protected void debug(String message) {
        if (log == null) {
            return;
        }
        log.debug(message);
    }


    protected void info(String message) {
        if (log == null) {
            return;
        }
        log.info(message);
    }


    protected void error(String error) {
        if (log == null) {
            return;
        }
        log.error(error);
    }
}
