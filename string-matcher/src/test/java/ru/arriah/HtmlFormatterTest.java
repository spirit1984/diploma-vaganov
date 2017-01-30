package ru.arriah;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by shevchenko-dv-100705 on 14.11.16.
 */
@Ignore
public class HtmlFormatterTest {

    @Test
    public void simpleFormatter() {

        StringMatcher.StringContent sc = new StringMatcher.StringContent();
        sc.getPattern().append("TG__A");
        sc.getText().append("TGXXB");

        StringMatcher.MatcherResponse response = new StringMatcher.MatcherResponse(sc, 3, (short) 5);
        System.out.println(new HtmlFormatter().getResponse(response));
    }
}
