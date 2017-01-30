package ru.arriah;

import static ru.arriah.HtmlFormatStateMachine.STATE.*;

/**
 * Позволяет вывести на экран с расцветкой нужную часть.
 * @author shevchenko-dv-100705
 */
class HtmlFormatStateMachine {
    private static final int BUFFER_LENGTH = 40;

    HtmlFormatStateMachine(char[] pattern, char[] text) {
        this.pattern = pattern;
        this.text = text;
    }

    enum STATE {
        START,
        CHECK,
        END,
        FIRST,
        SECOND,
        THIRD
    }

    private StringBuffer patternBuffer = new StringBuffer();
    private StringBuffer textBuffer = new StringBuffer();
    private StringBuffer result = new StringBuffer();
    private int bufferLength = 0;
    private int bufferPosition = 0;
    private int startTagPosition = -1;

    private final char[] pattern;
    private final char[] text;
    private int pos = 0;
    private STATE state = START;
    private String tag = null;



    public boolean isTerminated() {
        return state == END;
    }

    private boolean isBufferFull() {
        return bufferLength >= BUFFER_LENGTH;
    }

    private boolean isEndOfLine() {
        return pos >= pattern.length;
    }

    private boolean isFirstCase() {
        return pos < pattern.length && pattern[pos] == '_';
    }

    private boolean isSecondCase() {
        return pos < pattern.length && text[pos] == '_';
    }

    private boolean isBufferNotEmpty() {
        return bufferLength > 0;
    }

    private boolean isThirdCase() {
        return pos < pattern.length && pattern[pos] != text[pos] && isTGAC(pattern[pos]) && isTGAC(text[pos]);
    }

    private boolean isTGAC(char ch) {
        return ch == 'T' || ch == 'G' || ch == 'A' || ch == 'C';
    }

    private boolean isX6() {
        return pos < pattern.length || pattern[pos] == text[pos] || !isTGAC(pattern[pos]) || !isTGAC(text[pos]);
    }

    private void flushBuffer() {
        result.append("<table><tr>");
        result.append(textBuffer.toString());
        result.append("</tr><tr>");
        result.append(patternBuffer.toString());
        result.append("\n</tr></table>");
        result.append(String.format("Genome position is: %d", (bufferPosition+1)));
        if (startTagPosition >= 0) {
            result.append(String.format("<br/>First mismatch position is: %d", (startTagPosition+1)));
        }
        result.append("<hr/>");
        textBuffer = new StringBuffer();
        patternBuffer = new StringBuffer();
        bufferLength = 0;

        bufferPosition = pos;
        startTagPosition = -1;

    }

    private void startTagGeneral(String color) {
        tag = String.format("<font color = '%s'><b>", color);
        if (startTagPosition == -1) {
            startTagPosition = bufferLength;
        }
    }

    private void startRedTag() {
        startTagGeneral("red");
    }

    private void closeFontTag() {
        tag = null;
    }

    private void fillBufferWithCurrentSymbol() {
        textBuffer.append("<td>");
        if (tag != null) {
            textBuffer.append(tag);
        }
        textBuffer.append(text[pos]);
        if (tag != null) {
            textBuffer.append("</b></font>");
        }
        textBuffer.append("</td>");
        patternBuffer.append("<td>");
        if (tag != null) {
            patternBuffer.append(tag);
        }
        patternBuffer.append(pattern[pos++]);
        if (tag != null) {
            patternBuffer.append("</b></font>");
        }
        bufferLength++;
    }

    private void startGreenTag() {
        startTagGeneral("green");
    }

    private void startBlueTag() {
        startTagGeneral("blue");
    }

    public void iterate() {
        switch (state) {
            case START:
                if (isBufferFull()) {
                    flushBuffer();
                    return;
                }
                if (isEndOfLine()) {
                    state = CHECK;
                    return;
                }
                if (isFirstCase()) {
                    startRedTag();
                    state = FIRST;
                    return;
                }
                if (isSecondCase()) {
                    startGreenTag();
                    state = SECOND;
                    return;
                }
                if (isThirdCase()) {
                    startBlueTag();
                    state = THIRD;
                    return;
                }
                if (isX6()) {
                    fillBufferWithCurrentSymbol();
                    return;
                }
                throw new IllegalStateException("Could not resolve state: " + state);
            case CHECK:
                if (isBufferNotEmpty()) {
                    flushBuffer();
                    return;
                }
                state = END;
                return;
            case FIRST:
                if (isBufferFull()) {
                    flushBuffer();
                    state = START;
                    return;
                }
                if (isFirstCase()) {
                    fillBufferWithCurrentSymbol();
                    return;
                } else {
                    closeFontTag();
                    state = START;
                    return;
                }
            case SECOND:
                if (isBufferFull()) {
                    flushBuffer();
                    state = START;
                    return;
                }
                if (isSecondCase()) {
                    fillBufferWithCurrentSymbol();
                    return;
                } else {
                    closeFontTag();
                    state = START;
                    return;
                }
            case THIRD:
                if (isBufferFull()) {
                    flushBuffer();
                    state = START;
                    return;
                }
                if (isThirdCase()) {
                    fillBufferWithCurrentSymbol();
                    return;
                } else {
                    closeFontTag();
                    state = START;
                    return;
                }

        }

    }

    @Override
    public String toString() {
        return result.toString();
    }
}
