package com.fixit.util;

import java.util.Scanner;

/**
 * Utility for safe, robust console I/O.
 * Solves the common Scanner newline consumption issue by reading complete lines
 * and parsing types safely, preventing crashes from invalid inputs.
 */
public class ConsoleUtil {

    public static void printHeader(String title) {
        System.out.println();
        System.out.println("============================================================");
        System.out.println("   " + title.toUpperCase());
        System.out.println("============================================================");
    }

    public static void printSubHeader(String title) {
        System.out.println();
        System.out.println("--- " + title + " ---");
    }

    public static void printSuccess(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    public static void printError(String message) {
        System.out.println("[ERROR] " + message);
    }

    public static void printWarning(String message) {
        System.out.println("[WARNING] " + message);
    }

    public static void printInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    /**
     * Reads a line of text, allowing blank strings.
     */
    public static String readString(Scanner sc, String prompt) {
        System.out.print(prompt + ": ");
        return sc.nextLine().trim();
    }

    /**
     * Loops until a non-empty string is provided.
     */
    public static String readNonEmptyString(Scanner sc, String prompt, String errorMessage) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            printError(errorMessage);
        }
    }

    /**
     * Loops until a valid email address is entered.
     */
    public static String readEmail(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String email = sc.nextLine().trim();
            if (InputValidator.isValidEmail(email)) {
                return email;
            }
            printError("Invalid email format (e.g., student@college.edu). Please try again.");
        }
    }

    /**
     * Reads a password with basic length validation.
     */
    public static String readPassword(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String password = sc.nextLine().trim();
            if (InputValidator.isValidPassword(password)) {
                return password;
            }
            printError("Password cannot be blank and must be at least 4 characters.");
        }
    }

    /**
     * Reads an integer within [min, max] range without crashing on letters or symbols.
     */
    public static int readInt(Scanner sc, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt + " (" + min + "-" + max + "): ");
            String input = sc.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                printError("Choice out of range. Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                printError("Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Prompts the user to press Enter to continue.
     */
    public static void pressEnterToContinue(Scanner sc) {
        System.out.print("\nPress [Enter] to continue...");
        sc.nextLine();
    }
}
