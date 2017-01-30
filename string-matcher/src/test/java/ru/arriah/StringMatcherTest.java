package ru.arriah;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author shevchenko-dv-100705
 */
public class StringMatcherTest {
    private final StringMatcher matcher = new StringMatcher();

    @Test
    public void tgacSymbolsShouldBeRecognized() {
        char[] arr = {'A', 'T', 'G', 'C'};

        for (char ch : arr) {
            Assert.assertTrue("Symbol should have been recognized: " + ch, matcher.isTGAC(ch));
        }

    }

    @Test
    public void clearStringFromNoiseText() {
        String noisy = "111TG22AC\n\tAAC";
        Assert.assertEquals(String.format("Unexpected clearing for %s", noisy), "TGACAAC",
                matcher.clearStringFromNoise(noisy, null));
    }

    @Test
    public void singleLetterTest() {
        StringMatcher.MatrixCell[][] matr = matcher.constructMatrix("a", "bbbbaссccc");
        Assert.assertEquals(getOutput(matr), 0, getEditDistance(matr));
        checkContent(matcher.getStrings(matr, "a", "bbbbaссccc"), "OOOOaOOOOO", "bbbbaссccc");
    }

    @Test
    public void singleLetterTestHandle() {        
        StringMatcher.MatcherResponse resp = matcher.handle("a", "ttttaccccc");
        Assert.assertEquals("Edit distance does not match", 0, resp.getDistance());
        Assert.assertEquals("Offset does not match", 4, resp.getStart());        
        checkContent(resp.getContent(), "OOOOAOOOOO", "TTTTACCCCC");        
    }

    @Test
    public void oneDistanceTestHandle() {
        StringMatcher.MatcherResponse resp = matcher.handle("atc", "tttttattt");
        Assert.assertEquals("Edit distance does not match", 1, resp.getDistance());
        Assert.assertEquals("Offset does not match", 5, resp.getStart());        
        checkContent(resp.getContent(), "OOOOOatcOO", "tttttat_tt");
    }

    @Test
    public void oneDistanceTest() {
        StringMatcher.MatrixCell[][] matr = matcher.constructMatrix("abc", "bbbbbabbb");
        Assert.assertEquals(getOutput(matr), 1, getEditDistance(matr));
        checkContent(matcher.getStrings(matr, "abc", "bbbbbabbb"), "OOOOOabcOO", "bbbbbab_bb");
        matr = matcher.constructMatrix("abcd", "bbbbbbbabc");
        Assert.assertEquals(getOutput(matr), 1, getEditDistance(matr));
        checkContent(matcher.getStrings(matr, "abcd", "bbbbbbbabc"), "OOOOOOOabcd", "bbbbbbbabc_");
    }



    private void checkContent(StringMatcher.StringContent sc, String pattern, String text) {
        Assert.assertEquals("Pattern alignment does not match for " + sc, pattern.toUpperCase(), sc.getPattern().toString().toUpperCase());
        Assert.assertEquals("Text alignment does not match for " + sc, text.toUpperCase(), sc.getText().toString().toUpperCase());
    }

    private int getEditDistance(StringMatcher.MatrixCell[][] matr) {
        int n = matr.length-1;
        return matr[n][matcher.getEditDistancePosition(matr)].getValue();
    }

    private String getOutput(StringMatcher.MatrixCell[][] matr) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0;i<matr.length;i++) {
            for (StringMatcher.MatrixCell cell : matr[i]) {
                sb.append("\t" + cell);
            }
            sb.append("\n");
        }
        return sb.toString();

    }
}
