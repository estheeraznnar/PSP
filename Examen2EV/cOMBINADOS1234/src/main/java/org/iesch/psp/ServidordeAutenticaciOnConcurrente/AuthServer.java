// --- ServidorDeAutenticacionConcurrente/AuthServer.java ---
package org.iesch.psp.ServidordeAutenticaciOnConcurrente;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;

/**
 * Servidor de autenticación que acepta múltiples clientes
 * Basado en CubeMultiSvr del PDF (página 36) y CalcSvr (página 45)
 *
 * Combina:
 * - Tema 1: Hilos para atender múltiples clientes
 * - Tema 2: Sockets TCP para comunicación
 * - Tema 4: Hash PBKDF2 para contraseñas
 */
public class AuthServer {

    public static void main(String[] args) {
        // Puerto del servidor
        int port;

        // Obtener puerto de la línea de comandos
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception e) {
            System.out.println("Uso: java AuthServer <puerto>");
            return;
        }

        // Base de datos compartida entre todos los hilos (thread-safe)
        BaseDatosUsuarios bd = new BaseDatosUsuarios();

        // Crear el ServerSocket con try-with-resources
        try (ServerSocket svr = new ServerSocket(port)) {

            // Añadir timeout para poder responder a señales de parada
            // Como en el ejemplo CubeMultiSvr (página 36)
            svr.setSoTimeout(3000);

            System.out.println("========================================");
            System.out.println("Servidor de Autenticación iniciado");
            System.out.println("Puerto: " + svr.getLocalPort());
            System.out.println("Hora: " + LocalDateTime.now());
            System.out.println("========================================");
            System.out.println("Esperando conexiones...\n");

            // Bucle infinito aceptando conexiones
            while (true) {
                try {
                    // Aceptar conexión de cliente
                    // Esto bloquea hasta que llegue un cliente o timeout
                    // Crear nuevo hilo para atender al cliente
                    new Thread(new AuthServerThread(svr.accept(), bd)).start();

                } catch (SocketTimeoutException e) {
                    // Timeout - no hacer nada, volver a esperar
                    // Esto permite que el servidor responda a señales
                }
            }

        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}