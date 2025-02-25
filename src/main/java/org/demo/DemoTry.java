package org.demo;

import org.clip.DurationManager;

import java.util.Scanner;

public class DemoTry {

    public static void string2Seconds() {
        Scanner scanner = new Scanner(System.in);
        String time = scanner.next();

        long seconds = DurationManager.getSeconds(time);

        System.out.println("Total seconds: " + seconds);
    }
    public static void main(String[] args) {
        string2Seconds();
    }
}
