package org.example.EJCorreosHilos;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Clase para llevar estadísticas del envío de correos.
 *
 * Se utilizan AtomicInteger para que las operaciones de incremento sean
 * seguras en entornos multihilo (varios hilos actualizando a la vez).
 */
public class MailStats {

    private final AtomicInteger ok = new AtomicInteger(0);
    private final AtomicInteger fail = new AtomicInteger(0);

    /**
     * Incrementa el contador de correos enviados correctamente.
     */
    public void incOk() {
        ok.incrementAndGet();
    }

    /**
     * Incrementa el contador de correos fallidos.
     */
    public void incFail() {
        fail.incrementAndGet();
    }

    public int getOk() {
        return ok.get();
    }

    public int getFail() {
        return fail.get();
    }
}
