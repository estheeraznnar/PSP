// --- SistemaDeTareasDistribuidasUDPConProductorConsumidor/Main.java ---
package org.iesch.psp.SistemaDeTareasDistribuidasUDPConProductorConsumidor;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Programa principal que coordina el sistema distribuido
 *
 * Combina:
 * - Tema 1: Productor-Consumidor con cola sincronizada, múltiples hilos
 * - Tema 2: Sockets UDP para comunicación de resultados
 */
public class Main {

    // Configuración
    private static final int NUM_TAREAS = 20;
    private static final int NUM_CONSUMIDORES = 3;
    private static final int CAPACIDAD_COLA = 5;
    private static final int PUERTO_RECOLECTOR = 9999;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Sistema de Tareas Distribuidas");
        System.out.println("Tareas: " + NUM_TAREAS);
        System.out.println("Consumidores: " + NUM_CONSUMIDORES);
        System.out.println("========================================\n");

        try {
            // Dirección localhost para el recolector
            InetAddress localhost = InetAddress.getLocalHost();

            // Crear cola compartida (con capacidad limitada)
            ColaTareas cola = new ColaTareas(CAPACIDAD_COLA);

            // Crear y arrancar el recolector (recibe resultados UDP)
            Recolector recolector = new Recolector(PUERTO_RECOLECTOR, NUM_TAREAS);
            Thread hiloRecolector = new Thread(recolector);
            hiloRecolector.start();

            // Esperar a que el recolector esté listo
            Thread.sleep(500);

            // Crear y arrancar el productor
            Productor productor = new Productor(cola, NUM_TAREAS);
            Thread hiloProductor = new Thread(productor);
            hiloProductor.start();

            // Crear y arrancar los consumidores
            List<Thread> hilosConsumidores = new ArrayList<>();
            List<Consumidor> consumidores = new ArrayList<>();

            for (int i = 1; i <= NUM_CONSUMIDORES; i++) {
                Consumidor consumidor = new Consumidor(
                        "Consumidor-" + i,
                        cola,
                        localhost,
                        PUERTO_RECOLECTOR
                );
                consumidores.add(consumidor);

                Thread hilo = new Thread(consumidor);
                hilosConsumidores.add(hilo);
                hilo.start();
            }

            // Esperar a que termine el productor
            hiloProductor.join();

            // Esperar a que terminen los consumidores
            for (Thread hilo : hilosConsumidores) {
                hilo.join();
            }

            // Esperar a que termine el recolector
            hiloRecolector.join();

            // Mostrar estadísticas
            System.out.println("\n========================================");
            System.out.println("ESTADÍSTICAS FINALES");
            System.out.println("========================================");

            // Tareas por consumidor
            System.out.println("\nTareas procesadas por consumidor:");
            for (Consumidor c : consumidores) {
                System.out.println("  " + c.getProcesadas() + " tareas");
            }

            // Resumen de resultados
            System.out.println("\nTotal resultados recibidos: "
                    + recolector.getResultados().size());

            // Tiempo promedio
            long tiempoTotal = 0;
            for (Resultado r : recolector.getResultados()) {
                tiempoTotal += r.getTiempoMs();
            }
            System.out.println("Tiempo promedio por tarea: "
                    + (tiempoTotal / NUM_TAREAS) + " ms");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}