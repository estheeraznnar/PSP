package org.iesch.psp.CountDownLatch;

import java.util.concurrent.CountDownLatch;

public class CountDownExample implements Runnable {
    private String msg;
    private CountDownLatch countDown;

    public CountDownExample(String msg, CountDownLatch countDown) {
        this.msg = msg;
        this.countDown = countDown;
    }

    public void run() {
        try {
            Thread.sleep(1000);
            System.out.println(msg + " completado");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        countDown.countDown();
    }

    public static void main(String[] args) {
        System.out.println("*** Inicio ***");

        // CountDownLatch que esperará 3 hilos de preparación
        final CountDownLatch preparationLatch = new CountDownLatch(3);

        // Lanzamos los 3 hilos de preparación
        new Thread(new CountDownExample("Preparación 1", preparationLatch)).start();
        new Thread(new CountDownExample("Preparación 2", preparationLatch)).start();
        new Thread(new CountDownExample("Preparación 3", preparationLatch)).start();

        try {
            // Esperamos a que los 3 hilos de preparación terminen
            preparationLatch.await();

            // Ahora ejecutamos el hilo principal
            System.out.println("Todos los hilos de preparación completados");
            System.out.println("Ejecutando tarea principal...");
            Thread.sleep(1000);
            System.out.println("Tarea principal completada");

            System.out.println("*** Fin ***");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}