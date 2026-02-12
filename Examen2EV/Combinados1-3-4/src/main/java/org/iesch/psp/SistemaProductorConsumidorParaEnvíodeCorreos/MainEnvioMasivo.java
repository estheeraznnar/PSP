package org.iesch.psp.SistemaProductorConsumidorParaEnvíodeCorreos;// --- MainEnvioMasivo.java ---
import java.util.*;
import java.util.concurrent.*;

public class MainEnvioMasivo {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== SISTEMA DE ENVÍO MASIVO DE CORREOS ===\n");

        System.out.println("Archivo con lista de correos:");
        String archivo = sc.nextLine();
        System.out.println("Tu correo (Gmail):");
        String remitente = sc.nextLine();
        System.out.println("Tu contraseña de aplicación:");
        String password = sc.nextLine();
        System.out.println("Asunto del correo:");
        String asunto = sc.nextLine();
        System.out.println("Cuerpo del mensaje:");
        String cuerpo = sc.nextLine();

        // Cola con capacidad para 10 correos
        ColaCorreos cola = new ColaCorreos(10);

        // Crear productor
        Thread productor = new Thread(new ProductorCorreos(cola, archivo));

        // Crear pool de 3 consumidores
        List<ConsumidorCorreos> consumidores = new ArrayList<>();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

        for (int i = 0; i < 3; i++) {
            ConsumidorCorreos c = new ConsumidorCorreos(cola, remitente, password, asunto, cuerpo);
            consumidores.add(c);
        }

        long inicio = System.currentTimeMillis();

        // Iniciar productor
        productor.start();

        // Iniciar consumidores
        List<Future<?>> futures = new ArrayList<>();
        for (ConsumidorCorreos c : consumidores) {
            futures.add(executor.submit(c));
        }

        // Esperar a que terminen
        try {
            productor.join();
            for (Future<?> f : futures) {
                f.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        executor.shutdown();

        long fin = System.currentTimeMillis();

        // Estadísticas
        int totalEnviados = 0;
        int totalErrores = 0;
        for (ConsumidorCorreos c : consumidores) {
            totalEnviados += c.getEnviados();
            totalErrores += c.getErrores();
        }

        System.out.println("\n=== ESTADÍSTICAS ===");
        System.out.println("Correos enviados: " + totalEnviados);
        System.out.println("Errores: " + totalErrores);
        System.out.println("Tiempo total: " + (fin - inicio) + " ms");
    }
}