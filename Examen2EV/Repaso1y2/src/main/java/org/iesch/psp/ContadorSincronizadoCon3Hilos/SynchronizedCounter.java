package org.iesch.psp.ContadorSincronizadoCon3Hilos;

public class SynchronizedCounter {
    private int c = 0;

    public synchronized void increment() {
        c++;
    }

    public synchronized int getValue() {
        return c;
    }
}