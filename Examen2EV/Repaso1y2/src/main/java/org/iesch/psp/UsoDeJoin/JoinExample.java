package org.iesch.psp.UsoDeJoin;

public class JoinExample implements Runnable {
    private String name;

    public JoinExample(String name) {
        this.name = name;
    }

    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println(name + ": " + i);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(name + " finalizado");
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(new JoinExample("Hilo 1"));
        Thread t2 = new Thread(new JoinExample("Hilo 2"));

        try {
            // Arrancamos el primer hilo
            t1.start();
            // Esperamos a que termine el primer hilo
            t1.join();

            // Ahora arrancamos el segundo hilo
            t2.start();
            // Esperamos a que termine el segundo hilo
            t2.join();

            System.out.println("Ambos hilos han terminado");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}