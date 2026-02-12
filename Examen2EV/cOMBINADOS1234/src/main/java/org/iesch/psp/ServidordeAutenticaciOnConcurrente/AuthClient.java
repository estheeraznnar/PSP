// --- ServidorDeAutenticacionConcurrente/AuthClient.java ---
package org.iesch.psp.ServidordeAutenticaciOnConcurrente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Cliente de autenticación
 * Basado en CalcCli del PDF (páginas 41-44)
 */
public class AuthClient {

    public static void main(String[] args) {
        // Datos de conexión al servidor
        String host;
        int port;

        // Obtener datos de la línea de comandos
        try {
            host = args[0];
            port = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.out.println("Uso: java AuthClient <host> <puerto>");
            return;
        }

        // Conectar al servidor usando try-with-resources
        // Como en el ejemplo CubeCli (página 34)
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             Scanner sc = new Scanner(System.in)) {

            // Mostrar información de conexión
            System.out.println("========================================");
            System.out.println("Conectado a " + socket.getInetAddress()
                    + ":" + socket.getPort());
            System.out.println("========================================\n");

            boolean continuar = true;

            // Bucle principal del cliente
            while (continuar) {
                // Mostrar menú de opciones
                System.out.println("\n--- MENÚ ---");
                System.out.println("1. Registrar usuario");
                System.out.println("2. Iniciar sesión");
                System.out.println("0. Salir");
                System.out.print("Opción: ");

                // Leer opción del usuario
                String opcion = sc.nextLine().trim();

                AuthRequest req = null;

                switch (opcion) {
                    case "1": // REGISTER
                        System.out.print("Usuario: ");
                        String nuevoUsuario = sc.nextLine().trim();
                        System.out.print("Contraseña: ");
                        String nuevaPassword = sc.nextLine().trim();

                        req = new AuthRequest(
                                AuthRequest.RequestType.REGISTER,
                                nuevoUsuario,
                                nuevaPassword
                        );
                        break;

                    case "2": // LOGIN
                        System.out.print("Usuario: ");
                        String usuario = sc.nextLine().trim();
                        System.out.print("Contraseña: ");
                        String password = sc.nextLine().trim();

                        req = new AuthRequest(
                                AuthRequest.RequestType.LOGIN,
                                usuario,
                                password
                        );
                        break;

                    case "0": // LOGOUT
                        req = new AuthRequest(AuthRequest.RequestType.LOGOUT);
                        continuar = false;
                        break;

                    default:
                        System.out.println("Opción no válida");
                        continue;
                }

                // Enviar petición al servidor
                out.reset(); // Limpiar stream (recomendado en bucles)
                out.writeObject(req);

                // Recibir respuesta del servidor
                AuthResponse resp = (AuthResponse) in.readObject();

                // Mostrar resultado
                if (resp.isOk()) {
                    System.out.println("\n✓ " + resp.getMensaje());
                } else {
                    System.out.println("\n✗ " + resp.getMensaje());
                }
            }

            System.out.println("\nDesconectado del servidor.");

        } catch (IOException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Error: clase no encontrada");
        }
    }
}