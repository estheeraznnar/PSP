package org.iesch.psp.HTTPHashClaims;

// --- Credential.java --- (reutilizamos)
public class Credential {
    private String user;
    private String passwordHash;

    public Credential(String user, String passwordHash) {
        this.user = user;
        this.passwordHash = passwordHash;
    }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}