// --- ServidorDeAutenticacionConcurrente/AuthRequest.java ---
package org.iesch.psp.ServidordeAutenticaciOnConcurrente;

import java.io.Serializable;

/**
 * Clase serializable que representa las peticiones de autenticación
 * Similar a CalcRequest del ejemplo del PDF (página 39)
 */
public class AuthRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    // Tipos de petición disponibles
    public enum RequestType {
        REGISTER,   // Registrar nuevo usuario
        LOGIN,      // Iniciar sesión
        LOGOUT      // Cerrar sesión
    }

    // Atributos de la petición (inmutables)
    private final RequestType type;
    private final String usuario;
    private final String password;

    /**
     * Constructor para peticiones con credenciales
     */
    public AuthRequest(RequestType type, String usuario, String password) {
        this.type = type;
        this.usuario = usuario;
        this.password = password;
    }

    /**
     * Constructor para LOGOUT (sin credenciales)
     */
    public AuthRequest(RequestType type) {
        this.type = type;
        this.usuario = null;
        this.password = null;
    }

    // Métodos de acceso (solo lectura)
    public RequestType getType() { return type; }
    public String getUsuario() { return usuario; }
    public String getPassword() { return password; }
}