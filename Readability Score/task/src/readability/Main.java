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
        TextAnalyzer analyzer = new TextAnalyzer(text);
        System.out.println(analyzer.getTextInfo());
    }

}

class TextAnalyzer {

    private static final String[] yearsTable = {"5-6", "6-7", "7-9", "9-10", "10-11", "11-12", "12-13", "13-14",
            "14-15", "15-16", "16-17", "17-18", "18-24", "24+"};
    final String text;

    // Derived fields:
    private int wordNumber;
    private int sentenceNumber;
    private int charNumber;
    private double score;
    private String comprehensionYears;

    public TextAnalyzer(String text) {
        this.text = text;
        analyze();
    }

    public String getTextInfo() {
        return String.format(
                "Words: %d\nSentences: %d\nCharacters: %d\n" +
                        "The score is: %.2f\nThis text should be understood by %s-year-olds.",
                wordNumber, sentenceNumber, charNumber, score, comprehensionYears);
    }

    private void analyze() {
        wordNumber = countWords();
        sentenceNumber = countSentences();
        charNumber = countCharacters();
        score = calcScore();
        final int key = score > 14 ? 13 : (int) Math.ceil(score) - 1;
        comprehensionYears = yearsTable[key];
    }

    private int countWords() {
        return countMatches("[\\w,]+");
    }

    private int countSentences() {
        return countMatches("[.!?]+\\s+|$");
    }

    private int countCharacters() {
        return countMatches("[^\\s\\n\\t]");
    }

    private double calcScore() {
        return 4.71 * charNumber / wordNumber + .5 * wordNumber / sentenceNumber - 21.43;
    }

    private int countMatches(String patternStr) {
        Matcher matcher = Pattern.compile(patternStr).matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}
