package previous;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main4 {
    public static void main_(String[] args) throws IOException {
        String text;
        try (BufferedReader reader = new BufferedReader(new FileReader(args[0]))) {
            text = reader.lines().collect(Collectors.joining(" "));
        }
        new TestDispatcher(text);
    }
}

class TestDispatcher {

    final TextAnalyzer4 analyzer;
    final Map<String, ReadabilityTest> testMap;

    TestDispatcher(String text) {
        analyzer = new TextAnalyzer4(text);
        testMap = iniTestMap();
        dispatch();
    }

    private Map<String, ReadabilityTest> iniTestMap() {
        Map<String, ReadabilityTest> map = new LinkedHashMap<>();
        map.put("ARI", new AutomatedTest(analyzer));
        map.put("FK", new FleschKincaidTest(analyzer));
        map.put("SMOG", new GobbledygookTest(analyzer));
        map.put("CL", new ColemanLiauTest(analyzer));
        return map;
    }

    private void dispatch() {
        System.out.println(analyzer);
        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        String choice = new Scanner(System.in).nextLine();
        if (choice.equals("all")) {
            System.out.println(showAll());
        } else {
            System.out.println();
            System.out.println(testMap.get(choice).getTestString());
        }
    }

    private String showAll() {
        StringBuilder sb = new StringBuilder("\n");
        AtomicInteger years = new AtomicInteger();

        testMap.forEach((s, test) -> {
            sb.append(test.getTestString()).append('\n');
            years.addAndGet(test.getYear());
        });

        double averageYear = (double) years.get() / testMap.size();
        sb.append("\nThis text should be understood in average by ")
                .append(averageYear)
                .append("-year-olds.");
        return sb.toString();
    }
}

class TextAnalyzer4 {
    private final String text;
    public final int words;
    public final int sentences;
    public final int chars;
    public final int syllables;
    public final int polysyllables;

    TextAnalyzer4(String text) {
        this.text = text;
        words = countMatches(text, RegExp.WORD);
        sentences = countMatches(text, RegExp.SENTENCE);
        chars = countMatches(text, RegExp.CHARACTER);
        String textWithoutLastE = text.toLowerCase().replaceAll(RegExp.MERGE_LAST_E_WITH_PREVIOUS_SYLLABLE, "a");
        syllables = countMatches(textWithoutLastE, RegExp.SYLLABLE);
        polysyllables = countMatches(textWithoutLastE, RegExp.POLYSYLLABLE);
    }

    private static int countMatches(String text, String patternStr) {
        Matcher matcher = Pattern.compile(patternStr).matcher(text);
        return IntStream.generate(() -> 1).takeWhile(v -> matcher.find()).sum();
    }

    @Override
    public String toString() {
        return String.format("The text is:\n%s\n\n" +
                "Words: %d\nSentences: %d\nCharacters: %d\nSyllables: %d\nPolysyllables: %d"
                , text, words, sentences, chars, syllables, polysyllables);
    }
}

abstract class ReadabilityTest {

    private static final int[] yearsTable = {6, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 24, 24};
    protected final TextAnalyzer4 analyzer;
    public final double index;

    ReadabilityTest(TextAnalyzer4 analyzer) {
        this.analyzer = analyzer;
        index = calculate();
    }

    public int getYear() {
        return yearsTable[-1 + (int) Math.round(index)];
    }

    public String getTestString() {
        return String.format("%s: %.2f (about %d-year-olds).", getName(), index, getYear());
    }

    abstract String getName();

    protected abstract double calculate();
}

class AutomatedTest extends ReadabilityTest {

    AutomatedTest(TextAnalyzer4 analyzer) {
        super(analyzer);
    }

    @Override
    public String getName() {
        return "Automated Readability Index";
    }

    @Override
    protected double calculate() {
        return 4.71 * analyzer.chars / analyzer.words +
                .5 * analyzer.words / analyzer.sentences - 21.43;
    }
}

class FleschKincaidTest extends ReadabilityTest {

    FleschKincaidTest(TextAnalyzer4 analyzer) {
        super(analyzer);
    }

    @Override
    String getName() {
        return "Flesch–Kincaid readability tests";
    }

    @Override
    protected double calculate() {
        return .39 * analyzer.words / analyzer.sentences +
                11.8 * analyzer.syllables / analyzer.words - 15.59;
    }
}

class GobbledygookTest extends ReadabilityTest {

    GobbledygookTest(TextAnalyzer4 analyzer) {
        super(analyzer);
    }

    @Override
    String getName() {
        return "Simple Measure of Gobbledygook";
    }

    @Override
    protected double calculate() {
        return 1.043 * Math.sqrt((double) analyzer.polysyllables * 30 / analyzer.sentences) + 3.1291;
    }
}

class ColemanLiauTest extends ReadabilityTest {

    ColemanLiauTest(TextAnalyzer4 analyzer) {
        super(analyzer);
    }

    @Override
    String getName() {
        return "Coleman-Liau index";
    }

    @Override
    protected double calculate() {
        double lettersPerWord = (double) analyzer.chars / analyzer.words * 100;
        double sentPerWord = (double) analyzer.sentences / analyzer.words * 100;
        return 0.0588 * lettersPerWord - 0.296 * sentPerWord - 15.8;
    }
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
