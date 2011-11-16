package net.codjo.test.release.task.gui.matcher;
/**
 *
 */
public class EqualsMatcher extends AbstractMatcher{
    protected EqualsMatcher(String value) {
        super(value);
    }


    public boolean match(String pattern) {
        return value.equals(pattern);
    }
}
