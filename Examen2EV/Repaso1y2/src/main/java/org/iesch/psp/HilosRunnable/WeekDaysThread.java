package org.iesch.psp.HilosRunnable;

public class WeekDaysThread implements Runnable {

    public void run() {
        String[] days = {"Lunes", "Martes", "Miércoles", "Jueves",
                "Viernes", "Sábado", "Domingo"};

        for (int i = 0; i < days.length; i++) {
            System.out.println(days[i]);
            try {
                // Pausar 2 segundos
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Hilo interrumpido");
            }
        }
        System.out.println("***Hilo finalizado***");
    }

    public static void main(String[] args) {
        // Crear nuevo hilo
        Thread t = new Thread(new WeekDaysThread());
        // Arrancar hilo
        t.start();
    }
}