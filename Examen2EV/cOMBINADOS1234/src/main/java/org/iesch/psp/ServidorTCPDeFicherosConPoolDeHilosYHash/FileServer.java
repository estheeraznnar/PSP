// --- ServidorTCPDeFicherosConPoolDeHilosYHash/FileServer.java ---
package org.iesch.psp.ServidorTCPDeFicherosConPoolDeHilosYHash;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.List;

/**
 * Servidor TCP de ficheros con ThreadPoolExecutor
 *
 * Combina:
 * - Tema 1: ThreadPoolExecutor para gestionar hilos
 * - Tema 2: ServerSocket y Socket TCP
 * - Tema 4: Hash SHA-256 para verificar integridad
 */
public class FileServer {

    // Número máximo de hilos en el pool
    private static final int MAX_THREADS = 4;

    public static void main(String[] args) {
        // Obtener parámetros
        int port;
        String dirPath;

        try {
            port = Integer.parseInt(args[0]);
            dirPath = args.length > 1 ? args[1] : ".";
        } catch (Exception e) {
            System.out.println("Uso: java FileServer <puerto> [directorio]");
            return;
        }

        // Verificar directorio
        File directorio = new File(dirPath);
        if (!directorio.isDirectory()) {
            System.out.println("El directorio no existe: " + dirPath);
            return;
        }

        // Lista para guardar los Futures de las tareas
        List<Future<String>> tareas = new ArrayList<>();

        // Crear ThreadPoolExecutor con número fijo de hilos
        // Como en el ejemplo del Tema 1
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

        try (ServerSocket svr = new ServerSocket(port)) {

            // Configurar timeout
            svr.setSoTimeout(3000);

            System.out.println("========================================");
            System.out.println("Servidor de Ficheros TCP");
            System.out.println("Puerto: " + port);
            System.out.println("Directorio: " + directorio.getAbsolutePath());
            System.out.println("Pool de hilos: " + MAX_THREADS);
            System.out.println("Hora: " + LocalDateTime.now());
            System.out.println("========================================");
            System.out.println("Esperando conexiones...\n");

            // Bucle principal
            while (true) {
                try {
                    // Aceptar cliente y enviar tarea al pool
                    Future<String> future = executor.submit(
                            new FileServerThread(svr.accept(), directorio)
                    );
                    tareas.add(future);

                    // Limpiar tareas completadas
                    tareas.removeIf(Future::isDone);

                } catch (SocketTimeoutException e) {
                    // Timeout - permite verificar condiciones
                }
            }

        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        } finally {
            // Cerrar el pool de hilos
            executor.shutdown();
        }
    }
}