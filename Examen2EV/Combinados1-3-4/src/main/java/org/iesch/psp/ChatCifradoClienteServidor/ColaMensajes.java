package org.iesch.psp.ChatCifradoClienteServidor;// --- ColaMensajes.java (BlockingQueue para broadcast) ---
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ColaMensajes {

    private BlockingQueue<MensajeCifrado> cola = new LinkedBlockingQueue<>();

    public void poner(MensajeCifrado mensaje) {
        try {
            cola.put(mensaje);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public MensajeCifrado tomar() {
        try {
            return cola.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}