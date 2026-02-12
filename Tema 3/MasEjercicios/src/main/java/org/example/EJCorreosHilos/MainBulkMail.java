package org.example.EJCorreosHilos;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Programa principal para envío masivo de correos.
 *
 * Flujo:
 *   1) Pide por consola los datos de SMTP, remitente, destinatario, etc.
 *   2) Crea un MailStats para contar OK / fallos.
 *   3) Crea un pool de hilos (ExecutorService).
 *   4) Lanza N tareas MailTask, todas al mismo destinatario.
 *   5) Espera a que terminen todos los hilos.
 *   6) Muestra el resumen final.
 */
public class MainBulkMail {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== ENVÍO MASIVO DE CORREOS SMTP (CON HILOS) ===");

        System.out.print("Servidor SMTP (ej: smtp.gmail.com): ");
        String host = sc.nextLine().trim();

        System.out.print("Puerto SMTP (ej: 587): ");
        int port = Integer.parseInt(sc.nextLine().trim());

        System.out.print("Correo REMITENTE: ");
        String from = sc.nextLine().trim();

        System.out.print("Contraseña / password de aplicación: ");
        String password = sc.nextLine().trim();

        System.out.print("Correo DESTINATARIO: ");
        String to = sc.nextLine().trim();

        System.out.print("Número de correos a enviar: ");
        int n = Integer.parseInt(sc.nextLine().trim());

        System.out.print("Número de hilos en paralelo (ej: 5): ");
        int threads = Integer.parseInt(sc.nextLine().trim());

        System.out.print("Asunto base: ");
        String subjectBase = sc.nextLine().trim();

        System.out.print("Cuerpo base del mensaje: ");
        String bodyBase = sc.nextLine().trim();

        sc.close();

        // Objeto para acumular estadísticas de todos los hilos
        MailStats stats = new MailStats();

        // Pool de hilos con tamaño configurable
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        long t0 = System.currentTimeMillis();

        // Creamos N tareas, cada una envía un correo
        for (int i = 1; i <= n; i++) {
            String subject = subjectBase + " #" + i;
            String body = bodyBase + "\n\nEste es el mensaje número " + i;
            pool.execute(new MailTask(host, port, from, password, to, subject, body, stats));
        }

        // Cerramos el pool (no acepta más tareas) y esperamos a que terminen
        pool.shutdown();
        try {
            pool.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long t1 = System.currentTimeMillis();

        // Resumen final
        System.out.println("\n=== RESUMEN ENVÍO MASIVO ===");
        System.out.println("Total pedidos: " + n);
        System.out.println("Enviados OK:  " + stats.getOk());
        System.out.println("Fallidos:     " + stats.getFail());
        System.out.println("Tiempo total: " + (t1 - t0) + " ms (" + ((t1 - t0) / 1000.0) + " s)");
    }
}
