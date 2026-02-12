package org.iesch.psp.SistemaDeBackupCifradoConcurrente;// --- MainBackupCifrado.java ---
import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public class MainBackupCifrado {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== SISTEMA DE BACKUP CIFRADO CONCURRENTE ===\n");

        System.out.println("Directorio a respaldar:");
        String directorioLocal = sc.nextLine();
        System.out.println("Servidor FTP:");
        String servidor = sc.nextLine();
        System.out.println("Usuario:");
        String usuario = sc.nextLine();
        System.out.println("Contraseña:");
        String password = sc.nextLine();

        File carpeta = new File(directorioLocal);
        if (!carpeta.isDirectory()) {
            System.out.println("El directorio no existe.");
            return;
        }

        File[] archivos = carpeta.listFiles(f -> f.isFile());
        if (archivos == null || archivos.length == 0) {
            System.out.println("No hay archivos para respaldar.");
            return;
        }

        System.out.println("\nArchivos a respaldar: " + archivos.length);

        try {
            // Log sincronizado
            LogSincronizado log = new LogSincronizado("backup_log.txt");

            // Semáforo para limitar conexiones FTP (máximo 2)
            Semaphore semaforo = new Semaphore(2);

            // Pool de 4 hilos trabajadores
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

            // Crear tareas
            List<TareaBackup> tareas = new ArrayList<>();
            for (File archivo : archivos) {
                tareas.add(new TareaBackup(archivo, servidor, usuario, password, semaforo, log));
            }

            log.escribir("=== INICIO DEL BACKUP ===");
            long inicio = System.currentTimeMillis();

            // Ejecutar todas las tareas
            List<Future<String>> resultados = executor.invokeAll(tareas);

            // Mostrar resultados
            int exitosos = 0;
            int errores = 0;

            log.escribir("=== RESULTADOS ===");
            for (Future<String> resultado : resultados) {
                String res = resultado.get();
                log.escribir(res);
                if (res.startsWith("OK")) {
                    exitosos++;
                } else {
                    errores++;
                }
            }

            executor.shutdown();

            long fin = System.currentTimeMillis();

            log.escribir("=== ESTADÍSTICAS ===");
            log.escribir("Archivos exitosos: " + exitosos);
            log.escribir("Errores: " + errores);
            log.escribir("Tiempo total: " + (fin - inicio) + " ms");
            log.escribir("=== FIN DEL BACKUP ===");

            log.cerrar();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}