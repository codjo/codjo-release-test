/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.gui;
import java.awt.Window;
import java.io.FileDescriptor;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import junit.extensions.jfcunit.TestHelper;
import junit.extensions.jfcunit.WindowMonitor;
import net.codjo.test.release.task.AgfTask;
import net.codjo.test.release.task.Resource;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;
public class GuiTask extends AgfTask implements Resource {
    private static final String GUI_SESSIONS = "gui.sessions";
    private GroupStep group = new GroupStep();
    private String user;
    private String password;
    private String gui;
    private String session;
    private String additionalArguments;


    @Override
    public void execute() {
        group.setName("gui-test");
        SecurityManager oldSecurityManager = System.getSecurityManager();
        System.setSecurityManager(new NeverExitSecurityManager(oldSecurityManager));
        try {
            if (session == null || "".equals(session)) {
                doExecuteNoSession();
            }
            else {
                doExecute();
            }
        }
        finally {
            System.setSecurityManager(oldSecurityManager);
        }
    }


    public void open() {
        if (!isConfigured()) {
            setUpConfiguration();
        }
    }


    public void close() {
        if (isConfigured()) {
            tearDownConfiguration();
        }
    }


    public String getUser() {
        return user;
    }


    public void setUser(String user) {
        this.user = user;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getGui() {
        return gui;
    }


    public void setGui(String gui) {
        this.gui = gui;
    }


    public String getSession() {
        return session;
    }


    public void setSession(String session) {
        this.session = session;
    }


    public String getAdditionalArguments() {
        return additionalArguments;
    }


    public void setAdditionalArguments(String additionalArguments) {
        this.additionalArguments = additionalArguments;
    }


    public GuiContext getGuiContext() {
        return getSessions().get(gui + "." + session);
    }


    public void setGuiContext(GuiContext guiContext) {
        getSessions().put(gui + "." + session, guiContext);
    }


    public String getClassPropertyName() {
        if (gui != null && !"".equals(gui)) {
            return GUI_PREFIX + "." + gui + "." + CLASS_SUFFIX;
        }
        return GUI_DEFAULT_CLASS;
    }


    public String getClassValue() {
        String classValue = getProperty(getClassPropertyName());
        if (classValue != null) {
            return classValue;
        }
        return getProperty(GUI_DEFAULT_CLASS);
    }


    public String getArgumentPropertyName() {
        if (gui != null && !"".equals(gui)) {
            return GUI_PREFIX + "." + gui + "." + ARG_SUFFIX;
        }
        return GUI_DEFAULT_ARG;
    }


    public String getArgumentValue(String argName) {
        String argValue = getProperty(getArgumentPropertyName() + "." + argName);
        if (argValue != null) {
            return argValue;
        }
        return getProperty(GUI_DEFAULT_ARG + "." + argName);
    }


    public void addGroup(GroupStep step) {
        group.addGroup(step);
    }


    protected String[] determineArguments() {
        String[] args = getDefaultArguments();
        args = overrideValuesIfNeeded(args);
        args = addAdditionalArguments(args);
        return args;
    }


    private void doExecuteNoSession() {
        StepPlayer stepPlayer = new StepPlayer(getProject(), getProperty(TEST_DIRECTORY, false));
        try {
            startClient();
            stepPlayer.play(group);
        }
        catch (BuildException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new GuiException("Impossible de démarrer l'IHM", ex);
        }
        finally {
            stepPlayer.cleanUp();
        }
    }


    private void doExecute() {
        GuiContext guiContext;
        try {
            guiContext = getGuiContext();
            if (guiContext == null) {
                startClient();
                guiContext = new GuiContext(
                      new StepPlayer(getProject(), getProperty(TEST_DIRECTORY, false)));
                setGuiContext(guiContext);
            }
            else {
                showClient(guiContext);
            }
        }
        catch (BuildException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new GuiException("Impossible de démarrer l'IHM", ex);
        }

        guiContext.getStepPlayer().play(group);
        hideClient(guiContext);
    }


    private Map<String, GuiContext> getSessions() {
        //noinspection unchecked
        return ((Map<String, GuiContext>)getReference(GUI_SESSIONS));
    }


    private boolean isConfigured() {
        return getSessions() != null;
    }


    private void setUpConfiguration() {
        TestHelper.setKeyMapping(new FrenchKeyMapping());
        addReference(GUI_SESSIONS, new LinkedHashMap());
    }


    private void tearDownConfiguration() {
        SecurityManager oldSecurityManager = System.getSecurityManager();
        System.setSecurityManager(new NeverExitSecurityManager(oldSecurityManager));
        try {
            for (GuiContext guiContext : getSessions().values()) {
                showClient(guiContext);
                guiContext.getStepPlayer().cleanUp();
            }
        }
        finally {
            System.setSecurityManager(oldSecurityManager);
        }

        removeReference(GUI_SESSIONS);
    }


    private void startClient() throws
                               ClassNotFoundException,
                               NoSuchMethodException,
                               IllegalAccessException,
                               InvocationTargetException {
        String classValue = getClassValue();

        String[] args = determineArguments();

        Class clazz = Class.forName(classValue);
        Method meth = clazz.getMethod("main", String[].class);
        meth.invoke(null, new Object[]{args});
    }


    private void showClient(GuiContext guiContext) {
        for (Window window : guiContext.getWindows()) {
            window.setVisible(true);
        }
    }


    private void hideClient(GuiContext guiContext) {
        List<Window> windows = new ArrayList<Window>();
        for (Window window : WindowMonitor.getWindows()) {
            if (window.isVisible()) {
                window.setVisible(false);
                windows.add(window);
            }
        }
        guiContext.getWindows().clear();
        guiContext.getWindows().addAll(windows);
    }


    private String[] overrideValuesIfNeeded(String[] args) {
        if (user != null) {
            args = ensureSize(args, 1);
            args[0] = user;
        }
        if (password != null) {
            args = ensureSize(args, 2);
            args[1] = password;
        }
        return args;
    }


    private String[] ensureSize(String[] args, int expectedSize) {
        if (args.length >= expectedSize) {
            return args;
        }
        String[] newArgs = new String[expectedSize];
        System.arraycopy(args, 0, newArgs, 0, args.length);
        return newArgs;
    }


    private String[] getDefaultArguments() {
        List<String> argsList = new ArrayList<String>();
        // Ajout des arguments nommés
        addNamedArguments(argsList);
        // Ajout des arguments ordonnés
        if (argsList.size() == 0) {
            addOrderedArguments(argsList);
        }
        return argsList.toArray(new String[argsList.size()]);
    }


    private void addNamedArguments(List<String> argsList) {
        boolean readNextArg = true;
        String[] argNames = new String[]{"user", "password", "server.host", "server.port", "specific"};
        for (String argName : argNames) {
            if (readNextArg) {
                String arg = getArgumentValue(argName);
                if (arg == null) {
                    readNextArg = false;
                }
                else {
                    argsList.add(arg);
                }
            }
        }
    }


    private void addOrderedArguments(List<String> argsList) {
        boolean readNextArg = true;
        for (int argIndex = 0; readNextArg; argIndex++) {
            String arg = getArgumentValue("" + argIndex);
            if (arg == null) {
                readNextArg = false;
            }
            else {
                argsList.add(arg);
            }
        }
    }


    private String[] addAdditionalArguments(String[] args) {
        Commandline commandline = new Commandline(getAdditionalArguments());
        List<String> result = new ArrayList<String>();
        result.addAll(Arrays.asList(args));
        result.addAll(Arrays.asList(commandline.getCommandline()));
        return result.toArray(new String[result.size()]);
    }


    public static class NeverExitSecurityManager extends SecurityManager {
        private SecurityManager oldSecurityManager;


        public NeverExitSecurityManager(SecurityManager oldSecurityManager) {
            this.oldSecurityManager = oldSecurityManager;
        }


        @Override
        public void checkExit(final int status) {
            throw new SecurityException("=== BLOCK SYSTEM.EXIT() ===") {
                /**
                 * @noinspection UseOfSystemOutOrSystemErr
                 */
                @Override
                public void printStackTrace() {
                    System.err.println("Block System.exit(" + status + ")");
                }


                @Override
                public void printStackTrace(PrintStream printStream) {
                }


                @Override
                public void printStackTrace(PrintWriter printWriter) {
                }
            };
        }


        @Override
        public void checkPermission(Permission perm) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkPermission(perm);
            }
        }


        @Override
        public void checkPermission(Permission perm, Object context) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkPermission(perm, context);
            }
        }


        @Override
        public void checkCreateClassLoader() {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkCreateClassLoader();
            }
        }


        @Override
        public void checkAccess(Thread thread) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkAccess(thread);
            }
        }


        @Override
        public void checkAccess(ThreadGroup threadGroup) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkAccess(threadGroup);
            }
        }


        @Override
        public void checkExec(String cmd) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkExec(cmd);
            }
        }


        @Override
        public void checkLink(String lib) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkLink(lib);
            }
        }


        @Override
        public void checkRead(FileDescriptor fd) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkRead(fd);
            }
        }


        @Override
        public void checkRead(String file) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkRead(file);
            }
        }


        @Override
        public void checkRead(String file, Object context) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkRead(file, context);
            }
        }


        @Override
        public void checkWrite(FileDescriptor fd) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkWrite(fd);
            }
        }


        @Override
        public void checkWrite(String file) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkWrite(file);
            }
        }


        @Override
        public void checkDelete(String file) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkDelete(file);
            }
        }


        @Override
        public void checkConnect(String host, int port) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkConnect(host, port);
            }
        }


        @Override
        public void checkConnect(String host, int port, Object context) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkConnect(host, port, context);
            }
        }


        @Override
        public void checkListen(int port) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkListen(port);
            }
        }


        @Override
        public void checkAccept(String host, int port) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkAccept(host, port);
            }
        }


        @Override
        public void checkMulticast(InetAddress maddr) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkMulticast(maddr);
            }
        }


        @Override
        public void checkMulticast(InetAddress maddr, byte ttl) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkMulticast(maddr, ttl);
            }
        }


        @Override
        public void checkPropertiesAccess() {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkPropertiesAccess();
            }
        }


        @Override
        public void checkPropertyAccess(String key) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkPropertyAccess(key);
            }
        }


        @Override
        public boolean checkTopLevelWindow(Object window) {
            return true;
        }


        @Override
        public void checkPrintJobAccess() {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkPrintJobAccess();
            }
        }


        @Override
        public void checkSystemClipboardAccess() {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkSystemClipboardAccess();
            }
        }


        @Override
        public void checkAwtEventQueueAccess() {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkAwtEventQueueAccess();
            }
        }


        @Override
        public void checkPackageAccess(String pkg) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkPackageAccess(pkg);
            }
        }


        @Override
        public void checkPackageDefinition(String pkg) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkPackageDefinition(pkg);
            }
        }


        @Override
        public void checkSetFactory() {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkSetFactory();
            }
        }


        @Override
        public void checkMemberAccess(Class clazz, int which) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkMemberAccess(clazz, which);
            }
        }


        @Override
        public void checkSecurityAccess(String target) {
            if (oldSecurityManager != null) {
                oldSecurityManager.checkSecurityAccess(target);
            }
        }
    }
}
