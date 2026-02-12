package org.iesch.psp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientLauncher {

    private static final int NUM_CLIENTS = 10;

    public static void main(String[] args) {
        System.out.println("Launching " + NUM_CLIENTS + " client processes...");

        List<Process> processes = new ArrayList<>();

        // Obtener el classpath actual
        String classpath = System.getProperty("java.class.path");
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";

        try {
            // Lanzar 10 procesos cliente independientes
            for (int i = 1; i <= NUM_CLIENTS; i++) {
                // Crear el comando para ejecutar cada cliente
                ProcessBuilder pb = new ProcessBuilder(
                        javaBin,
                        "-cp",
                        classpath,
                        "org.iesch.psp.EchoClient",  // Nombre completo de la clase
                        String.valueOf(i)  // Pasar el ID del cliente
                );

                // Redirigir la salida para poder verla
                pb.inheritIO();

                // Iniciar el proceso
                Process process = pb.start();
                processes.add(process);

                System.out.println("Client " + i + " process started");

                // Pequeña pausa entre lanzamientos
                Thread.sleep(100);
            }

            System.out.println("\nAll " + NUM_CLIENTS + " client processes launched!");
            System.out.println("Waiting for all clients to finish...\n");

            // Esperar a que todos los procesos terminen
            for (int i = 0; i < processes.size(); i++) {
                int exitCode = processes.get(i).waitFor();
                System.out.println("Client " + (i + 1) + " finished with exit code: " + exitCode);
            }

            System.out.println("\nAll clients completed!");

        } catch (IOException e) {
            System.err.println("Error launching processes: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting: " + e.getMessage());
            e.printStackTrace();
        }
    }
}