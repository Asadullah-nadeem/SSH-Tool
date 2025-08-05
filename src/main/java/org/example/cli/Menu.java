package org.example.cli;

public class Menu {
    public static void displayMainMenu() {
        System.out.println("\n==== SSH Toolkit ====");
        System.out.println("1. Key Management");
        System.out.println("2. SSH Operations");
        System.out.println("3. Port Forwarding");
        System.out.println("4. File Transfer");
        System.out.println("5. Exit");
        System.out.print("Select option: ");
    }

    public static void displayKeyMenu() {
        System.out.println("\n--- Key Management ---");
        System.out.println("1. Generate Key Pair");
        System.out.println("2. Load Key Pair");
        System.out.println("3. Back to Main");
        System.out.print("Select option: ");
    }

    public static void displaySSHMenu() {
        System.out.println("\n--- SSH Operations ---");
        System.out.println("1. Create Session");
        System.out.println("2. Execute Command");
        System.out.println("3. List Sessions");
        System.out.println("4. Close Session");
        System.out.println("5. Back to Main");
        System.out.print("Select option: ");
    }
}