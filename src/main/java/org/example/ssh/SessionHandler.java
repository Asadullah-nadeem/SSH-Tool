package org.example.ssh;

import com.jcraft.jsch.*;
import java.util.*;

public class SessionHandler {
    private Map<String, Session> sessions = new HashMap<>();

    public void addSession(String label, Session s) { sessions.put(label, s); }
    public Session getSession(String label) { return sessions.get(label); }
    public void removeSession(String label) {
        Session s = sessions.get(label);
        if (s != null && s.isConnected()) s.disconnect();
        sessions.remove(label);
    }
}
