package org.iesch.psp.ContadorSincronizadoCon3Hilos;

public class ThreeThreadsCounter implements Runnable {
    private static SynchronizedCounter counter = new SynchronizedCounter();
    private int increments;

    public ThreeThreadsCounter(int increments) {
        this.increments = increments;
    }

    public void run() {
        for (int i = 0; i < increments; i++) {
            counter.increment();
        }
    }

    public static void main(String[] args) {
        try {
            Thread t1 = new Thread(new ThreeThreadsCounter(50000));
            Thread t2 = new Thread(new ThreeThreadsCounter(50000));
            Thread t3 = new Thread(new ThreeThreadsCounter(50000));

            t1.start();
            t2.start();
            t3.start();

            t1.join();
            t2.join();
            t3.join();

            // Resultado esperado: 150000
            System.out.println("Valor final del contador: " + counter.getValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}