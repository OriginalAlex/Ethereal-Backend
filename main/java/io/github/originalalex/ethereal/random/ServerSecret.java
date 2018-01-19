package io.github.originalalex.ethereal.random;

import io.github.originalalex.ethereal.math.SHA256;

import java.util.UUID;

/**
 * Generate a random server-side secret
 * The hashed version of this secret will be shown to the player
 * and when the player asks for it we will reveal the secret itself
 */

public class ServerSecret {

    private String value;

    public ServerSecret() {
        this.value = UUID.randomUUID().toString();
    }

    public String getHash() {
        return SHA256.hash(value);
    }

    public String getSecret() {
        return this.value;
    }

}
