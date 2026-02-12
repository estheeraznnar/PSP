package org.example.EJ1Repaso;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;

/**
 * TAREA 1: Cliente que envía comando STATS al servidor
 *
 * Funcionalidad:
 * - Se conecta al servidor SecureMessageServer
 * - Envía el comando "STATS" (cifrado)
 * - Recibe y muestra las estadísticas
 * - El servidor debe finalizar su ejecución tras esto
 */
public class SecureMessageStopper {

    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;
    private static SecretKey claveAES = CryptoUtil.getClaveCompartida();

    public static void main(String[] args) {
        System.out.println("=== SECURE MESSAGE STOPPER ===");
        System.out.println("Enviando comando STATS al servidor...\n");

        try (
                Socket socket = new Socket(HOST, PUERTO);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()))
        ) {
            // Cifrar el comando STATS
            String comandoCifrado = CryptoUtil.cifrar("STATS", claveAES);

            // Enviar comando al servidor
            out.println(comandoCifrado);
            System.out.println("✓ Comando STATS enviado");

            // Leer estadísticas del servidor
            String linea;
            System.out.println("\n ESTADÍSTICAS DEL SERVIDOR:");
            System.out.println("================================");

            while ((linea = in.readLine()) != null) {
                System.out.println(linea);
            }

            System.out.println("================================");
            System.out.println("\n Servidor ha finalizado su ejecución");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("\n  Asegúrate de que el servidor está ejecutándose");
        }
    }
}
