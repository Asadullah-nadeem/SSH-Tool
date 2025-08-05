package org.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;

public class SecurityProvider {
    private static boolean initialized = false;

    public static synchronized void initialize() {
        if (!initialized) {
            Security.addProvider(new BouncyCastleProvider());
            initialized = true;
        }
    }
}