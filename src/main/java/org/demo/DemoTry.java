package org.demo;

import org.clip.LearningMode;

import java.util.Scanner;

public class DemoTry {

    public static void string2Seconds() {
        Scanner scanner = new Scanner(System.in);
        String time = scanner.next();

        int seconds = LearningMode.ExtractExistingSeconds(time);

        System.out.println("Total seconds: " + seconds);
    }
    public static void main(String[] args) {
        string2Seconds();
    }
}
