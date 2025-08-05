package org.example.cli;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.example.crypto.KeyGenerator;
import org.example.crypto.KeyLoader;
import org.example.crypto.KeyWriter;
import org.example.ssh.ConnectionManager;
import org.example.ssh.PortForwarder;
import org.example.ssh.SFTPHandler;
import org.example.ssh.SSHCommandExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.util.SecurityProvider;

import java.io.IOException;
import java.security.KeyPair;
import java.util.Map;

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
                // TODO : [RED-ERROR] ERROR org.example.cli.CommandLineInterface - Operation failed: java.net.ConnectException: Connection refused: connect
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

    private void executeCommand() throws Exception {
        String label = UserInput.readLine("Session label: ");
        Session session = connectionManager.getSession(label);
        if (session == null) {
            System.out.println("Session not found: " + label);
            return;
        }

        String command = UserInput.readLine("Command to execute: ");
        String result = commandExecutor.executeCommand(session, command);
        System.out.println("Command output:\n" + result);
    }

    private void listSessions() {
        Map<String, Session> sessions = connectionManager.getActiveSessions();
        if (sessions.isEmpty()) {
            System.out.println("No active sessions");
            return;
        }

        System.out.println("\nActive Sessions:");
        sessions.forEach((label, session) -> {
            System.out.printf("- %s: %s@%s:%d (%s)%n",
                    label,
                    session.getUserName(),
                    session.getHost(),
                    session.getPort(),
                    session.isConnected() ? "connected" : "disconnected");
        });
    }

    private void closeSession() throws JSchException, IOException {
        String label = UserInput.readLine("Session label to close: ");
        connectionManager.closeSession(label);
        System.out.println("Session closed: " + label);
    }

    private void handlePortForwarding() throws Exception {
        System.out.println("\n--- Port Forwarding ---");
        System.out.println("1. Local Port Forwarding");
        System.out.println("2. Remote Port Forwarding");
        System.out.println("3. Remove Forwarding");
        System.out.println("4. Back to Main");
        System.out.print("Select option: ");

        int choice = UserInput.readInt("");
        String label = UserInput.readLine("Session label: ");
        Session session = connectionManager.getSession(label);
        if (session == null) {
            System.out.println("Session not found: " + label);
            return;
        }

        switch (choice) {
            case 1 -> {
                int localPort = UserInput.readInt("Local port: ");
                String remoteHost = UserInput.readLine("Remote host: ");
                int remotePort = UserInput.readInt("Remote port: ");
                portForwarder.createLocalForward(session, localPort, remoteHost, remotePort);
                System.out.printf("Local forwarding created: %d -> %s:%d%n", localPort, remoteHost, remotePort);
            }
            case 2 -> {
                int remotePort = UserInput.readInt("Remote port: ");
                String localHost = UserInput.readLine("Local host: ");
                int localPort = UserInput.readInt("Local port: ");
                portForwarder.createRemoteForward(session, remotePort, localHost, localPort);
                System.out.printf("Remote forwarding created: %s:%d -> %d%n", localHost, localPort, remotePort);
            }
            case 3 -> {
                int port = UserInput.readInt("Port to remove forwarding from: ");
                portForwarder.removeForward(session, port);
                System.out.println("Forwarding removed from port: " + port);
            }
            case 4 -> { return; }
            default -> System.out.println("Invalid option");
        }
    }

    private void handleFileTransfer() throws Exception {
        System.out.println("\n--- File Transfer ---");
        System.out.println("1. Upload File");
        System.out.println("2. Download File");
        System.out.println("3. Back to Main");
        System.out.print("Select option: ");

        int choice = UserInput.readInt("");
        String label = UserInput.readLine("Session label: ");
        Session session = connectionManager.getSession(label);
        if (session == null) {
            System.out.println("Session not found: " + label);
            return;
        }

        switch (choice) {
            case 1 -> {
                String localPath = UserInput.readLine("Local file path: ");
                String remotePath = UserInput.readLine("Remote file path: ");
                sftpHandler.uploadFile(session, localPath, remotePath);
                System.out.println("File uploaded successfully");
            }
            case 2 -> {
                String remotePath = UserInput.readLine("Remote file path: ");
                String localPath = UserInput.readLine("Local file path: ");
                sftpHandler.downloadFile(session, remotePath, localPath);
                System.out.println("File downloaded successfully");
            }
            case 3 -> { return; }
            default -> System.out.println("Invalid option");
        }
    }
}