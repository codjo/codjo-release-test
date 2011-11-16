package net.codjo.test.release.task.web.form;
/**
 *
 */
public class Option {
    private String value = "";


    public void addText(String text) {
        this.value += text;
    }


    @Override
    public String toString() {
        return value;
    }
}
