package net.codjo.test.release.task.batch;
import net.codjo.test.release.task.util.RemoteCommand;
import java.util.List;

public class BatchTask extends AbstractBatchTask {
    private Remote remote = new Remote();
    private Local local = new Local();
    private int returnCode = 0;


    @Override
    public int getReturnCode() {
        return returnCode;
    }


    @Override
    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }


    public Remote getRemote() {
        return remote;
    }


    public void addRemote(Remote newRemote) {
        this.remote = newRemote;
    }


    public Local getLocal() {
        return local;
    }


    public void addLocal(Local newLocal) {
        this.local = newLocal;
    }


    @Override
    protected String getBatchStartClass() {
        return local.getMain();
    }


    @Override
    protected String getExecuteDescription() {
        return "batch ...";
    }


    @Override
    protected RemoteCommand createRemoteCommand() {
        return new BatchSecureCommand(getProperty(REMOTE_USER, true),
                                      getProperty(REMOTE_SERVER, true),
                                      getRemoteBatchDir(),
                                      remote.getScript(),
                                      getProject().replaceProperties(remote.getArg()));
    }


    @Override
    protected List<String> buildLocalArguments() {
        return argumentsToList(getProject().replaceProperties(local.getArg()));
    }


    public void setRemote(boolean remote) {
        setRemoteEnabled(remote);
    }


    public static class Local {
        private String main;
        private String arg;


        public String getMain() {
            return main;
        }


        public void setMain(String main) {
            this.main = main;
        }


        public String getArg() {
            return arg;
        }


        public void setArg(String arg) {
            this.arg = arg;
        }
    }

    public static class Remote {
        private String script;
        private String arg;


        public String getScript() {
            return script;
        }


        public void setScript(String script) {
            this.script = script;
        }


        public String getArg() {
            return arg;
        }


        public void setArg(String arg) {
            this.arg = arg;
        }
    }
}
