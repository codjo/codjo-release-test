package net.codjo.test.release.task.gui.matcher;
/**
 *
 */
public class EndsWithMatcher extends AbstractMatcher{
    protected EndsWithMatcher(String value) {
        super(value);
    }


    public boolean match(String pattern) {
        return value.endsWith(pattern);
    }
}
