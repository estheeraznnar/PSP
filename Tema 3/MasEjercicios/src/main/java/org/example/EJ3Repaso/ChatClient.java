package org.example.EJ3Repaso;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

/**
 * Cliente de chat multihilo.
 *
 * Usa DOS hilos:
 *  - Hilo principal: lee de consola, cifra y envía mensajes.
 *  - Hilo receptor: lee mensajes cifrados del servidor, descifra y muestra.
 *
 * Formato de comunicación:
 *  - Mensajes cifrados en Base64 (para poder enviar por socket de texto).
 */
public class ChatClient {

    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    // Clave AES compartida con el servidor (¡misma que en ChatServer!).
    private static SecretKey sharedKey;

    static {
        // Clave fija de 16 bytes para el ejemplo.
        // En un caso real se negociaría o se cargaría de un fichero seguro.
        byte[] keyBytes = "1234567890abcdef".getBytes(StandardCharsets.UTF_8);
        sharedKey = MessageCrypto.fromBytes(keyBytes);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== ChatClient ===");
        System.out.print("Nombre de usuario: ");
        String username = sc.nextLine().trim();

        try (
                Socket socket = new Socket(HOST, PORT);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), "UTF-8"));
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true)
        ) {
            // 1. El servidor pide el nombre
            String prompt = in.readLine();
            System.out.println("Servidor: " + prompt);

            // 2. Enviamos el nombre de usuario en texto plano
            out.println(username);

            // 3. Hilo receptor: lee mensajes cifrados del servidor
            Thread receiver = new Thread(() -> {
                try {
                    String base64Line;
                    while ((base64Line = in.readLine()) != null) {
                        // Base64 -> bytes cifrados
                        byte[] encrypted = Base64.getDecoder().decode(base64Line);
                        // Descifrar -> texto plano
                        String msg = MessageCrypto.decrypt(encrypted, sharedKey);
                        System.out.println(msg);
                    }
                } catch (Exception e) {
                    System.out.println("Conexión cerrada por el servidor.");
                }
            });
            receiver.setDaemon(true); // muere cuando el main muere
            receiver.start();

            // 4. Bucle principal: leer consola y enviar mensajes cifrados
            System.out.println("Escribe mensajes. /disconnect para salir.");
            while (true) {
                String line = sc.nextLine();
                if (line.trim().equalsIgnoreCase("/disconnect")) {
                    out.println("/disconnect");
                    break;
                }

                // Cifrar mensaje -> Base64 -> enviar
                byte[] encrypted = MessageCrypto.encrypt(line, sharedKey);
                String base64 = Base64.getEncoder().encodeToString(encrypted);
                out.println(base64);
            }

            System.out.println("Desconectando...");

        } catch (IOException e) {
            System.err.println("Error en ChatClient: " + e.getMessage());
        } finally {
            sc.close();
        }
    }
}
