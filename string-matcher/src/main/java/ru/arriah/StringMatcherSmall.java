package ru.arriah;

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

        public MatcherResponse(int distance, int end) {
            this.distance = distance;
            this.end = end;
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
        checkNonEmpty(pattern, "pattern");
        checkNonEmpty(text, "text");

        // Remove everything but the required symbols
        pattern = clearStringFromNoise(pattern, "pattern");
        text = clearStringFromNoise(text, "text");

        checkPatternIsNotLongerThanText(pattern, text);
        int n = pattern.length();
        int m = text.length();
        MatrixSmall sm = new MatrixSmall(m);

        for (int x = 1;x<=n;x++) {
            final char patternSymbol = pattern.charAt(x-1); // Не забываем о строке, что у нее нулевое смещение
            sm.matr[1][0] = x; // Получить непустой образец из пустой строки можно только за k - извиняйте
            for (int y =1;y<=m;y++) {
                final char textSymbol = text.charAt(y-1);
                sm.matr[1][y] = min(sm.matr[1][y-1], 1 + sm.matr[0][y],
                        cost(patternSymbol,textSymbol) + sm.matr[0][y-1]);
            }
            for (int i = 0;i<=m;i++) {
                sm.matr[0][i] = sm.matr[1][i];
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

        return new MatcherResponse(distance, end);
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
