package org.example.EJ1Repaso;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * TAREA 3: Lanzador que crea 50 clientes simultáneos
 * Cada cliente envía 200 mensajes cifrados
 * Total esperado: 50 × 200 = 10,000 mensajes
 */
public class SecureClientLauncher {

    private static final int NUMERO_CLIENTES = 50;
    private static final int MENSAJES_POR_CLIENTE = 200;

    public static void main(String[] args) {
        System.out.println("=== SECURE CLIENT LAUNCHER ===");
        System.out.println("Número de clientes: " + NUMERO_CLIENTES);
        System.out.println("Mensajes por cliente: " + MENSAJES_POR_CLIENTE);
        System.out.println("Total mensajes esperados: " +
                (NUMERO_CLIENTES * MENSAJES_POR_CLIENTE));
        System.out.println("\nIniciando clientes...\n");

        // Pool de hilos para lanzar los clientes
        ExecutorService poolClientes = Executors.newFixedThreadPool(NUMERO_CLIENTES);

        long tiempoInicio = System.currentTimeMillis();

        // Lanzar los 50 clientes
        for (int i = 1; i <= NUMERO_CLIENTES; i++) {
            final int clienteId = i;

            poolClientes.execute(() -> {
                System.out.println("Cliente #" + clienteId + " iniciado");
                SecureMessageClient.enviarMensajes(MENSAJES_POR_CLIENTE);
                System.out.println("Cliente #" + clienteId + " finalizado");
            });
        }

        // Cerrar el pool y esperar a que terminen todos
        poolClientes.shutdown();

        try {
            // Esperar hasta 5 minutos
            if (poolClientes.awaitTermination(5, TimeUnit.MINUTES)) {
                long tiempoTotal = System.currentTimeMillis() - tiempoInicio;

                System.out.println("\n=== RESUMEN ===");
                System.out.println("✓ Todos los clientes finalizaron correctamente");
                System.out.println("Tiempo total: " + tiempoTotal + " ms");
                System.out.println("(" + (tiempoTotal / 1000.0) + " segundos)");
                System.out.println("\nAhora ejecuta SecureMessageStopper para ver las estadísticas");
            } else {
                System.err.println(" Timeout: Los clientes tardaron demasiado");
            }
        } catch (InterruptedException e) {
            System.err.println("Error esperando clientes: " + e.getMessage());
        }
    }
}
