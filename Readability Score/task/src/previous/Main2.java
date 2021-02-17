package previous;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main2 {
    public static void main_(String[] args) {
        String text = new Scanner(System.in).nextLine();
        final double ratio = 1.0 * countWords(text) / countSentences(text);
        System.out.println(ratio > 10 ? "HARD" : "EASY");
    }

    static int countSentences(String text) {
        return countMatches("[.!?]+\\s+|$", text);
    }

    static int countWords(String text) {
        return countMatches("\\w+", text);
    }

    static int countMatches(String patternStr, String text) {
        Matcher matcher = Pattern.compile(patternStr).matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}
