package org.iesch.psp.ChatCifradoClienteServidor;// --- ServidorChat.java ---
import java.io.*;
import java.net.*;

public class ServidorChat {

    public static void main(String[] args) {
        int puerto = 9999;

        GestorClientes gestor = new GestorClientes();
        ColaMensajes colaBroadcast = new ColaMensajes();

        // Iniciar hilo de broadcast (Consumidor)
        Thread hiloBroadcast = new Thread(new HiloBroadcast(colaBroadcast, gestor));
        hiloBroadcast.setDaemon(true);
        hiloBroadcast.start();

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor de Chat Cifrado iniciado en puerto " + puerto);
            System.out.println("Esperando conexiones...\n");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nueva conexión desde " + clientSocket.getInetAddress());

                // Un hilo por cada cliente (Productor)
                new Thread(new HiloClienteChat(clientSocket, gestor, colaBroadcast)).start();
            }

        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }
}