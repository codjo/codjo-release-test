package net.codjo.test.release.task.gui.matcher;
import java.util.regex.Pattern;
/**
 *
 */
public class RegExpMatcher extends AbstractMatcher {
    Pattern pattern;


    public RegExpMatcher(String value) {
        super(value);
        pattern = Pattern.compile(value);
    }


    public boolean match(String stringToVerify) {
        return pattern.matcher(stringToVerify).matches();
    }
}
