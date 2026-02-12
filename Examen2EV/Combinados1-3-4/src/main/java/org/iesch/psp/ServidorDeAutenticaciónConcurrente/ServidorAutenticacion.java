package org.iesch.psp.ServidorDeAutenticaciónConcurrente;// --- ServidorAutenticacion.java (Main) ---
import java.io.*;
import java.net.*;

public class ServidorAutenticacion {

    public static void main(String[] args) {
        int puerto = 9000;

        // Base de datos compartida entre todos los hilos
        BaseDatosUsuarios bd = new BaseDatosUsuarios();

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor de autenticación iniciado en puerto " + puerto);
            System.out.println("Esperando conexiones...\n");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde " + clientSocket.getInetAddress());

                // Un hilo por cada cliente
                new Thread(new HiloCliente(clientSocket, bd)).start();
            }

        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }
}