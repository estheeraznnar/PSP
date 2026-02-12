package org.example.EJ1Repaso;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor que recibe mensajes cifrados de clientes
 * - Descifra los mensajes usando AES
 * - Almacena los mensajes en MessageData
 * - Si recibe "STATS", envía estadísticas y finaliza
 *
 * VERSIÓN INICIAL: Solo atiende un cliente a la vez
 */
public class SecureMessageServer {

    private static final int PUERTO = 5000;
    private static MessageData messageData = new MessageData();
    private static SecretKey claveAES = CryptoUtil.getClaveCompartida();
    private static boolean servidorActivo = true;

    public static void main(String[] args) {
        System.out.println("=== SERVIDOR DE MENSAJES SEGUROS ===");
        System.out.println("Puerto: " + PUERTO);
        System.out.println("Esperando clientes...\n");

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {

            while (servidorActivo) {
                // Aceptar conexión de cliente
                Socket cliente = serverSocket.accept();
                System.out.println("✓ Cliente conectado desde: " +
                        cliente.getInetAddress().getHostAddress());

                // Atender al cliente (bloqueante)
                atenderCliente(cliente);
            }

        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }

        System.out.println("\n=== SERVIDOR FINALIZADO ===");
    }

    /**
     * Atiende a un cliente individual
     * Lee mensajes cifrados, los descifra y almacena
     */
    private static void atenderCliente(Socket socket) {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String mensajeCifrado;

            // Leer mensajes del cliente
            while ((mensajeCifrado = in.readLine()) != null) {

                // Descifrar el mensaje
                String mensajeDescifrado = CryptoUtil.descifrar(mensajeCifrado, claveAES);

                System.out.println(" Mensaje recibido: " + mensajeDescifrado);

                // Verificar si es comando STATS
                if (mensajeDescifrado.equals("STATS")) {
                    System.out.println("\n Comando STATS recibido");

                    // Enviar estadísticas al cliente
                    String stats = messageData.getEstadisticas();
                    out.println(stats);

                    System.out.println(" Estadísticas enviadas:");
                    System.out.println(stats);

                    // Finalizar el servidor
                    servidorActivo = false;
                    break;
                }

                // Almacenar el mensaje
                messageData.agregarMensaje(mensajeDescifrado);

                // Enviar confirmación al cliente
                out.println("OK");
            }

        } catch (Exception e) {
            System.err.println("Error atendiendo cliente: " + e.getMessage());
        } finally {
            try {
                socket.close();
                System.out.println("✗ Cliente desconectado\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
