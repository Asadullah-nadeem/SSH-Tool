package org.example.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyGenerator {
    private static final Logger logger = LoggerFactory.getLogger(KeyGenerator.class);
    private static final SecureRandom secureRandom = new SecureRandom();

    public enum Algorithm {
        RSA, ECDSA, ED25519
    }

    public KeyPair generateKeyPair(Algorithm algorithm, int size)
            throws NoSuchAlgorithmException, NoSuchProviderException {
        String algoName = algorithm.name();
        KeyPairGenerator gen = KeyPairGenerator.getInstance(algoName, "BC");

        if (algorithm != Algorithm.ED25519) {
            gen.initialize(size, secureRandom);
        } else {
            gen.initialize(255, secureRandom);
        }

        KeyPair keyPair = gen.generateKeyPair();
        logger.info("Generated {} keypair (size: {})", algoName, size);
        return keyPair;
    }
}