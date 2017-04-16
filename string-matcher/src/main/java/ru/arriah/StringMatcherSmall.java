package ru.arriah;

import org.apache.log4j.Logger;

import static ru.arriah.StringMatcher.checkNonEmpty;
import static ru.arriah.StringMatcher.checkPatternIsNotLongerThanText;
import static ru.arriah.StringMatcher.clearStringFromNoise;

/**
 * Данный класс позволяет определить расстояние между двумя строками,
 * и начало вхождения подстроки в строку. Необходимо для случаев,
 * когда расстояние является на самом деле нулевым, и нет необходимости определять,
 * где находится геном на самом деле и где вставки. Это позволяет данному коду
 * выполняться гораздо быстрее и с гораздо меньшим количеством памяти,
 * чем коду полноценного поиска.
 * @author shevchenko-dv-100705
 */
public class StringMatcherSmall {

    private static final Logger logger = Logger.getLogger(StringMatcherSmall.class);
    /**
     * Ответ от данного класса состоит из двух элементов:
     * расстояния между строками и окончания образца в строке (если
     * расстояние нулевое, то восстановить, где начинался этот образец,
     * является тривиальной задачей.
     * <br/>
     * Боже мой, это же обычный case class на Scala. И там это определение
     * класса заняло бы одну строку. Но раз уж мы начали разрабатывать этот
     * консольный проект на Java, то мы продолжим.
     *
     */
    public class MatcherResponse {
        private final int distance;
        private final int end;
        private final int start;


        public MatcherResponse(int distance, int end, int start) {
            this.distance = distance;
            this.end = end;
            this.start = start;
        }

        public int getStart() {
            return start;
        }

        public int getDistance() {
            return distance;
        }

        public int getEnd() {
            return end;
        }
    }

    private class MatrixSmall {
        private final int[][] matr;
        MatrixSmall(int size) {
            matr = new int[2][size+1];
        }
    }

    public MatcherResponse search(String pattern, String text) {
        logger.info("First pass - just the distance");
        checkNonEmpty(pattern, "pattern");
        checkNonEmpty(text, "text");

        // Remove everything but the required symbols
        pattern = clearStringFromNoise(pattern, "pattern");
        text = clearStringFromNoise(text, "text");

        checkPatternIsNotLongerThanText(pattern, text);

        int n = pattern.length();
        int m = text.length();
        logger.info("Trying the standard library");
        int pos = text.indexOf(pattern);
        if (pos >= 0) return new MatcherResponse(0, pos+n-1, pos);
        logger.info("The distance is actually non-zero, so using non-standard methods");
        MatrixSmall sm = new MatrixSmall(m);

        int step = Math.max(1, n/100);
        for (int x = 1;x<=n;x++) {
            final char patternSymbol = pattern.charAt(x-1); // Не забываем о строке, что у нее нулевое смещение
            sm.matr[1][0] = x; // Получить непустой образец из пустой строки можно только за k - извиняйте
            for (int y =1;y<=m;y++) {
                final char textSymbol = text.charAt(y-1);
                sm.matr[1][y] = min(1+sm.matr[1][y-1], 1 + sm.matr[0][y],
                        cost(patternSymbol,textSymbol) + sm.matr[0][y-1]);
            }
            for (int i = 0;i<=m;i++) {
                sm.matr[0][i] = sm.matr[1][i];
            }

            if (x%step == 0) {
                logger.info(String.format("Step %d/%d", x, n));
            }
        }



        int distance = n;
        int end =  -1;
        for (int i=0;i<=m;i++) {
            if (distance > sm.matr[0][i]) {
                distance = sm.matr[0][i];
                end = i-1;
            }
        }

        logger.info("The actual distance found by dynamic programming is:" + distance);

        assertInvariant(distance, end, pattern, text);

        return new MatcherResponse(distance, end, end-n+1);
    }

    private void assertInvariant(int distance, int end, String pattern, String text) {
        if (distance > 0) return;
        if (end < 0 ) throw new IllegalStateException("End should not be negative at this point, since the distance is zero");
        int start = end - pattern.length() + 1;
        if (start < 0) throw new IllegalStateException(String.format("Start is negative. End is: %d", end));
        String subtext = text.substring(start, end+1);
        if (!pattern.equals(subtext))
            throw new IllegalStateException(String.format("Pattern does not match the subtext: %s", subtext));
    }

    private int min(int val, int... vals) {
        int result = val;
        for (int x : vals) {
            if (result > x) result = x;
        }
        return result;
    }

    private int cost(char pat, char text) {
        if (pat == text) return 0;
        return 1;
    }




}
