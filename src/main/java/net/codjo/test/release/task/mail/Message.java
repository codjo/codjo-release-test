package net.codjo.test.release.task.mail;
import net.codjo.test.common.fixture.MailMessage;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
public class Message {
    private String from;
    private String to;
    private String subject;
    private boolean present = true;
    private List<AssertText> assertions = new ArrayList<AssertText>();


    public boolean isSame(String fromRecipient, String toRecipient, String mailSubject) {
        return equals(this.from, fromRecipient)
               && equals(this.to, toRecipient)
               && equals(this.subject, mailSubject);
    }


    public void assertBody(MailMessage mailMessage) {
        for (AssertText assertion : assertions) {
            mailMessage.assertThat().bodyContains(assertion.getValue());
        }
    }


    private boolean equals(String string1, String string2) {
        return string1 == null ? string2 == null : string1.equals(string2);
    }


    public void setFrom(String from) {
        this.from = from;
    }


    public void setTo(String to) {
        this.to = to;
    }


    public void setSubject(String subject) {
        this.subject = subject;
    }


    public boolean isPresent() {
        return present;
    }


    public void setPresent(boolean present) {
        this.present = present;
    }


    public void addAssertText(AssertText assertion) {
        this.assertions.add(assertion);
    }


    @Override
    public String toString() {
        return "from='" + from + "' to='" + to + "' subject='" + subject + "'";
    }
}
