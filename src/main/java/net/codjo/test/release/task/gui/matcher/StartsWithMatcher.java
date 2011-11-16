package net.codjo.test.release.task.gui.matcher;
/**
 *
 */
public class StartsWithMatcher extends AbstractMatcher{
    protected StartsWithMatcher(String value) {
        super(value);
    }


    public boolean match(String pattern) {
        return value.startsWith(pattern);
    }
}
