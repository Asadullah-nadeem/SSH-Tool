package org.example.ssh;

import com.jcraft.jsch.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;

public class SSHClient {
    private static final Logger logger = LoggerFactory.getLogger(SSHClient.class);
    private JSch jsch = new JSch();
    private Session session;

    public void connect(String host, int port, String user, String privKeyPath, String passphrase, String password) throws JSchException {
        if (privKeyPath != null && !privKeyPath.isEmpty()) {
            if (passphrase != null && !passphrase.isEmpty()) {
                jsch.addIdentity(privKeyPath, passphrase);
            } else {
                jsch.addIdentity(privKeyPath);
            }
        }
        session = jsch.getSession(user, host, port);
        if (password != null && !password.isEmpty()) session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no"); // For demo only!
        logger.info("Connecting to {}:{}", host, port);
        session.connect();
        logger.info("Connected.");
    }

    public String executeCommand(String command) throws Exception {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.setInputStream(null);
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        channel.setOutputStream(responseStream);
        channel.connect();
        while (!channel.isClosed()) Thread.sleep(100);
        String resp = responseStream.toString("UTF-8");
        channel.disconnect();
        return resp;
    }

    public void disconnect() {
        if (session != null && session.isConnected()) session.disconnect();
        logger.info("Session disconnected.");
    }
}
