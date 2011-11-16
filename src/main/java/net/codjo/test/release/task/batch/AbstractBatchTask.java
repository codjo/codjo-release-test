package net.codjo.test.release.task.batch;
import net.codjo.test.common.fixture.SystemExitFixture;
import net.codjo.test.release.task.AgfTask;
import net.codjo.test.release.task.Arg;
import net.codjo.test.release.task.util.RemoteCommand;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;

abstract class AbstractBatchTask extends AgfTask {
    public static final String BATCH_CONFIGURATION = "batch.configuration";
    public static final String BATCH_START_CLASS = "batch.start.class";
    private final List<Arg> args = new ArrayList<Arg>();
    private RemoteCommand remoteCommand;

    private boolean remoteEnabled = true;

    public List<Arg> getArgs() {
        return args;
    }


    public void addArg(Arg arg) {
        args.add(arg);
    }


    public abstract int getReturnCode();


    public abstract void setReturnCode(int returnCode);


    protected void setRemoteEnabled(boolean remoteEnabled) {
        this.remoteEnabled = remoteEnabled;
    }


    protected RemoteCommand getRemoteCommand() {
        if (remoteCommand == null) {
            remoteCommand = createRemoteCommand();
            remoteCommand.setLog(logger);
        }
        return remoteCommand;
    }


    public void setRemoteCommand(RemoteCommand remoteCommand) {
        this.remoteCommand = remoteCommand;
    }


    protected abstract RemoteCommand createRemoteCommand();


    protected String getBatchStartClass() {
        return getProperty(AbstractBatchTask.BATCH_START_CLASS);
    }


    protected abstract String getExecuteDescription();


    @Override
    public final void execute() {
        info(getExecuteDescription());
        try {
            if (getTestEnvironement().isRemoteMode() && remoteEnabled) {
                debug("\tRemote");
                doRemote();
            }
            else {
                debug("\tLocal");
                doLocal();
            }
        }
        catch (BuildException e) {
            throw e;
        }
        catch (Exception e) {
            throw new BuildException(getExecuteDescription() + " en erreur", e);
        }
    }


    protected void doRemote() throws Exception {
        int actualReturnCode = getRemoteCommand().execute();
        if (getReturnCode() != actualReturnCode) {
            throw new BuildException(String.format("Erreur dans l'exécution du Batch, code retour : "
                                                   + "expected > %s < actual > %s <",
                                                   getReturnCode(),
                                                   actualReturnCode));
        }
    }


    protected void doLocal() {
        List<String> arguments = buildLocalArguments();

        SystemExitFixture systemExitFixture = new SystemExitFixture();
        systemExitFixture.doSetUp();
        try {
            Class clazz = Class.forName(getBatchStartClass());
            Method meth = clazz.getMethod("main", String[].class);
            meth.invoke(null, new Object[]{arguments.toArray(new String[arguments.size()])});
        }
        catch (InvocationTargetException e) {
            if (!(e.getTargetException() instanceof SecurityException)) {
                throw new BuildException(e.getTargetException());
            }
        }
        catch (Throwable e) {
            throw new BuildException(e);
        }
        finally {
            systemExitFixture.doTearDown();
        }

        if (systemExitFixture.getFirstExitValue() != getReturnCode()) {
            throw new BuildException(String.format("Erreur dans l'exécution du Batch, code retour : "
                                                   + "expected > %s < actual > %s <",
                                                   getReturnCode(),
                                                   systemExitFixture.getFirstExitValue()));
        }
    }


    protected abstract List<String> buildLocalArguments();


    protected String argumentsToString(List<Arg> arguments) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Arg arg : arguments) {
            stringBuilder.append(arg.getLine()).append(" ");
        }
        return getProject().replaceProperties(stringBuilder.toString()).trim();
    }


    protected List<String> argumentsToList(String arguments) {
        List<String> list = new ArrayList<String>();
        StringTokenizer strTokenizer = new StringTokenizer(arguments, "\"", false);
        boolean even = false;
        while (strTokenizer.hasMoreElements()) {
            String token = (String)strTokenizer.nextElement();
            if (even) {
                list.add(token);
            }
            else {
                StringTokenizer argTokenizer = new StringTokenizer(token, " ", false);
                while (argTokenizer.hasMoreElements()) {
                    list.add((String)argTokenizer.nextElement());
                }
            }
            even = !even;
        }
        return list;
    }
}