package ru.arriah;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author shevchenko-dv-100705
 */
public class StringMatcherSmallText {

    private final StringMatcherSmall matcher = new StringMatcherSmall();

    @Test
    public void singleLetterTest() {
        String pattern = "a";
        String text = "ggggaссccc";
        StringMatcherSmall.MatcherResponse resp = matcher.search(pattern, text);
        Assert.assertEquals(0, resp.getDistance());
        Assert.assertEquals(4, resp.getEnd());
    }

    @Test
    public void oneDistanceTestHandle() {
        String pattern = "atc";
        String text = "tttttattt";
        StringMatcherSmall.MatcherResponse resp = matcher.search(pattern, text);
        Assert.assertEquals(1, resp.getDistance());
        Assert.assertEquals(6, resp.getEnd());
    }

}
