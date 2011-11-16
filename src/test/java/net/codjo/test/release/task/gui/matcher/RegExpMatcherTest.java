package net.codjo.test.release.task.gui.matcher;
import org.junit.Assert;
import org.junit.Test;

public class RegExpMatcherTest {

    @Test
    public void test_matchSimple() throws Exception {
        RegExpMatcher matcher = new RegExpMatcher("toto");
        Assert.assertTrue(matcher.match("toto"));
        Assert.assertFalse(matcher.match("titi"));
        Assert.assertFalse(matcher.match("tototo"));
    }


    @Test
    public void test_match() throws Exception {
        RegExpMatcher matcher = new RegExpMatcher(".*[0-9]{2}");
        Assert.assertTrue(matcher.match("albert99"));
        Assert.assertFalse(matcher.match("gigi7"));
        Assert.assertFalse(matcher.match("tete"));
    }
}
