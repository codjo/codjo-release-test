package net.codjo.test.release.task.mail;
/**
 *
 */
public class AssertAttachment {
    private String expected;
    private int attachmentIndex;
    private String comparisonType;


    public String getExpected() {
        return expected;
    }


    public void setExpected(String expected) {
        this.expected = expected;
    }


    public int getAttachmentIndex() {
        return attachmentIndex;
    }


    public void setAttachmentIndex(int attachmentIndex) {
        this.attachmentIndex = attachmentIndex;
    }


    public String getComparisonType() {
        return comparisonType;
    }


    public void setComparisonType(String comparisonType) {
        this.comparisonType = comparisonType;
    }
}
