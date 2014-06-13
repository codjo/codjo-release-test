package net.codjo.test.release.task.security;
import net.codjo.test.release.task.AgfTask;
import net.codjo.test.release.task.util.RemoteCommand;
import net.codjo.test.release.util.ssh.ChannelSftpSecureCommand;
import net.codjo.util.file.FileUtil;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
/**
 *
 */
public class SecurityModelTask extends AgfTask {
    public static final String UNDEFINED_USER_ERROR
          = "L'attribut user est obligatoire (balise security-model)";

    private String user;
    private String roles;
    private String lastLogin;
    private String lastLogout;
    static final String SECURITY_MODEL_STORAGE_TYPE = "securityModel.storageType";
    static final String SECURITY_MODEL_STORAGE_FILE = "securityModel.storageFile";
    private static final String UNDEFINED_STORAGE_FILE_ERROR =
          "L'attribut '" + SECURITY_MODEL_STORAGE_FILE + "' est obligatoire si l'attribut '"
          + SECURITY_MODEL_STORAGE_TYPE + "' est de type 'file'.";
    private RemoteCommand remoteCommand;
    private static final String DEFAULT_DIR = "securityModel";


    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }


    public void setLastLogout(String lastLogout) {
        this.lastLogout = lastLogout;
    }


    public void setUser(String user) {
        this.user = user;
    }


    public void setRoles(String roles) {
        this.roles = roles;
    }


    @Override
    public void execute() {
        if ((user == null) || ("".equals(user.trim()))) {
            throw new BuildException(UNDEFINED_USER_ERROR);
        }
        info("Insertion du modèle de sécurité : user=" + user + " roles=" + roles);
        final String storageType = getProperty(SECURITY_MODEL_STORAGE_TYPE);
        if ("file".equals(storageType)) {
            info(" - type de stockage du modèle de sécurité = fichier");
            insertInFileStorageSecurityModel();
        }
        else {
            info(" - type de stockage du modèle de sécurité = base de donnee");
            insertInDatabaseSecurityModel();
        }
    }


    private void insertInFileStorageSecurityModel() {
        final String storageFile = getProperty(SECURITY_MODEL_STORAGE_FILE);
        if ((storageFile == null) || ("".equals(storageFile.trim()))) {
            throw new BuildException(UNDEFINED_STORAGE_FILE_ERROR);
        }
        File targetFile = new File(storageFile);
        try {
            if (!targetFile.exists()) {
                targetFile.getParentFile().mkdirs();
                targetFile.createNewFile();
            }
            FileUtil.saveContent(targetFile, "<xml version=\"0\">" + createSecurityModel() + "</xml>");
            info(" - sauvegarde dans le fichier "+ targetFile.getAbsolutePath());
        }
        catch (IOException e) {
            throw new BuildException(e);
        }

        if (getTestEnvironement().isRemoteMode()) {
            info(" - copie du fichier de configuration en mode remote");            
            debug("\t FTP");
            try {
                File destFile = new File(defaultSecurityModelDirectory(getProject()), targetFile.getName());
                if (!destFile.getParentFile().exists()) {
                    destFile.getParentFile().mkdirs();
                }
                FileUtil.copyFile(targetFile, destFile);
                getRemoteCommand(targetFile.getCanonicalPath()).execute();
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


    private void insertInDatabaseSecurityModel() {
        try {
            Connection connection = getConnection();
            connection.createStatement().executeUpdate("delete from PM_SEC_MODEL");
            PreparedStatement statement = connection.prepareStatement(
                  "insert into PM_SEC_MODEL (VERSION, MODEL) values (0, ?)");
            statement.setString(1, createSecurityModel());

            // BUGFIX :
            // Sous Sybase, la précision du datetime est de l'ordre de 1/300s.
            // Lors de l'insertion dans PM_SEC_MODEL, la colonne LAST_UPDATE est renseignée via getdate().
            // Deux insertions rapides dans PM_SEC_MODEL peuvent renseigner deux MODEL différents mais
            // avec une même LAST_UPDATE.
            // Afin d'être sur qu'elles soient différentes,
            //  on simule une petite pause avant et après insertions.
            try {
                Thread.sleep(10);
                statement.execute();
                Thread.sleep(10);
            }
            catch (InterruptedException e) {
                // Rien à faire
            }
            finally {
                statement.close();
            }
        }
        catch (SQLException e) {
            throw new BuildException(e);
        }
    }


    private String createSecurityModel() {
        StringBuilder xml = new StringBuilder();
        xml.append("<model>")
              .append("<grants>")
              .append("<entry>");
        appendUser(xml);
        appendRoles(xml);
        xml.append("</entry>")
              .append("</grants>")
              .append("</model>");
        return xml.toString();
    }


    private void appendRoles(StringBuilder xml) {
        if ((roles == null) || ("".equals(roles.trim()))) {
            xml.append("<list/>");
            return;
        }
        xml.append("<list>");
        for (String role : roles.split(",")) {
            xml.append("<role name='")
                  .append(role.trim())
                  .append("'/>");
        }
        xml.append("</list>");
    }


    private void appendUser(StringBuilder xml) {
        xml.append("<user name='")
              .append(user.trim()).append("'");
        if (lastLogin != null) {
            xml.append(" lastLogin='").append(convert(lastLogin)).append("'");
        }
        if (lastLogout != null) {
            xml.append(" lastLogout='").append(convert(lastLogout)).append("'");
        }
        xml.append("/>");
    }


    private static File fileInTemp(Project project, File file) {
        return fileInTemp(project, file.getName());
    }


    private static File fileInTemp(Project project, String fileName) {
        return new File(defaultSecurityModelDirectory(project), fileName);
    }


    private static String defaultSecurityModelDirectory(Project project) {
        File dir = new File(project.getBaseDir(), "target");
        return new File(dir.getPath(), DEFAULT_DIR).getPath();
    }


    private String convert(String date) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Timestamp.valueOf(date));
    }


    private RemoteCommand getRemoteCommand(String fileName) throws IOException {
        if (remoteCommand == null) {
            remoteCommand = new CopyFileSecureCommand(getRemoteUser(),
                                                      getRemoteServer(),
                                                      getPort(),
                                                      fileInTemp(getProject(),
                                                                 toAbsoluteFile(fileName)).getPath(),
                                                      determineRemoteRepository());
            remoteCommand.setLog(logger);
        }
        return remoteCommand;
    }


    public String determineRemoteRepository() throws IOException {
        String fileName = getProperty(SECURITY_MODEL_STORAGE_FILE + REMOTE_SUFFIX, true);
        if (fileName.lastIndexOf("/")>0){
            return fileName.substring(0,fileName.lastIndexOf("/"));
        }        
        return new File(fileName).getParent();
    }


    void setRemoteCommand(RemoteCommand remoteCommand) {
        this.remoteCommand = remoteCommand;
    }


    private class CopyFileSecureCommand extends ChannelSftpSecureCommand implements RemoteCommand {
        private final String source;
        private final String remoteDirectory;


        CopyFileSecureCommand(String user, String host, int port, String source, String remoteDirectory) {
            super(user, host, port);
            this.source = source;
            this.remoteDirectory = remoteDirectory;
        }


        @Override
        protected void execute(ChannelSftp channel) throws SftpException {
            cd(channel, remoteDirectory);
            put(channel, source, ChannelSftp.OVERWRITE);
        }
    }
}
