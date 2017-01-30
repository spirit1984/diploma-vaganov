package ru.arriah;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by shevchenko-dv-100705 on 25.11.16.
 */
public class StringUtils {
    public final static Pattern genePattern = Pattern.compile("gene=([\\w|\\.]*)");
    public final static Pattern locationPattern = Pattern.compile("location=(\\w*)");

    public static boolean isGeneDescription(String name) {
        return name != null && name.startsWith(">lcl");
    }

    public static String convertToFileName(String description) {

        Matcher geneMatcher = genePattern.matcher(description);
        if (!geneMatcher.find() || geneMatcher.groupCount() < 1) {
            return "";
        }
        String filename = geneMatcher.group(1);

        Matcher locationMatcher = locationPattern.matcher(description);

        if (!locationMatcher.find() || locationMatcher.groupCount() < 1) {
            return filename;
        }

        return filename.replace(".", "") + "_" + locationMatcher.group(1);

    }
}
