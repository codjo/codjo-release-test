package net.codjo.test.release.task.mail;
import com.dumbster.smtp.SmtpMessage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import junit.framework.AssertionFailedError;
import net.codjo.test.common.fixture.MailFixture;
import net.codjo.test.common.fixture.MailMessage;
import net.codjo.test.release.task.AgfTask;
import org.apache.tools.ant.BuildException;
/**
 *
 */
public class AssertInboxTask extends AgfTask {

    private int retryCount = 5;
    private List<Message> expectedMessages = new ArrayList<Message>();


    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }


    @Override
    public void execute() {
        MailFixture mailFixture =
              (MailFixture)getProject().getReference(StartMailServerTask.MAIL_FIXTURE);
        if (mailFixture == null) {
            throw new BuildException("La balise <start-mail-server> doit etre specifiee"
                                     + " avant d'utiliser <assert-inbox>");
        }

        List<MailMessage> actualMessages = mailFixture.getReceivedMessages();

        for (int i = 0; ; i++) {
            try {
                checkExpectedMessages(actualMessages);
                return;
            }
            catch (BuildException e) {
                if (i < retryCount && !expectedMessages.isEmpty()) {
                    waitForMessage();
                }
                else {
                    throw e;
                }
            }
            catch (AssertionFailedError assertionFailedError) {
                StringBuilder builder = new StringBuilder();
                builder.append("Comparaison en erreur : ")
                      .append(removeLineFeed(assertionFailedError.getMessage()))
                      .append("\n\n");
                builder.append("Current Inbox :\n---------------\n").append(getInboxContents(actualMessages));
                throw new BuildException(builder.toString());
            }
        }
    }


    private void checkExpectedMessages(List<MailMessage> actualMessages) {
        for (Iterator<Message> expecteds = expectedMessages.iterator(); expecteds.hasNext(); ) {
            Message expectedMessage = expecteds.next();

            boolean found = false;
            for (Iterator<MailMessage> actualIterator = actualMessages.iterator(); actualIterator.hasNext(); ) {
                MailMessage actualMailMessage = actualIterator.next();
                SmtpMessage actualMessage = actualMailMessage.getSmtpMessage();

                String from = actualMessage.getHeaderValue("From");
                String to = actualMessage.getHeaderValue("To");
                String subject = actualMailMessage.getSubject();
                if (expectedMessage.isSame(from, to, subject)) {
                    expectedMessage.assertBody(actualMailMessage);
                    if (MultiPartMessage.class.isInstance(expectedMessage)) {
                        ((MultiPartMessage)expectedMessage).assertAttachments(actualMailMessage,
                                                                              this,
                                                                              getProperty(BROADCAST_LOCAL_DIR, true));
                    }
                    found = true;
                    expecteds.remove();
                    actualIterator.remove();
                    break;
                }
            }
            if (!found && expectedMessage.isPresent()) {
                throw new BuildException("Message non reçu : " + expectedMessage + "\n"
                                         + "Inbox=\n" + getInboxContents(actualMessages));
            }
            else if (found && !expectedMessage.isPresent()) {
                throw new BuildException("Message non attendu reçu : " + expectedMessage + "\n"
                                         + "Inbox=\n" + getInboxContents(actualMessages));
            }
        }
    }


    private void waitForMessage() {
        try {
            Thread.sleep(500);
        }
        catch (InterruptedException ex) {
            throw new BuildException(ex);
        }
    }


    private String getInboxContents(List<MailMessage> messages) {
        StringBuilder builder = new StringBuilder();
        for (MailMessage message : messages) {
            builder.append("From: ").append(message.getFrom())
                  .append(" - To: ").append(message.getTo())
                  .append(" - Subject: ").append(message.getSubject())
                  .append(" - Body: ").append(removeLineFeed(message.getBody()))
                  .append("\n");
        }
        return builder.toString();
    }


    public void addMessage(Message message) {
        this.expectedMessages.add(message);
    }


    public void addMultipartMessage(MultiPartMessage message) {
        this.expectedMessages.add(message);
    }


    private String removeLineFeed(String text) {
        return text != null ? text.replaceAll("\n", "<br/>") : text;
    }
}
