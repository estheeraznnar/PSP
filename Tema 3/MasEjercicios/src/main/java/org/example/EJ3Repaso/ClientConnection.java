package org.example.EJ3Repaso;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.util.Base64;

/**
 * Representa la conexión de UN cliente en el servidor.
 * Se ejecuta en su propio hilo (implements Runnable).
 *
 * Responsabilidades:
 *  - Leer mensajes cifrados del cliente.
 *  - Descifrarlos y reenviarlos al ChatServer para broadcast.
 *  - Enviar mensajes cifrados al cliente.
 */
public class ClientConnection implements Runnable {

    private final ChatServer server;    // Referencia al servidor principal
    private final Socket socket;        // Socket TCP de esta conexión
    private final SecretKey key;        // Clave AES compartida
    private String username;            // Nombre del usuario de este cliente
    private BufferedReader in;          // Para leer del cliente
    private PrintWriter out;            // Para escribir al cliente

    public ClientConnection(ChatServer server, Socket socket, SecretKey key) {
        this.server = server;
        this.socket = socket;
        this.key = key;
    }

    public String getUsername() {
        return username;
    }

    /**
     * Envía un mensaje de texto plano al cliente (se cifra automáticamente).
     */
    public void sendEncrypted(String plainMessage) {
        try {
            // 1. Cifrar el mensaje
            byte[] encrypted = MessageCrypto.encrypt(plainMessage, key);

            // 2. Codificar en Base64 para poder enviarlo por socket como texto
            String base64 = Base64.getEncoder().encodeToString(encrypted);

            // 3. Enviar al cliente
            out.println(base64);
        } catch (Exception e) {
            System.err.println("Error enviando mensaje a " + username + ": " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // Abrir streams del socket
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

            // 1. Pedir nombre de usuario al cliente
            out.println("Introduce tu nombre de usuario:");
            username = in.readLine();
            if (username == null || username.isEmpty()) {
                username = "Anonimo";
            }
            System.out.println("Usuario conectado: " + username +
                    " desde " + socket.getInetAddress().getHostAddress());

            // Notificar a todos los demás que ha entrado alguien
            server.broadcast("** " + username + " se ha unido al chat **", this);

            // 2. Bucle principal: leer mensajes cifrados del cliente
            String base64Line;
            while ((base64Line = in.readLine()) != null) {
                // Comando especial /disconnect (en texto plano)
                if (base64Line.equals("/disconnect")) {
                    break;
                }

                try {
                    // Decodificar Base64 -> bytes cifrados
                    byte[] encrypted = Base64.getDecoder().decode(base64Line);

                    // Descifrar -> texto plano
                    String message = MessageCrypto.decrypt(encrypted, key);

                    // Si el usuario escribe /disconnect como mensaje, también salir
                    if (message.trim().equalsIgnoreCase("/disconnect")) {
                        break;
                    }

                    // Construir mensaje completo con nombre de usuario
                    String fullMessage = username + ": " + message;
                    System.out.println("Mensaje de " + username + ": " + message);

                    // Enviar al servidor para que lo distribuya
                    server.broadcast(fullMessage, this);

                } catch (Exception e) {
                    System.err.println("Error procesando mensaje de " + username + ": " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Error en conexión con cliente: " + e.getMessage());
        } finally {
            // 3. Desconexión ordenada
            server.removeClient(this);
            server.broadcast("** " + username + " se ha desconectado **", this);
            try {
                socket.close();
            } catch (IOException ignored) {}
            System.out.println("Cliente desconectado: " + username);
        }
    }
}
