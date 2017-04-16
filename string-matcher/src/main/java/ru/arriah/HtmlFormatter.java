package ru.arriah;


/**
 * Данный класс производит раскраску ответа в виде HTML.
 * @author shevchenko-dv-100705
 */
class HtmlFormatter {
    String getResponse(StringMatcher.MatcherResponse response) {
        StringBuffer sb = new StringBuffer();

        addStartTag(sb, "html");

        addStartTag(sb, "body");

        addParagraph(sb, String.format("Edit distance is: %d", response.getDistance()));
        addParagraph(sb, String.format("Offset is: %d", (response.getStart()+1)));





        if (response.getDistance() > 0) {
            int length = getGenomeLength(response.getContent().getPattern().toString().toCharArray());
            addParagraph(sb, String.format("Length is: %d", length));
            addParagraph(sb, String.format("End is: %d", (response.getStart() + length)));


            addStartTag(sb, "pre");
            sb.append("\n");
            sb.append(response.getContent().getText());
            sb.append("\n");
            sb.append(response.getContent().getPattern());
            sb.append("\n");
            addCloseTag(sb, "pre");

            sb.append("\n<br/>");

            HtmlFormatStateMachine machine = new HtmlFormatStateMachine(
                    response.getContent().getPattern().toString().toCharArray(),
                    response.getContent().getText().toString().toCharArray());

            while (!machine.isTerminated()) {
                machine.iterate();
            }

            sb.append(machine);
        }

        addCloseTag(sb, "body");

        addCloseTag(sb, "html");



        return sb.toString();
    }

    private int getGenomeLength(char[] chars) {
        int pos = 0;

        for (char ch : chars) {
            if (ch == 'O') {
                return pos;
            }
            pos++;
        }
        return pos;
    }

    private void addParagraph(StringBuffer sb, String text) {
        sb.append(text);
        sb.append("\n</br>\n");
    }

    private void addStartTag(StringBuffer sb, String tag) {
        sb.append(String.format("<%s>", tag));
    }

    private void addCloseTag(StringBuffer sb, String tag) {
        sb.append(String.format("</%s>", tag));
    }
}
