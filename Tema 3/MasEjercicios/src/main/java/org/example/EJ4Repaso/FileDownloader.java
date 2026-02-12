package org.example.EJ4Repaso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Clase que se encarga de gestionar múltiples descargas concurrentes.
 *
 * - Recibe una lista de URLs.
 * - Crea un pool de 5 hilos (ExecutorService).
 * - Para cada URL crea una DownloadTask y la envía al pool.
 * - Espera a que terminen todas las tareas.
 * - Mide el tiempo total de ejecución.
 */
public class FileDownloader {

    private final DownloadStats stats = new DownloadStats();
    private final List<DownloadTask> tasks = new ArrayList<>();

    /**
     * Descarga todos los archivos de la lista de URLs.
     */
    public void downloadAll(List<String> urls) {
        // Pool fijo de 5 hilos
        ExecutorService pool = Executors.newFixedThreadPool(5);

        long start = System.currentTimeMillis();

        int index = 1;
        for (String url : urls) {
            // Generar un nombre de archivo local a partir del índice y extensión
            File output = new File("file_" + index + getExtensionFromUrl(url));

            // Crear la tarea de descarga
            DownloadTask task = new DownloadTask(url, output, stats);

            // Guardamos la tarea para poder consultar su hash más tarde
            tasks.add(task);

            // Ejecutar la tarea en el pool
            pool.execute(task);

            index++;
        }

        // No se aceptan más tareas
        pool.shutdown();

        try {
            // Esperar hasta 10 minutos a que todas las descargas terminen
            if (!pool.awaitTermination(10, TimeUnit.MINUTES)) {
                System.err.println("Timeout esperando descargas.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        long elapsed = end - start;

        System.out.println("\n=== DESCARGAS COMPLETADAS ===");
        System.out.println(stats);
        System.out.println("Tiempo total: " + elapsed + " ms (" + (elapsed / 1000.0) + " s)");
    }

    /**
     * Devuelve la lista de tareas (para poder leer los hashes calculados).
     */
    public List<DownloadTask> getTasks() {
        return tasks;
    }

    /**
     * Extrae la extensión de archivo de la URL, si existe.
     * Si no tiene extensión, se usa ".dat".
     */
    private String getExtensionFromUrl(String url) {
        int idxDot = url.lastIndexOf('.');
        int idxSlash = url.lastIndexOf('/');

        // Si no hay punto o el punto está antes de la última '/', no lo consideramos extensión
        if (idxDot == -1 || idxDot < idxSlash) {
            return ".dat";
        }

        return url.substring(idxDot);
    }
}
