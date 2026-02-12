// --- SistemaDeChatUDPConcurrenteConCifrado/MensajeChat.java ---
package org.iesch.psp.SistemaDeChatUDPConcurrenteConCifrado;

import java.io.Serializable;

/**
 * Clase serializable que representa los mensajes del chat
 * Similar a ChatMsg del PDF (página 22)
 * Añadimos cifrado AES al contenido del mensaje
 */
public class MensajeChat implements Serializable {

    private static final long serialVersionUID = 1L;

    // Tipos de mensaje del chat
    public enum TipoMensaje {
        ENTER,  // Usuario entra al chat
        MSG,    // Mensaje normal
        QUIT    // Usuario sale del chat
    }

    // Atributos del mensaje
    private String nick;           // Nombre o nick del usuario
    private TipoMensaje tipo;      // Tipo de mensaje
    private String contenido;      // Contenido (cifrado si es MSG)
    private boolean cifrado;       // Indica si el contenido está cifrado

    /**
     * Constructor para mensajes con contenido (MSG)
     */
    public MensajeChat(String nick, String contenido, boolean cifrado) {
        this.nick = nick;
        this.tipo = TipoMensaje.MSG;
        this.contenido = contenido;
        this.cifrado = cifrado;
    }

    /**
     * Constructor para ENTER y QUIT (sin contenido)
     */
    public MensajeChat(String nick, TipoMensaje tipo) {
        if (tipo.equals(TipoMensaje.MSG)) {
            throw new IllegalArgumentException("MSG requiere contenido");
        }
        this.nick = nick;
        this.tipo = tipo;
        this.contenido = "";
        this.cifrado = false;
    }

    // Getters
    public String getNick() { return nick; }
    public TipoMensaje getTipo() { return tipo; }
    public String getContenido() { return contenido; }
    public boolean isCifrado() { return cifrado; }
}