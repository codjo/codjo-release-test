package net.codjo.test.release.task.gui.matcher;
/**
 *
 */
public class ContainsMatcher extends AbstractMatcher {

    public ContainsMatcher(String value) {
        super(value);
    }


    public boolean match(String pattern) {
        return value.contains(pattern);
    }
}
