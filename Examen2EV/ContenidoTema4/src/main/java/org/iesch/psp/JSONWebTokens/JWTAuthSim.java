package org.iesch.psp.JSONWebTokens;

import java.util.ArrayList;
import java.util.List;
import java.util.Base64;

// Basado en las clases de Claims de la página 16 y 17 del PDF
class Claim {
    private final String type;
    private final String value;

    public Claim(String type, String value) {
        this.type = type;
        this.value = value;
    }
    public String getType() { return type; }
    public String getValue() { return value; }
}

public class JWTAuthSim {

    /**
     * Simulación de la creación del Payload del JWT 
     * utilizando Claims (Pág. 15-18)
     */
    public static void main(String[] args) {
        // 1. Definimos los Claims que irán en el Payload del JWT [cite: 521]
        List<Claim> claims = new ArrayList<>();
        claims.add(new Claim("Nameldentifier", "user123"));
        claims.add(new Claim("Role", "Operator"));
        claims.add(new Claim("Department", "IT"));

        // 2. Visualización de los datos que se codificarían en el token
        System.out.println("--- Contenido del Payload (Claims) ---");
        for (Claim c : claims) {
            System.out.println(c.getType() + ": " + c.getValue());
        }

        // 3. Recordatorio de seguridad (Pág. 19)
        System.out.println("\nRECOMENDACIONES DEL PDF:");
        System.out.println("- No añadir información sensible al payload[cite: 531].");
        System.out.println("- Los tokens deben tener una caducidad[cite: 532].");
        System.out.println("- No enviar tokens por conexiones no seguras (sin TLS)[cite: 533].");
    }
}