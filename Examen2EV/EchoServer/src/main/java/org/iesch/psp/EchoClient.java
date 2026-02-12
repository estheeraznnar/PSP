package org.iesch.psp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoClient {

    private static final int MAX_MESSAGES = 10;
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        // Obtener el ID del cliente (si se pasa como argumento)
        int clientId = 0;
        if (args.length > 0) {
            try {
                clientId = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                clientId = 0;
            }
        }

        // Conectar al servidor
        try (
                Socket socket = new Socket(HOST, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
        ) {
            // Mostrar información de la conexión
            System.out.println("Client " + clientId + " connected to "
                    + socket.getInetAddress() + ":" + socket.getPort());

            // Enviar exactamente 10 mensajes
            for (int i = 1; i <= MAX_MESSAGES; i++) {
                String message = "Client " + clientId + " - Message " + i;

                // Enviar mensaje al servidor
                System.out.println("[Client " + clientId + "] Sending: " + message);
                out.println(message);

                // Recibir el echo del servidor
                String response = in.readLine();
                System.out.println("[Client " + clientId + "] Received: " + response);

                // Pequeña pausa entre mensajes
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Client " + clientId + " finished - All "
                    + MAX_MESSAGES + " messages sent");

        } catch (IOException e) {
            System.err.println("Client " + clientId + " error: " + e.getMessage());
        }
    }
}