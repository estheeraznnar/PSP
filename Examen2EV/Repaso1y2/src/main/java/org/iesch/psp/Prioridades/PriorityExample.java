package org.iesch.psp.Prioridades;

public class PriorityExample extends Thread {

    public PriorityExample(String name) {
        this.setName(name);
    }

    public void run() {
        System.out.println("Ejecutando [" + this.getName() + "] - Prioridad: " + this.getPriority());
        for (int i = 0; i < 5; i++) {
            System.out.println("\t(" + this.getName() + ": " + i + ")");
        }
    }

    public static void main(String[] args) {
        // Crear hilos
        PriorityExample h1 = new PriorityExample("Bajo");
        PriorityExample h2 = new PriorityExample("Normal");
        PriorityExample h3 = new PriorityExample("Alto");

        // Establecer prioridades
        h1.setPriority(MIN_PRIORITY);
        h2.setPriority(NORM_PRIORITY);
        h3.setPriority(MAX_PRIORITY);

        // Ejecución
        h1.start();
        h2.start();
        h3.start();
    }
}