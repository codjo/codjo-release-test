package net.codjo.test.release.task.mail;
import net.codjo.test.common.fixture.MailFixture;
import net.codjo.test.release.task.AgfTask;
import net.codjo.test.release.task.Resource;
import org.apache.tools.ant.BuildException;
/**
 *
 */
public class StartMailServerTask extends AgfTask implements Resource {

    private int port = -1;
    public static final String MAIL_FIXTURE = "test.mail.fixture";


    @Override
    public void execute() {
        if (port < 0) {
            throw new BuildException("Champ 'port' obligatoire");
        }

        MailFixture mailFixture = new MailFixture(port);
        mailFixture.doSetUp();
        getProject().addReference(MAIL_FIXTURE, mailFixture);
    }


    public void setPort(int port) {
        this.port = port;
    }


    public void open() {

    }


    public void close() {
        MailFixture mailFixture = (MailFixture)getProject().getReference(MAIL_FIXTURE);
        if (mailFixture != null) {
            mailFixture.doTearDown();
        }
    }
}
