package net.codjo.test.release.task.mail;
import net.codjo.test.common.fixture.MailFixture;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import junit.framework.TestCase;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public class AssertInboxTaskTest extends TestCase {
    protected MailFixture mailfixture;
    protected AssertInboxTask task;


    public void test_messageExpectedReceived() throws Exception {
        sendEmail();
        assertEmail(true);
    }


    public void test_messageExpectedNotReceived() throws Exception {
        try {
            assertEmail(true);
            fail();
        }
        catch (BuildException e) {
            assertTrue(e.getMessage().startsWith("Message non reçu : "));
        }
    }


    public void test_messageNotExpectedReceived() throws Exception {
        sendEmail();
        try {
            assertEmail(false);
            fail();
        }
        catch (BuildException e) {
            assertTrue(e.getMessage().startsWith("Message non attendu reçu : "));
        }
    }


    public void test_messageNotExpectedNotReceived() throws Exception {
        assertEmail(false);
    }


    private void assertEmail(boolean present) {
        Message expectedMessage = new Message();
        task.addMessage(expectedMessage);
        expectedMessage.setFrom("emetteur@example.com");
        expectedMessage.setTo("destinataire@example.com");
        expectedMessage.setSubject("Sujet");
        expectedMessage.setPresent(present);
        task.execute();
    }


    private void sendEmail() throws MessagingException {
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", "localhost");
        properties.setProperty("mail.smtp.port", "8082");
        Session session = Session.getInstance(properties);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("emetteur@example.com"));
        message.setRecipients(javax.mail.Message.RecipientType.TO,
                              InternetAddress.parse("destinataire@example.com", false));
        message.setSubject("Sujet", "ISO-8859-1");

        MimeMultipart multipartMessage = new MimeMultipart();
        message.setContent(multipartMessage);

        String body = "Contenu du mail en tant que part d'un multi-part message.";
        MimeBodyPart contentsPart = new MimeBodyPart();
        contentsPart.setContent(body, "text/html; charset=ISO-8859-1");
        multipartMessage.addBodyPart(contentsPart);

        Transport.send(message);
    }


    @Override
    protected void setUp() throws Exception {
        mailfixture = new MailFixture(8082);
        mailfixture.doSetUp();

        Project project = new Project();
        project.addReference(StartMailServerTask.MAIL_FIXTURE, mailfixture);

        task = new AssertInboxTask();
        task.setProject(project);
    }


    @Override
    protected void tearDown() throws Exception {
        mailfixture.doTearDown();
    }
}
