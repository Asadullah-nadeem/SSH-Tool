package org.example.key;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Logger;

import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;


public class KeyManager {
    private static final Logger logger = Logger.getLogger(KeyManager.class.getName());

    public KeyPair generateKeyPair(String algorithm, int size) throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance(algorithm);
        if (algorithm.equalsIgnoreCase("Ed25519")) {
            gen.initialize(255);
        } else {
            gen.initialize(size);
        }
        KeyPair keyPair = gen.generateKeyPair();
        logger.info(algorithm + " keypair generated with size " + size);
        return keyPair;
    }

    public void savePEMKeyPair(KeyPair kp, String privFile, String pubFile, String passphrase) throws Exception {
        try (JcaPEMWriter privWriter = new JcaPEMWriter(new FileWriter(privFile))) {
            if (passphrase != null && !passphrase.isEmpty()) {
                OutputEncryptor encryptor = new JceOpenSSLPKCS8EncryptorBuilder(PKCS8Generator.AES_256_CBC)
                        .setRandom(new SecureRandom())
                        .setPassword(passphrase.toCharArray())
                        .build();
                JcaPKCS8Generator pkcs8Gen = new JcaPKCS8Generator(kp.getPrivate(), encryptor);
                privWriter.writeObject(pkcs8Gen);
            } else {
                privWriter.writeObject(kp.getPrivate());
            }
        }
        logger.info("Private key saved to " + privFile);

        try (JcaPEMWriter pubWriter = new JcaPEMWriter(new FileWriter(pubFile))) {
            pubWriter.writeObject(kp.getPublic());
        }
        logger.info("Public key saved to " + pubFile);
    }

    public KeyPair loadPEMKeyPair(String privFile, String passphrase) throws Exception {
        try (PEMParser parser = new PEMParser(new FileReader(privFile))) {
            Object obj = parser.readObject();
            JcaPEMKeyConverter conv = new JcaPEMKeyConverter().setProvider("BC");
            if (obj instanceof PEMEncryptedKeyPair) {
                PEMEncryptedKeyPair eKeyPair = (PEMEncryptedKeyPair) obj;
                return conv.getKeyPair(eKeyPair.decryptKeyPair(
                        new JcePEMDecryptorProviderBuilder().build(passphrase.toCharArray())));
            } else if (obj instanceof PEMKeyPair) {
                return conv.getKeyPair((PEMKeyPair) obj);
            } else {
                throw new IllegalArgumentException("Unsupported PEM format.");
            }
        }
    }
}
