package net.codjo.test.release.task.imports;
import net.codjo.test.common.fixture.DirectoryFixture;
import net.codjo.test.release.task.AgfTask;
import net.codjo.test.release.task.util.RemoteCommand;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FilterSet;

public class CopyToInbox extends AgfTask {
    protected static final String DEFAULT_INBOX = "inbox";
    private String file;
    private String in;
    private List<Variable> variables = new ArrayList<Variable>();
    private RemoteCommand remoteCommand;


    public static File fileInTemp(Project project, File file) {
        return fileInTemp(project, file.getName());
    }


    public static File fileInTemp(Project project, String fileName) {
        return new File(defaultInboxDirectory(project), fileName);
    }


    public void setFile(String file) {
        this.file = file;
    }


    public String getFile() {
        return file;
    }


    public String getIn() {
        return in;
    }


    public void setIn(String in) {
        this.in = in;
    }


    public void addVariable(Variable var) {
        variables.add(var);
    }


    @Override
    public void execute() {
        info("Copying to Inbox file : " + getFile());
        copyAndFilter();

        if (getTestEnvironement().isRemoteMode()) {
            debug("\t FTP");
            try {
                getRemoteCommand().execute();
            }
            catch (BuildException e) {
                throw e;
            }
            catch (Exception e) {
                throw new BuildException("Impossible de copier le fichier", e);
            }
        }
        else {
            debug("\t LOCAL_SUFFIX");
        }
    }


    private RemoteCommand getRemoteCommand() {
        if (remoteCommand == null) {
            remoteCommand = new CopyToInboxSecureCommand(getRemoteUser(),
                                                         getRemoteServer(),
                                                         getPort(),
                                                         fileInTemp(getProject(),
                                                                    toAbsoluteFile(getFile())).getPath(),
                                                         determineRemoteInbox());
            remoteCommand.setLog(logger);
        }
        return remoteCommand;
    }


    void setRemoteCommand(RemoteCommand remoteCommand) {
        this.remoteCommand = remoteCommand;
    }


    private void copyAndFilter() {
        DirectoryFixture directory = new DirectoryFixture(determineFilteringInbox());
        if (!directory.exists()) {
            directory.mkdir();
        }

        Copy copy = new Copy();
        copy.setProject(getProject());
        copy.setTaskName("copy");
        copy.setFile(toAbsoluteFile(getFile()));
        copy.setTodir(directory.getAbsoluteFile());
        copy.setOverwrite(true);

        if (!variables.isEmpty()) {
            copy.setFiltering(true);
            FilterSet filterSet = copy.createFilterSet();
            for (Variable item : variables) {
                filterSet.addFilter(item.getName(), item.formatValue());
            }
        }

        copy.execute();
    }


    private static String defaultInboxDirectory(Project project) {
        File dir = new File(project.getBaseDir(), "target");
        return new File(dir.getPath(), DEFAULT_INBOX).getPath();
    }


    private String determineFilteringInbox() {
        if (getTestEnvironement().isRemoteMode() || getIn() == null || "".equals(getIn().trim())) {
            return defaultInboxDirectory(getProject());
        }
        else {
            return getProperty(getIn() + LOCAL_SUFFIX, true);
        }
    }


    String determineRemoteInbox() {
        if (getIn() != null && !"".equals(getIn().trim())) {
            return getProperty(getIn() + REMOTE_SUFFIX, true);
        }
        else {
            return getRemoteInDir();
        }
    }
}
