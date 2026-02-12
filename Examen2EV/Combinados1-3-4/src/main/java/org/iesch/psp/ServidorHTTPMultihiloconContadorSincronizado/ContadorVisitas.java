package org.iesch.psp.ServidorHTTPMultihiloconContadorSincronizado;

// --- ContadorVisitas.java (Clase sincronizada) ---
public class ContadorVisitas {
    private int contador = 0;

    public synchronized void incrementar() {
        contador++;
    }

    public synchronized int getValor() {
        return contador;
    }
}