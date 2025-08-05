package org.example;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.example.key.KeyManager;
import org.example.ssh.SSHClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.Security;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        KeyManager km = new KeyManager();
        SSHClient ssh = new SSHClient();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            try {
                char key;
                boolean boo = false;
                while (boo) {
                    System.out.println("Enter a Text Example KeyPair(K), SSH(S): ");
                    key = (char) br.read();
                    switch (key) {
                        case 'S':
                        case 's':
                            ssh.connect("example.com", 22, "sda", "id_rsa.pem", "1234sfs", null);
                            String output = ssh.executeCommand("whoami");
                            System.out.println("Remote whoami: " + output);
                            ssh.disconnect();
                            break;
                        case 'k':
                        case 'K':
                            KeyPair kp = km.generateKeyPair("RSA", 2048);
                            km.savePEMKeyPair(kp, "id_rsa.pem", "id_rsa.pub", "1234sfs");
                            break;
                        default:
                            System.out.println("Unknown Command");


                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading input: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Error closing BufferedReader: " + e.getMessage());
        }
    }
}
