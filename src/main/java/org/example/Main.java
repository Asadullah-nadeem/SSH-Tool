package org.example;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.example.key.KeyManager;
import org.example.ssh.SSHClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.Security;

public class Main {
    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        KeyManager km = new KeyManager();
        SSHClient ssh = new SSHClient();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            boolean running = true;
            while (running) {
                System.out.print("Enter a Command: KeyPair (K), SSH (S), Exit (E): ");
                String input = br.readLine().trim().toLowerCase();

                switch (input) {
                    case "s":
                        try {
                            ssh.connect("example.com", 22, "sda", "id_rsa.pem", "1234sfs", null);
                            String output = ssh.executeCommand("whoami");
                            System.out.println("Remote whoami: " + output);
                            ssh.disconnect();
                        } catch (Exception e) {
                            System.err.println("SSH Error: " + e.getMessage());
                        }
                        break;

                    case "k":
                        try {
                            KeyPair kp = km.generateKeyPair("RSA", 2048);
                            km.savePEMKeyPair(kp, "id_rsa.pem", "id_rsa.pub", "1234sfs");
                            System.out.println("Key pair generated and saved.");
                        } catch (Exception e) {
                            System.err.println("Key Generation Error: " + e.getMessage());
                        }
                        break;

                    case "e":
                        running = false;
                        System.out.println("Exiting...");
                        break;

                    default:
                        System.out.println("Unknown Command. Please enter K, S, or E.");
                }
            }
        } catch (IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
        }
    }
}
