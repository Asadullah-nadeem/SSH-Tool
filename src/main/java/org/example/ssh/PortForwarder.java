package org.example.ssh;

import com.jcraft.jsch.*;

public class PortForwarder {

    public void addLocalForward(Session session, int lPort, String rHost, int rPort) throws JSchException {
        session.setPortForwardingL(lPort, rHost, rPort);
    }

    public void addRemoteForward(Session session, int rPort, String lHost, int lPort) throws JSchException {
        session.setPortForwardingR(rPort, lHost, lPort);
    }
}
