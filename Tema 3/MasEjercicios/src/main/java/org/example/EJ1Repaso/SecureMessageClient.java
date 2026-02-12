package org.example.EJ1Repaso;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;

/**
 * Cliente que se conecta al servidor y envía mensajes cifrados
 * - Cifra mensajes usando AES
 * - Envía el mensaje cifrado al servidor
 * - Espera confirmación del servidor
 */
public class SecureMessageClient {

    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;
    private static SecretKey claveAES = CryptoUtil.getClaveCompartida();

    /**
     * Envía un único mensaje al servidor
     */
    public static void enviarMensaje(String mensaje) {
        try (
                Socket socket = new Socket(HOST, PUERTO);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()))
        ) {
            // Cifrar el mensaje
            String mensajeCifrado = CryptoUtil.cifrar(mensaje, claveAES);

            // Enviar mensaje cifrado
            out.println(mensajeCifrado);

            // Esperar confirmación
            String confirmacion = in.readLine();
            System.out.println("Servidor responde: " + confirmacion);

        } catch (IOException e) {
            System.err.println("Error enviando mensaje: " + e.getMessage());
        }
    }

    /**
     * Envía múltiples mensajes al servidor
     */
    public static void enviarMensajes(int cantidad) {
        System.out.println("Cliente iniciando envío de " + cantidad + " mensajes...");

        try (
                Socket socket = new Socket(HOST, PUERTO);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()))
        ) {
            for (int i = 1; i <= cantidad; i++) {
                String mensaje = "Mensaje #" + i;

                // Cifrar el mensaje
                String mensajeCifrado = CryptoUtil.cifrar(mensaje, claveAES);

                // Enviar mensaje cifrado
                out.println(mensajeCifrado);

                // Esperar confirmación
                String confirmacion = in.readLine();

                if (i % 50 == 0) {
                    System.out.println("  → Enviados " + i + " mensajes...");
                }
            }

            System.out.println("Cliente finalizó envío de " + cantidad + " mensajes");

        } catch (IOException e) {
            System.err.println("Error enviando mensajes: " + e.getMessage());
        }
    }

    /**
     * Método main para pruebas individuales
     */
    public static void main(String[] args) {
        // Probar enviando 10 mensajes
        enviarMensajes(10);
    }
}
