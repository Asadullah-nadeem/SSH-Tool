package org.example.key;

public class KeyConverter {
    // Use putty-key-tools or external commands if strict PPK conversion is required.
    public static void pemToPpk(String pemFile, String ppkFile, String passphrase) throws Exception {
        // Suggestion: call "puttygen pemFile -O private -o ppkFile --old-passphrase pass ..." as a process
        throw new UnsupportedOperationException("Native .ppk conversion not implemented in pure Java.");
    }
}
