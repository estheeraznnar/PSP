package org.iesch.psp.HTTPHashClaims;

// --- Claim.java ---
public class Claim {
    private final String type;
    private final String value;

    public Claim(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() { return type; }
    public String getValue() { return value; }
}