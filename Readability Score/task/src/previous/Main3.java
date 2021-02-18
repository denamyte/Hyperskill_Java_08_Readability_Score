package previous;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main3 {
    public static void main_(String[] args) throws IOException {
        String text;
        try (BufferedReader reader = new BufferedReader(new FileReader(args[0]))) {
            text = reader.lines().collect(Collectors.joining(" "));
        }
        TextAnalyzer analyzer = new TextAnalyzer(text);
        System.out.println(analyzer.getTextInfo());
    }

}

class TextAnalyzer {

    static final Map<Integer, String> scoreTable;
    final String text;

    // Derived fields:
    private int wordNumber;
    private int sentenceNumber;
    private int charNumber;
    private double score;
    private String comprehensionYears;

    static {
        scoreTable = Stream.of("1 5-6", "2 6-7", "3 7-9", "4 9-10", "5 10-11", "6 11-12", "7 12-13", "8 13-14",
                               "9 14-15", "10 15-16", "11 16-17", "12 17-18", "13 18-24", "14 24+")
                .map(s -> s.split(" "))
                .collect(Collectors.toMap(
                        ar -> Integer.parseInt(ar[0]),
                        ar -> ar[1],
                        (s, s2) -> s));
    }

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
        final int key = score > 14 ? 14 : (int) Math.ceil(score);
        comprehensionYears = scoreTable.get(key);
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
