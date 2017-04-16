package ru.arriah;

import static ru.arriah.StringMatcher.DIRECTION.DIAG;
import static ru.arriah.StringMatcher.DIRECTION.LEFT;
import static ru.arriah.StringMatcher.DIRECTION.UP;
import org.apache.log4j.Logger;


/**
 * Данный класс использует базовые идеи алгоритма <a href = "https://en.wikipedia.org/wiki/Needleman%E2%80%93Wunsch_algorithm">
 *     сопоставления строк</a>, однако использует не функцию максимума, а минимума, причем таким образом, чтобы
 *     не было штрафа за пропуски в начале и конце текста, в котором ищется образец (т.е. мы пытаемся расположить
 *     образец оптимальным образом, и не штрафуем за его сдвиг влево или вправо в тексте).
 * @author shevchenko-dv-100705
 */
public class StringMatcher {
    private static final Logger logger = Logger.getLogger(StringMatcher.class);
    private static final char MISS_SIGN = 'O';
    private static final char GAP_SIGN = '_';

    private static final int BUFFER_SIZE = 8000;
    
    

    public static class StringContent {
        private final StringBuffer text = new StringBuffer();
        private final StringBuffer pattern = new StringBuffer();

        public StringBuffer getText() {
            return text;
        }

        public StringBuffer getPattern() {
            return pattern;
        }

        @Override
        public String toString() {
            return text + "\n" + pattern;
        }
    }

    enum DIRECTION {
        UP,
        LEFT,
        DIAG,
        SKIP
    }

    class MatrixCell {
        private final  short value;
        private final DIRECTION direction;

        public MatrixCell(short value, DIRECTION direction) {
            this.value = value;
            this.direction = direction;
        }

        public short getValue() {
            return value;
        }

        public DIRECTION getDirection() {
            return direction;
        }

        @Override
        public String toString() {
            return "{" +
                    "value=" + value +
                    ", direction=" + direction +
                    '}';
        }
    }

    public static class MatcherResponse {
        private final StringContent content;
        private final int start;
        private final short distance;

        public MatcherResponse(StringContent content, int start, short distance) {
            this.content = content;
            this.start = start;
            this.distance = distance;
        }

        public StringContent getContent() {
            return content;
        }

        public int getStart() {
            return start;
        }

        public short getDistance() {
            return distance;
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(String.format("Edit distance is: %d\n", distance));
            sb.append(String.format("Offset is: %d\n", start));
            sb.append(content.toString());            
            return sb.toString();
        }
    }



    public MatcherResponse handleBuffer(String pattern, String text, int k, int end) {
        logger.info("Second pass");
        checkNonEmpty(pattern, "pattern");
        checkNonEmpty(text, "text");

        // Remove everything but the required symbols
        pattern = clearStringFromNoise(pattern, "pattern");
        text = clearStringFromNoise(text, "text");

        checkPatternIsNotLongerThanText(pattern, text);        
        checkFieldDoesNotExceedLimit(pattern, "pattern",  10000);
        logger.info(String.format(
            "After clearing the noise from data now pattern size is: %d. Text size is %d",
            pattern.length(), text.length()));
        int n = pattern.length(), m = text.length();

        String optimalText = null;
        int min = n+1; // This is the upper bound for the edit distance, you know
        int offset = 0;
        StringMatcher.MatrixCell[][] matr = null;


        int left = Math.max(1, end-n-k-5), right = Math.min(m, end+k+5);

        logger.info(String.format("%d-%d/%d", left, right, m));
        String currentText = text.substring(left-1, right); // Don't forget it's exclusive
        StringMatcher.MatrixCell[][] currentMatr = constructMatrix(pattern, currentText);
        int distance = getEditDistance(currentMatr);
        if (min > distance) {
            offset = left-1;
            matr=  currentMatr;
            optimalText = currentText;
        }


        return stripPatternOffset(
            modifyOffset(convertMatrixToResponse(matr, pattern, optimalText), offset));        
    }

    private int getEditDistance(StringMatcher.MatrixCell[][] matr) {
        int n = matr.length-1;

        return matr[n][getEditDistancePosition(matr)].getValue();
    }

    private MatcherResponse modifyOffset(MatcherResponse resp, int offset) {
        if (offset == 0) {
            return resp;
        }
        return new MatcherResponse(resp.content, resp.start + offset, resp.distance);
    }

    private MatcherResponse stripPatternOffset(MatcherResponse resp) {
        boolean skip = true;
        StringContent sc = new StringContent();
        char[] pattern = resp.content.pattern.toString().toCharArray();
        char[] text = resp.content.text.toString().toCharArray();

        for (int i = 0; i<pattern.length;i++) {
            if (pattern[i] == MISS_SIGN && skip) {
                continue;
            }
            skip = false;

            sc.pattern.append(pattern[i]);
            sc.text.append(text[i]);
        }

        return new MatcherResponse(sc, resp.start, resp.distance);
    }

    public MatcherResponse handle(String pattern, String text) {
        checkNonEmpty(pattern, "pattern");
        checkNonEmpty(text, "text");

        // Remove everything but the required symbols
        pattern = clearStringFromNoise(pattern, "pattern");
        text = clearStringFromNoise(text, "text");

        checkPatternIsNotLongerThanText(pattern, text);        
        checkFieldDoesNotExceedLimit(pattern, "pattern",  3000);
        // Это означает матрицу [3000][6000], т.е. около 100 Мегабайт оперативной памяти требуется приложению
        checkFieldDoesNotExceedLimit(text, "text", 6000);

        MatrixCell[][] matr = constructMatrix(pattern, text);
        return convertMatrixToResponse(matr, pattern, text);
    }

    MatrixCell[][] constructMatrix(String pattern, String text) {
        int n = pattern.length(), m = text.length();

        MatrixCell[][] matr = new MatrixCell[n+1][m+1];

        for (int i = 0;i<=n;i++) {
            for (int j = 0;j<=m;j++) {
                matr[i][j] = calculateCellValue(matr, i, j, pattern, text);
            }
        }

        return matr;
    }

    private MatrixCell calculateCellValue(MatrixCell[][] matr, int row, int col, String pattern, String text) {
        // Используем технику запоминания
        if (matr[row][col] != null) {
            return matr[row][col];
        }



        if (col == 0) {
            return new MatrixCell((short) row, UP);
        }

        char textSymbol = symbol(text, col);

        if (row == 0) {
            // Тогда 0, поскольку мы не штрафуем за это
            return calculateCellValue(matr, row, col-1, pattern, text);
        }

        char patternSymbol = symbol(pattern, row);

        MatrixCell cell = min(
                calculateCell(calculateCellValue(matr, row, col-1, pattern, text).value, LEFT, costReplace(patternSymbol, GAP_SIGN)),
                calculateCell(calculateCellValue(matr, row-1, col, pattern, text).value, UP, costReplace(GAP_SIGN, textSymbol)),
                calculateCell(calculateCellValue(matr, row-1, col-1, pattern, text).value, DIAG, costReplace(patternSymbol, textSymbol))
        );


        if (row == pattern.length()) {
            cell = min(
                    cell,
                    calculateCell(calculateCellValue(matr, row, col-1, pattern, text).value, DIRECTION.SKIP, (short) 0) // Здесь это нам ничего не стоит
            )
            ;
        }



        return cell;
    }

    private MatrixCell calculateCell(short value, DIRECTION direction, short cost) {
        return new MatrixCell((short) (value+cost), direction);
    }

    private MatrixCell min(MatrixCell first, MatrixCell second, MatrixCell... arr) {
        MatrixCell result = first;
        if (result.value > second.value) {
            result = second;
        }

        if (arr != null) {
            for (MatrixCell cell : arr) {
                if (result.value > cell.value) {
                    result = cell;
                }
            }
        }

        return result;
    }

    private char symbol(String text, int pos) {
        return text.charAt(pos-1);

    }

    private short costReplace(char first, char second) {
        if (first == second) return 0;
        return 1;
    }



    StringContent getStrings(MatrixCell[][] matr, String pattern, String text) {
        int col = getEditDistancePosition(matr);
        StringContent sc = getStrings(matr, pattern.length(), col, pattern, text);

        for (int i = col+1;i<=text.length();i++) {
            sc.text.append(symbol(text, i));
            sc.pattern.append(MISS_SIGN);
        }

        return sc;
    }

    private StringContent append(StringContent content, char text, char pattern) {
        content.text.append(text);
        content.pattern.append(pattern);
        return content;
    }

    private StringContent getStrings(MatrixCell[][] matr, int row, int col, String pattern, String text) {
        if (row == 0 && col == 0) {
            return new StringContent();
        }

        if (row == 0) {
            return append(getStrings(matr, row, col-1, pattern, text), symbol(text, col), MISS_SIGN);
        }

        char textSymbol = symbol(text, col);
        char patternSymbol = symbol(pattern, row);

        if (matr[row][col].direction == DIAG) {
            return append(getStrings(matr, row-1, col-1, pattern, text), textSymbol, patternSymbol);

        }
        if (matr[row][col].direction == UP) {
            return append(getStrings(matr, row-1, col, pattern, text), GAP_SIGN, patternSymbol);
        }

        if (matr[row][col].direction == LEFT) {
            return append(getStrings(matr, row, col-1, pattern, text), textSymbol, GAP_SIGN);
        }

        throw new IllegalStateException("The code should simply never reached here");

    }

    int getEditDistancePosition(StringMatcher.MatrixCell[][] matr) {
        int n = matr.length-1, m = matr[0].length-1, min = n, pos = 0;

        for (int i =0;i<=m;i++) {
            if (min > matr[n][i].getValue() && matr[n][i].getDirection() != StringMatcher.DIRECTION.SKIP) {
                min = matr[n][i].getValue();
                pos = i;
            }
        }

        return pos;

    }

    private MatcherResponse convertMatrixToResponse(MatrixCell[][] matr, String pattern, String text) {
        int col = getEditDistancePosition(matr);
        int n = pattern.length();
        short distance = matr[n][col].value;
        StringContent sc = getStrings(matr, pattern, text);
        int offset = calculateOffset(sc.pattern.toString());
        return new MatcherResponse(sc, offset, distance);
    }

    private int calculateOffset(String str) {
        int pos = 0;
        for (char ch : str.toCharArray()) {
            if (ch != MISS_SIGN) {
                return pos;
            }
            pos++;
        }
        throw new IllegalStateException("Should not get here, check the string: " + str);
    }

    void checkFieldDoesNotExceedLimit(String value, String label, int maxLength) {
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(String.format("Sorry, but argument %s has %d symbols, which" +
                    "is more than max length - %d", label, value.length(), maxLength));
        }
    }

    static String clearStringFromNoise(String value, String label) {
        StringBuffer sb = new StringBuffer();

        for (char ch : value.toUpperCase().toCharArray()) {
            if (isTGAC(ch)) {
                sb.append(ch);
            }
        }

        String result = sb.toString();
        if (result.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("Argument '%s' turned out to have no TGAC symbols", label));
        }
        return result;
    }

    static boolean isTGAC(char ch) {
        return ch == 'A' || ch == 'C' || ch == 'G' || ch == 'T';
    }

    static void checkPatternIsNotLongerThanText(String pattern, String text) {
        if (pattern.length() > text.length() ) {
            throw new IllegalArgumentException(String.format("Pattern has %d symbols, more than text that has %d symbols",
                    pattern.length(), text.length()));
        }
    }

    static void checkNonEmpty(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format("Argument '%s' cannot be empty", label));
        }

    }
}
