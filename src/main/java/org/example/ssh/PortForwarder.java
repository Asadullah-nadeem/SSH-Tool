package org.example.ssh;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortForwarder {
    private static final Logger logger = LoggerFactory.getLogger(PortForwarder.class);

    public void createLocalForward(Session session, int localPort, String remoteHost, int remotePort)
            throws JSchException {
        session.setPortForwardingL(localPort, remoteHost, remotePort);
        logger.info("Local forwarding: {} -> {}:{}", localPort, remoteHost, remotePort);
    }

    public void createRemoteForward(Session session, int remotePort, String localHost, int localPort)
            throws JSchException {
        session.setPortForwardingR(remotePort, localHost, localPort);
        logger.info("Remote forwarding: {}:{} -> {}", localHost, localPort, remotePort);
    }

    public void removeForward(Session session, int port) throws JSchException {
        session.delPortForwardingL(port);
        logger.info("Removed forwarding for port: {}", port);
    }
}