package org.example;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.example.key.KeyManager;
import org.example.ssh.SSHClient;

import java.security.KeyPair;
import java.security.Security;

public class Main {
    public static void main(String[] args) throws Exception {
        // Register BouncyCastle provider early
        Security.addProvider(new BouncyCastleProvider());

        KeyManager km = new KeyManager();
        SSHClient ssh = new SSHClient();

        KeyPair kp = km.generateKeyPair("RSA", 2048);
        km.savePEMKeyPair(kp, "id_rsa.pem", "id_rsa.pub", "1234sfs");

        ssh.connect("example.com", 22, "sda", "id_rsa.pem", "1234sfs", null);
        String output = ssh.executeCommand("whoami");
        System.out.println("Remote whoami: " + output);
        ssh.disconnect();
    }
}
