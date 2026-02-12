package org.example.EJ3Repaso;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Servidor de chat multihilo con cifrado AES.
 *
 * Funcionamiento:
 *  - Escucha en puerto 5000 con ServerSocket.
 *  - Por cada cliente que se conecte:
 *      1) Crea un ClientConnection.
 *      2) Lo añade a la lista de clientes.
 *      3) Lanza un hilo para ese ClientConnection.
 *  - broadcast() envía un mensaje a todos menos al remitente.
 *  - Usa CopyOnWriteArrayList para thread-safety.
 */
public class ChatServer {

    private static final int PORT = 5000;

    // Lista de clientes conectados (CopyOnWriteArrayList es thread-safe
    // para iteraciones mientras otros hilos modifican la lista)
    private final List<ClientConnection> clients = new CopyOnWriteArrayList<>();

    // Clave AES compartida por todos los clientes.
    // En un caso real se negociaría con Diffie-Hellman.
    // En lugar de: private final SecretKey sharedKey = MessageCrypto.generateKey();
    private static SecretKey sharedKey;
    static {
        byte[] keyBytes = "1234567890abcdef".getBytes(StandardCharsets.UTF_8);
        sharedKey = MessageCrypto.fromBytes(keyBytes);
    }

    public static void main(String[] args) {
        new ChatServer().start();
    }

    /**
     * Bucle principal del servidor.
     */
    public void start() {
        System.out.println("=== ChatServer escuchando en puerto " + PORT + " ===");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                // Espera bloqueante a que un cliente se conecte
                Socket socket = serverSocket.accept();
                System.out.println("Nuevo cliente desde " + socket.getInetAddress().getHostAddress());

                // Crear objeto que representa la conexión del cliente
                ClientConnection clientConn = new ClientConnection(this, socket, sharedKey);

                // Añadir a la lista de clientes conectados
                clients.add(clientConn);

                // Lanzar hilo para este cliente
                Thread t = new Thread(clientConn);
                t.start();
            }
        } catch (IOException e) {
            System.err.println("Error en ChatServer: " + e.getMessage());
        }
    }

    /**
     * Envía un mensaje de texto plano a TODOS los clientes menos al remitente.
     *
     * @param message     Mensaje en texto plano.
     * @param fromClient  Cliente que envía el mensaje (se excluye).
     */
    public void broadcast(String message, ClientConnection fromClient) {
        System.out.println("Broadcast: " + message);
        for (ClientConnection client : clients) {
            // No reenviar al remitente original
            if (client != fromClient) {
                client.sendEncrypted(message);
            }
        }
    }

    /**
     * Elimina un cliente de la lista de conectados.
     */
    public void removeClient(ClientConnection clientConnection) {
        clients.remove(clientConnection);
    }
}


