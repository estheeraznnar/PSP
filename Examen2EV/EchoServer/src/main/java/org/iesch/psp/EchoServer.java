package org.iesch.psp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

public class EchoServer {

    private static final int PORT = 12345;

    public static void main(String[] args) {
        // Arrancar el servidor
        try (ServerSocket svr = new ServerSocket(PORT);) {
            System.out.println("Server running at " + LocalDateTime.now());
            System.out.println("Server port: " + svr.getLocalPort());

            // Aceptar peticiones en bucle
            while (true) {
                // Crear nuevo thread para cada cliente
                Socket clientSocket = svr.accept();
                new Thread(new EchoServerThread(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Server stopped");
    }
}

// Hilo que atiende a cada cliente
class EchoServerThread implements Runnable {

    private Socket socket;

    public EchoServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
        ) {
            System.out.println("Client connected: " + socket.getInetAddress()
                    + ":" + socket.getPort());

            String inputLine;
            int messageCount = 0;

            // Leer mensajes del cliente y hacer echo
            while ((inputLine = in.readLine()) != null) {
                messageCount++;
                System.out.println("Message " + messageCount + " from "
                        + socket.getPort() + ": " + inputLine);

                // Enviar echo al cliente
                out.println("Echo: " + inputLine);

                // Si el cliente ha enviado 10 mensajes, terminar
                if (messageCount >= 10) {
                    System.out.println("Client " + socket.getPort()
                            + " completed 10 messages");
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Cerrar el socket
        try {
            socket.close();
            System.out.println("Connection closed with client: "
                    + socket.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}