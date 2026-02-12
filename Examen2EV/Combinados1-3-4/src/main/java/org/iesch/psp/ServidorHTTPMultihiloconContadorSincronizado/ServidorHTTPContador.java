package org.iesch.psp.ServidorHTTPMultihiloconContadorSincronizado;// --- ServidorHTTPContador.java ---
import java.io.*;
import java.net.*;

public class ServidorHTTPContador {

    // Contador compartido entre todos los hilos
    private static ContadorVisitas contador = new ContadorVisitas();

    public static void main(String[] args) {
        int puerto = 8080;

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor HTTP iniciado en puerto " + puerto);
            System.out.println("Endpoints disponibles:");
            System.out.println("  http://localhost:" + puerto + "/");
            System.out.println("  http://localhost:" + puerto + "/visitas");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Un hilo por cada petición
                new Thread(new ManejadorPeticion(clientSocket, contador)).start();
            }

        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }
}