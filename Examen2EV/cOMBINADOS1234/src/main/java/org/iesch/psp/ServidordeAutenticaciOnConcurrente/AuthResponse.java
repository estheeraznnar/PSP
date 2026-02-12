// --- ServidorDeAutenticacionConcurrente/AuthResponse.java ---
package org.iesch.psp.ServidordeAutenticaciOnConcurrente;

import java.io.Serializable;

/**
 * Clase serializable que representa las respuestas del servidor
 * Similar a CalcResponse del ejemplo del PDF (página 40)
 */
public class AuthResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    // Atributos de la respuesta (inmutables)
    private final AuthRequest request;  // Petición original
    private final boolean ok;           // Si fue exitosa
    private final String mensaje;       // Mensaje descriptivo

    /**
     * Constructor con todos los atributos
     */
    public AuthResponse(AuthRequest request, boolean ok, String mensaje) {
        this.request = request;
        this.ok = ok;
        this.mensaje = mensaje;
    }

    // Métodos de acceso (solo lectura)
    public AuthRequest getRequest() { return request; }
    public boolean isOk() { return ok; }
    public String getMensaje() { return mensaje; }
}