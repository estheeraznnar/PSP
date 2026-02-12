package org.iesch.psp.ChatCifradoClienteServidor;// --- MensajeCifrado.java (Objeto serializable) ---
import java.io.Serializable;

public class MensajeCifrado implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum TipoMensaje {
        CLAVE_PUBLICA,  // Intercambio de clave pública
        MENSAJE,        // Mensaje cifrado
        SALIR           // Desconexión
    }

    private TipoMensaje tipo;
    private String remitente;
    private String contenido; // Clave pública o mensaje cifrado

    public MensajeCifrado(TipoMensaje tipo, String remitente, String contenido) {
        this.tipo = tipo;
        this.remitente = remitente;
        this.contenido = contenido;
    }

    public TipoMensaje getTipo() { return tipo; }
    public String getRemitente() { return remitente; }
    public String getContenido() { return contenido; }
}