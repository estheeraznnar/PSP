package org.iesch.psp.Semaforos;

import java.util.concurrent.Semaphore;

public class SemaphoreExample implements Runnable {
    private static final int AVAILABLE_THREADS = 2;
    private static final Semaphore semaphore = new Semaphore(AVAILABLE_THREADS);
    private final String name;

    public SemaphoreExample(String name) {
        this.name = name;
    }

    public void run() {
        try {
            semaphore.acquire();
            System.out.println("Ejecutando proceso: " + name);
            // Simular trabajo
            Thread.sleep(2000);
            System.out.println("Fin: " + name);
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Lanzamos 6 procesos
        for (int i = 0; i < 6; i++) {
            new Thread(new SemaphoreExample("Hilo-" + (i + 1))).start();
        }
    }
}