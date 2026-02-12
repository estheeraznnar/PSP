package org.example.EJ2Repaso;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Clase para hacer una prueba de estrés del servidor.
 * Lanza 20 clientes simultáneos que:
 *   - Se conectan
 *   - Hacen LOGIN (user1/password1)
 *   - Hacen UPLOAD de un archivo cada uno
 */
public class FTPStressTest {

    public static void main(String[] args) {
        final int NUM_CLIENTES = 20;

        // Pool de hilos para ejecutar los 20 clientes a la vez
        ExecutorService pool = Executors.newFixedThreadPool(NUM_CLIENTES);

        for (int i = 0; i < NUM_CLIENTES; i++) {
            final int id = i;  // id del cliente

            pool.execute(() -> {
                FTPClient client = new FTPClient("localhost", 2121);
                String filename = "file_" + id + ".txt";
                String content = "Contenido de prueba del cliente " + id + "\nLínea 2\nLínea 3";
                client.uploadTestFile("user1", "password1", filename, content);
            });
        }

        // Cerramos el pool y esperamos a que terminen todos
        pool.shutdown();
        try {
            if (!pool.awaitTermination(3, TimeUnit.MINUTES)) {
                System.out.println("Timeout en stress test.");
            } else {
                System.out.println("Stress test completado.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
