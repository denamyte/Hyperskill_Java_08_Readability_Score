package readability;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        String text;
        try (BufferedReader reader = new BufferedReader(new FileReader(args[0]))) {
            text = reader.lines().collect(Collectors.joining(" "));
        }
        ReadabilityCounter counter = new ReadabilityCounter(text);

    }

}

class TextAnalyzer {
    private final String text;
    public final int wordNumber;
    public final int sentenceNumber;
    public final int charNumber;
    public final int syllableNumber;
    public final int polysyllableNumber;

    TextAnalyzer(String text) {
        this.text = text;
        wordNumber = countMatches(text, RegExp.WORD);
        sentenceNumber = countMatches(text, RegExp.SENTENCE);
        charNumber = countMatches(text, RegExp.CHARACTER);
        String textWithoutLastE = text.toLowerCase().replaceAll(RegExp.MERGE_LAST_E_WITH_PREVIOUS_SYLLABLE, "a");

        System.out.println(textWithoutLastE);

        syllableNumber = countMatches(textWithoutLastE, RegExp.SYLLABLE);
        polysyllableNumber = countMatches(textWithoutLastE, RegExp.POLYSYLLABLE);
    }

    private static int countMatches(String text, String patternStr) {
        Matcher matcher = Pattern.compile(patternStr).matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    @Override
    public String toString() {
        return String.format("The text is:\n%s\n\n" +
                "Words: %d\nSentences: %d\nCharacters: %d\nSyllables: %d\nPolysyllables: %d"
                , text, wordNumber, sentenceNumber, charNumber, syllableNumber, polysyllableNumber);
    }
}

class ReadabilityCounter {

    private static final int[] yearsTable = {6, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 24, 24};

    final TextAnalyzer analyzer;

    ReadabilityCounter(String text) {
        analyzer = new TextAnalyzer(text);
        System.out.println(analyzer);
    }

    private double automatedTest() {
        return 4.71 * analyzer.charNumber / analyzer.wordNumber +
                .5 * analyzer.wordNumber / analyzer.sentenceNumber - 21.43;
    }

    // TODO: 2/23/21 Implement readability tests
}

class RegExp {
    public static final String WORD = "[\\w,]+";
    public static final String SENTENCE = "[.!?]+\\s+|$";
    public static final String CHARACTER = "[^\\s\\n\\t]";

    public static final String VOW = "[aeiouy]";
    public static final String CONS = "[bcdfghjklmnpqrstvwxz]";
    public static final String MERGE_LAST_E_WITH_PREVIOUS_SYLLABLE = String.format("%s%s+e\\b", VOW, CONS);
    public static final String REGULAR_SYLLABLE = String.format("%s+", VOW);
    public static final String DIGITAL_SYLLABLE = "\\d[\\d,]+";
    public static final String SYLLABLE = String.format("%s|%s", REGULAR_SYLLABLE, DIGITAL_SYLLABLE);
    public static final String POLYSYLLABLE = String.format("\\b%s*(%s+%s+){2,}%s+%s*\\b",
                                                            CONS, VOW, CONS, VOW, CONS);

}
