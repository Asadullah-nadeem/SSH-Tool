package org.example.crypto;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class KeyWriter {
    private static final Logger logger = LoggerFactory.getLogger(KeyWriter.class);

    public void savePEMKey(PrivateKey privateKey, String filePath, String passphrase) throws IOException {
        try (JcaPEMWriter writer = new JcaPEMWriter(new FileWriter(filePath))) {
            if (passphrase != null && !passphrase.isEmpty()) {
                OutputEncryptor encryptor = new JceOpenSSLPKCS8EncryptorBuilder(PKCS8Generator.AES_256_CBC)
                        .setSecureRandom(new SecureRandom())
                        .setPassword(passphrase.toCharArray())
                        .build();
                JcaPKCS8Generator gen = new JcaPKCS8Generator(privateKey, encryptor);
                writer.writeObject(gen);
            } else {
                writer.writeObject(privateKey);
            }
            logger.info("Private key saved to {}", filePath);
        }
    }

    public void savePublicKey(PublicKey publicKey, String filePath) throws IOException {
        try (JcaPEMWriter writer = new JcaPEMWriter(new FileWriter(filePath))) {
            writer.writeObject(publicKey);
            logger.info("Public key saved to {}", filePath);
        }
    }
}