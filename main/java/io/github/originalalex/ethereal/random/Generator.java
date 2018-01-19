package io.github.originalalex.ethereal.random;

import io.github.originalalex.ethereal.math.HexToDecimal;
import io.github.originalalex.ethereal.math.SHA256;

public class Generator {

    public static double generate(ServerSecret secret, String clientKey, int nonce) {
        String combined = secret.getSecret() + "," + clientKey + "-" + nonce;
        String hash = SHA256.hash(combined);
        double hashDecimal = Double.MAX_VALUE;
        int i = 0;
        while (hashDecimal > 999999) { // numbers must be distributed binomially (so it has to end in 9999)
            hashDecimal = HexToDecimal.hexToDecimal(hash.substring(i, i+5));
            i += 5;
        }
        System.out.println(hashDecimal);
        hashDecimal %= 10000;
        hashDecimal /= 100; // now we have a number of the form ab.cd
        return hashDecimal;
    }

}
