package org.iesch.psp.ScheduledThreadPoolExecutorHora;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TimeWatcher {

    static class TimeTask implements Runnable {
        @Override
        public void run() {
            System.out.println("Hora actual: " + new Date());
        }
    }

    public static void main(String[] args) {
        // Creamos un ScheduledThreadPool de un hilo
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        // Creamos la tarea
        TimeTask task = new TimeTask();

        // Mostramos la hora de inicio
        System.out.println("Inicio: " + new Date());

        // Lanzamos la ejecución cada 3 segundos
        ScheduledFuture<?> result = executor.scheduleAtFixedRate(task, 0, 3, TimeUnit.SECONDS);

        // Esperamos 30 segundos antes de terminar
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Terminamos la ejecución del thread pool
        executor.shutdown();
        System.out.println("Fin: " + new Date());
    }
}