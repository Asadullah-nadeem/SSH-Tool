package org.example.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInput {
    private static final BufferedReader reader =
            new BufferedReader(new InputStreamReader(System.in));

    public static String readLine(String prompt) throws IOException {
        System.out.print(prompt);
        return reader.readLine().trim();
    }

    public static int readInt(String prompt) throws IOException {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    public static String readPassword(String prompt) throws IOException {
        // In real implementation, use Console.readPassword()
        return readLine(prompt);
    }
}