package net.codjo.test.release.task.mail;
import net.codjo.test.release.task.AgfTask;
import com.sun.mail.smtp.SMTPTransport;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import org.apache.tools.ant.BuildException;
/**
 *
 */
public class SendMailTask extends AgfTask {

    private static final String SMTP_HOST = "localhost";

    private int port = -1;
    private String subject = "";
    private String from;
    private String to;
    private String body = "";


    @Override
    public void execute() {
        if (to == null) {
            throw new BuildException("Champ 'to' obligatoire");
        }
        if (from == null) {
            throw new BuildException("Champ 'from' obligatoire");
        }
        if (port < 0) {
            throw new BuildException("Champ 'port' obligatoire");
        }
        try {
            sendMail();
        }
        catch (Exception e) {
            throw new BuildException("Impossible d'envoyer le mail", e);
        }
    }


    private void sendMail() throws MessagingException, IOException {

        Properties props = System.getProperties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", Integer.toString(port));

        Session session = Session.getInstance(props);
        session.setDebug(true);

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));

        msg.setSubject(subject);

        msg.setDataHandler(new DataHandler(new ByteArrayDataSource(body, "text/html")));

        msg.setHeader("X-Mailer", "test");
        msg.setSentDate(new Date());

        Transport transport = new SMTPTransport(session, null);
        transport.connect(SMTP_HOST, port, "test", "test");
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
    }


    public void setSubject(String subject) {
        this.subject = subject;
    }


    public void setFrom(String from) {
        this.from = from;
    }


    public void setTo(String to) {
        this.to = to;
    }


    public void setPort(int port) {
        this.port = port;
    }


    public void addText(String msg) {
        this.body += msg;
    }
}
