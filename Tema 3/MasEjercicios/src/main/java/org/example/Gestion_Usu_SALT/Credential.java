package org.example.Gestion_Usu_SALT;

/**
 * Usuario con nombre y saltedHash.
 */
public class Credential {

    private final String username;
    private final String saltedHash;

    public Credential(String username, String saltedHash) {
        this.username = username;
        this.saltedHash = saltedHash;
    }

    public String getUsername() {
        return username;
    }

    public String getSaltedHash() {
        return saltedHash;
    }

    @Override
    public String toString() {
        return "Credential{username='" + username + "'}";
    }
}
