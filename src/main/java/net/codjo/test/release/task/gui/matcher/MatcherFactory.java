package net.codjo.test.release.task.gui.matcher;
import junit.extensions.jfcunit.finder.Finder;
/**
 *
 */
public class MatcherFactory {
    public static final String EQUALS_MATCHING = "equals";
    public static final String CONTAINS_MATCHING = "contains";
    public static final String STARTS_WITH_MATCHING = "startsWith";
    public static final String ENDS_WITH_MATCHING = "endsWith";
    public static final String REGEXP_MATCHING = "regexp";


    public Matcher get(String matcher, String value) {
        if (EQUALS_MATCHING.equals(matcher)) {
            return new EqualsMatcher(value);
        }
        if (CONTAINS_MATCHING.equals(matcher)) {
            return new ContainsMatcher(value);
        }
        if (STARTS_WITH_MATCHING.equals(matcher)) {
            return new StartsWithMatcher(value);
        }
        if (ENDS_WITH_MATCHING.equals(matcher)) {
            return new EndsWithMatcher(value);
        }
        if (REGEXP_MATCHING.equals(matcher)) {
            return new RegExpMatcher(value);
        }
        throw new IllegalArgumentException("Error: matcher '" + matcher + "' not found");
    }


    public static int getFinderOperationFromMatching(String matcher) {
        if (EQUALS_MATCHING.equals(matcher)) {
            return Finder.OP_EQUALS;
        }
        if (CONTAINS_MATCHING.equals(matcher)) {
            return Finder.OP_CONTAINS;
        }
        if (STARTS_WITH_MATCHING.equals(matcher)) {
            return Finder.OP_STARTSWITH;
        }
        if (ENDS_WITH_MATCHING.equals(matcher)) {
            return Finder.OP_ENDSWITH;
        }
        if (REGEXP_MATCHING.equals(matcher)) {
            return Finder.OP_MATCH;
        }
        throw new IllegalArgumentException("Error: matcher '" + matcher + "' not found");
    }
}
