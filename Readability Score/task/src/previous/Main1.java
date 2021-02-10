package previous;

import java.util.Scanner;

public class Main1 {
    public static void main_(String[] args) {
        String text = new Scanner(System.in).nextLine();
        System.out.println(text.length() > 100 ? "HARD" : "EASY");
    }
}
