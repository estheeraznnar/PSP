package org.iesch.psp.Interrupciones;

public class WeekDaysInterrupt implements Runnable {

    public void run() {
        String[] days = {"Lunes", "Martes", "Miércoles", "Jueves",
                "Viernes", "Sábado", "Domingo"};

        for (int i = 0; i < days.length; i++) {
            System.out.println(days[i]);
            try {
                // Pausar 2 segundos
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Hilo interrumpido - Terminando ejecución");
                return;
            }
        }
        System.out.println("***Hilo finalizado***");
    }

    public static void main(String[] args) throws InterruptedException {
        // Crear nuevo hilo
        Thread t = new Thread(new WeekDaysInterrupt());
        // Arrancar hilo
        t.start();

        // Dejar pasar 5 segundos e interrumpir el hilo
        Thread.sleep(5000);
        t.interrupt();

        System.out.println("Main finalizado");
    }
}