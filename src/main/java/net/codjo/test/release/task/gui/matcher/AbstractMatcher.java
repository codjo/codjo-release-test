package net.codjo.test.release.task.gui.matcher;
/**
 *
 */
public abstract class AbstractMatcher implements Matcher {
    protected String value;


    protected AbstractMatcher(String value) {
        this.value = value;
    }
}
