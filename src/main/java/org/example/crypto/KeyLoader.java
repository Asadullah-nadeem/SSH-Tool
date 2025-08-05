package org.example.crypto;

import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyPair;

public class KeyLoader {
    private static final Logger logger = LoggerFactory.getLogger(KeyLoader.class);
    private final JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

    public KeyPair loadKeyPair(String filePath, String passphrase) throws IOException {
        try (PEMParser parser = new PEMParser(new FileReader(filePath))) {
            Object obj = parser.readObject();

            if (obj instanceof PEMEncryptedKeyPair) {
                PEMEncryptedKeyPair encryptedKeyPair = (PEMEncryptedKeyPair) obj;
                return converter.getKeyPair(encryptedKeyPair.decryptKeyPair(
                        new JcePEMDecryptorProviderBuilder().build(passphrase.toCharArray())));
            } else if (obj instanceof PEMKeyPair) {
                return converter.getKeyPair((PEMKeyPair) obj);
            } else {
                throw new IllegalArgumentException("Unsupported key format: " + obj.getClass().getName());
            }
        }
    }
}