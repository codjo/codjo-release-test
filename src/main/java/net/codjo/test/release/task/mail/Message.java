package net.codjo.test.release.task.mail;
import java.util.ArrayList;
import java.util.List;
import net.codjo.test.common.fixture.MailMessage;
/**
 *
 */
public class Message {
    private String from;
    private String to;
    private String cc;
    private String subject;
    private boolean present = true;
    private List<AssertText> assertions = new ArrayList<AssertText>();


    public boolean isSame(String fromRecipient, String toRecipient, String ccRecipient, String mailSubject) {
        return equals(this.from, fromRecipient)
               && equals(this.to, toRecipient)
               && equals(this.cc, ccRecipient)
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


    public void setCc(String cc) {
        this.cc = cc;
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
        return "from='" + from + "' to='" + to + "'" + appendCcIfNeeded() + " subject='" + subject + "'";
    }


    private String appendCcIfNeeded() {
        return (cc == null) ? "" : " cc='" + cc + "'";
    }
}
