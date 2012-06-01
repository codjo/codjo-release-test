package net.codjo.test.release.task.mail;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import junit.framework.TestCase;
import net.codjo.test.common.PathUtil;
import net.codjo.test.common.fixture.MailFixture;
import net.codjo.test.release.TestEnvironment;
import net.codjo.test.release.TestEnvironmentMock;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import static net.codjo.test.release.task.AgfTask.BROADCAST_LOCAL_DIR;
import static net.codjo.test.release.task.AgfTask.TEST_DIRECTORY;

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


    public void test_multipartMessageReceived() throws Exception {
        sendEmailWithAttachments();

        MultiPartMessage expectedMessage = new MultiPartMessage();
        task.addMessage(expectedMessage);
        expectedMessage.setFrom("emetteur@example.com");
        expectedMessage.setTo("destinataire@example.com");
        expectedMessage.setCc("destinataireEnCopie@example.com");
        expectedMessage.setSubject("Sujet");
        expectedMessage.setPresent(true);
        AssertText assertion = new AssertText();
        assertion.setValue("Contenu du mail en tant que part d'un multi-part message.");
        expectedMessage.addAssertText(assertion);
        AssertAttachment assertAttachment = new AssertAttachment();
        assertAttachment.setAttachmentIndex(1);
        assertAttachment.setComparisonType("xls");
        assertAttachment.setExpected("attachmentFileTest.xls");
        expectedMessage.addAssertAttachment(assertAttachment);
        task.execute();
    }


    private void assertEmail(boolean present) {
        Message expectedMessage = new Message();
        task.addMessage(expectedMessage);
        expectedMessage.setFrom("emetteur@example.com");
        expectedMessage.setTo("destinataire@example.com");
        expectedMessage.setCc("destinataireEnCopie@example.com");
        expectedMessage.setSubject("Sujet");
        expectedMessage.setPresent(present);
        AssertText assertion = new AssertText();
        assertion.setValue("Contenu du mail en tant que part d'un multi-part message.");
        expectedMessage.addAssertText(assertion);
        task.execute();
    }


    private void sendEmail() throws MessagingException {
        MimeMessage message = prepareMessage();

        MimeMultipart multipartMessage = new MimeMultipart();
        message.setContent(multipartMessage);

        addBody(multipartMessage);

        Transport.send(message);
    }


    private void sendEmailWithAttachments() throws MessagingException, IOException {
        MimeMessage message = prepareMessage();

        MimeMultipart multipartMessage = new MimeMultipart();
        message.setContent(multipartMessage);

        addBody(multipartMessage);

        MimeBodyPart attachmentPart = new MimeBodyPart();

        File attachment = new File(PathUtil.findResourcesFileDirectory(getClass()).getPath(), "attachmentFileTest.xls");
        attachmentPart.setDataHandler(new DataHandler(new ByteArrayDataSource(new FileInputStream(attachment),
                                                                              "application/excel")));
        attachmentPart.setFileName(attachment.getName());
        multipartMessage.addBodyPart(attachmentPart);

        Transport.send(message);
    }


    private void addBody(MimeMultipart multipartMessage) throws MessagingException {
        String body = "Contenu du mail en tant que part d'un multi-part message.";
        MimeBodyPart contentsPart = new MimeBodyPart();
        contentsPart.setContent(body, "text/html; charset=ISO-8859-1");
        multipartMessage.addBodyPart(contentsPart);
    }


    private MimeMessage prepareMessage() throws MessagingException {
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", "localhost");
        properties.setProperty("mail.smtp.port", "8082");
        Session session = Session.getInstance(properties);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("emetteur@example.com"));
        message.setRecipients(javax.mail.Message.RecipientType.TO,
                              InternetAddress.parse("destinataire@example.com", false));
        message.setRecipients(javax.mail.Message.RecipientType.CC,
                              InternetAddress.parse("destinataireEnCopie@example.com", false));
        message.setSubject("Sujet", "ISO-8859-1");
        return message;
    }


    @Override
    protected void setUp() throws Exception {
        mailfixture = new MailFixture(8082);
        mailfixture.doSetUp();

        Project project = new Project();
        project.addReference(StartMailServerTask.MAIL_FIXTURE, mailfixture);
        project.setProperty(TEST_DIRECTORY, PathUtil.findResourcesFileDirectory(getClass()).getPath());
        project.setProperty(BROADCAST_LOCAL_DIR, PathUtil.findResourcesFileDirectory(getClass()).getPath());
        project.addReference(TestEnvironment.class.getName(), new TestEnvironmentMock(project));

        task = new AssertInboxTask();
        task.setProject(project);
    }


    @Override
    protected void tearDown() throws Exception {
        mailfixture.doTearDown();
    }
}
