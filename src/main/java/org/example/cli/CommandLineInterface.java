package org.example.cli;

import org.example.crypto.KeyGenerator;
import org.example.crypto.KeyLoader;
import org.example.crypto.KeyWriter;
import org.example.ssh.ConnectionManager;
import org.example.ssh.PortForwarder;
import org.example.ssh.SFTPHandler;
import org.example.ssh.SSHCommandExecutor;
import org.example.util.SecurityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.security.KeyPair;

public class CommandLineInterface {
    private static final Logger logger = LoggerFactory.getLogger(CommandLineInterface.class);
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final KeyGenerator keyGenerator = new KeyGenerator();
    private final KeyWriter keyWriter = new KeyWriter();
    private final KeyLoader keyLoader = new KeyLoader();
    private final SSHCommandExecutor commandExecutor = new SSHCommandExecutor();
    private final PortForwarder portForwarder = new PortForwarder();
    private final SFTPHandler sftpHandler = new SFTPHandler();

    static {
        SecurityProvider.initialize();
    }

    public void start() {
        boolean running = true;
        while (running) {
            try {
                Menu.displayMainMenu();
                int choice = UserInput.readInt("");

                switch (choice) {
                    case 1 -> handleKeyManagement();
                    case 2 -> handleSshOperations();
                    case 3 -> handlePortForwarding();
                    case 4 -> handleFileTransfer();
                    case 5 -> running = false;
                    default -> System.out.println("Invalid option");
                }
            } catch (Exception e) {
                logger.error("Operation failed: {}", e.getMessage());
                System.out.println("Error: " + e.getMessage());
            }
        }
        connectionManager.closeAllSessions();
        System.out.println("Exiting...");
    }

    private void handleKeyManagement() throws Exception {
        Menu.displayKeyMenu();
        int choice = UserInput.readInt("");

        switch (choice) {
            case 1 -> generateKeyPair();
            case 2 -> loadKeyPair();
            case 3 -> { return; }
            default -> System.out.println("Invalid option");
        }
    }

    private void generateKeyPair() throws Exception {
        String algorithm = UserInput.readLine("Algorithm (RSA/ECDSA/ED25519): ");
        int size = UserInput.readInt("Key size: ");
        String privPath = UserInput.readLine("Private key path: ");
        String pubPath = UserInput.readLine("Public key path: ");
        String passphrase = UserInput.readLine("Passphrase (optional): ");

        KeyGenerator.Algorithm algo = KeyGenerator.Algorithm.valueOf(algorithm.toUpperCase());
        KeyPair keyPair = keyGenerator.generateKeyPair(algo, size);

        keyWriter.savePEMKey(keyPair.getPrivate(), privPath, passphrase);
        keyWriter.savePublicKey(keyPair.getPublic(), pubPath);

        System.out.println("Key pair generated successfully");
    }

    private void loadKeyPair() throws Exception {
        String path = UserInput.readLine("Key file path: ");
        String passphrase = UserInput.readLine("Passphrase (if encrypted): ");

        KeyPair keyPair = keyLoader.loadKeyPair(path, passphrase);
        System.out.println("Key loaded successfully: " + keyPair.getPublic().getAlgorithm());
    }

    private void handleSshOperations() throws Exception {
        Menu.displaySSHMenu();
        int choice = UserInput.readInt("");

        switch (choice) {
            case 1 -> createSession();
            case 2 -> executeCommand();
            case 3 -> listSessions();
            case 4 -> closeSession();
            case 5 -> { return; }
            default -> System.out.println("Invalid option");
        }
    }

    private void createSession() throws Exception {
        String label = UserInput.readLine("Session label: ");
        String host = UserInput.readLine("Host: ");
        int port = UserInput.readInt("Port: ");
        String user = UserInput.readLine("Username: ");
        String keyPath = UserInput.readLine("Private key path (optional): ");
        String passphrase = UserInput.readLine("Passphrase (optional): ");
        String password = UserInput.readLine("Password (optional): ");

        connectionManager.createSession(label, host, port, user,
                keyPath.isEmpty() ? null : keyPath,
                passphrase.isEmpty() ? null : passphrase,
                password.isEmpty() ? null : password);
        System.out.println("Session created: " + label);
    }

    // Other handler methods follow similar pattern...
}