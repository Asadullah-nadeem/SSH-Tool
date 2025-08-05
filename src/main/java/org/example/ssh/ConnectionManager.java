package org.example.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
    private final JSch jsch = new JSch();
    private final Map<String, Session> activeSessions =
            Collections.synchronizedMap(new LinkedHashMap<>(10, 0.75f, true));
    private final Map<String, String> sessionConfig = new ConcurrentHashMap<>();

    public ConnectionManager() {
        // Default configuration
        sessionConfig.put("StrictHostKeyChecking", "no");
        sessionConfig.put("PreferredAuthentications", "publickey,password");
        sessionConfig.put("MaxAuthTries", "3");
    }

    public Session createSession(String label, String host, int port, String username,
                                 String privateKeyPath, String passphrase, String password)
            throws JSchException {
        if (activeSessions.containsKey(label)) {
            throw new IllegalArgumentException("Session label already exists: " + label);
        }

        if (privateKeyPath != null) {
            if (passphrase != null) {
                jsch.addIdentity(privateKeyPath, passphrase);
            } else {
                jsch.addIdentity(privateKeyPath);
            }
        }

        Session session = jsch.getSession(username, host, port);

        // Apply configuration
        sessionConfig.forEach(session::setConfig);

        if (password != null) {
            session.setPassword(password);
        }

        session.connect();
        activeSessions.put(label, session);
        logger.info("Created session: {}@{}:{}", username, host, port);
        return session;
    }

    public Session getSession(String label) {
        return activeSessions.get(label);
    }

    public Map<String, Session> getActiveSessions() {
        return Collections.unmodifiableMap(activeSessions);
    }

    public void closeSession(String label) {
        Session session = activeSessions.remove(label);
        if (session != null && session.isConnected()) {
            session.disconnect();
            logger.info("Closed session: {}", label);
        }
    }

    public void closeAllSessions() {
        activeSessions.keySet().forEach(this::closeSession);
    }

    public void setConfig(String key, String value) {
        sessionConfig.put(key, value);
    }
}