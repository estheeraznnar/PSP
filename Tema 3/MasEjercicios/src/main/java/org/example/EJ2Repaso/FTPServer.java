package org.example.EJ2Repaso;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Servidor FTP simplificado con:
 * - Autenticación segura (salted password hashing)
 * - Pool de hilos (hasta 5 clientes simultáneos)
 * - Comando USERS solo para admin
 *
 * Comandos soportados:
 *   LOGIN <user> <pass>
 *   UPLOAD <nombre>
 *   DOWNLOAD <nombre>
 *   LIST
 *   USERS
 *   EXIT
 */
public class FTPServer {

    private static final int PORT = 2121;
    private static final int MAX_CLIENTS = 5;
    private static final File ROOT_DIR = new File("ftp_root");

    // Conjunto de usuarios conectados actualmente (thread-safe)
    private static final Set<String> connectedUsers = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        // Creamos el directorio raíz si no existe
        if (!ROOT_DIR.exists()) ROOT_DIR.mkdirs();

        System.out.println("=== FTPServer Seguro ===");
        System.out.println("Puerto: " + PORT);
        System.out.println("Directorio raíz: " + ROOT_DIR.getAbsolutePath());

        // Pool de hilos con capacidad para 5 clientes simultáneos
        ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            // Bucle principal del servidor: acepta conexiones indefinidamente
            while (true) {
                Socket client = serverSocket.accept();
                // Por cada cliente nuevo, se crea un ClientHandler en un hilo del pool
                pool.execute(new ClientHandler(client));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Cerramos el pool de hilos al apagar el servidor
            pool.shutdown();
        }
    }

    /**
     * Clase interna que maneja un cliente concreto.
     * Cada instancia se ejecuta en un hilo distinto.
     */
    private static class ClientHandler implements Runnable {

        private final Socket socket;
        private String currentUser = null; // Usuario autenticado en esta conexión

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            String clientInfo = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
            System.out.println("Cliente conectado: " + clientInfo + " [" + Thread.currentThread().getName() + "]");

            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                // Mensaje de bienvenida típico de FTP
                out.println("220 FTPServer listo. Use LOGIN <user> <pass>");

                // 1) AUTENTICACIÓN OBLIGATORIA
                if (!handleLogin(in, out)) {
                    out.println("530 Login incorrecto. Cerrando conexión.");
                    return; // Termina el hilo para este cliente
                }

                out.println("230 Login correcto. Bienvenido " + currentUser);

                // 2) Bucle de comandos FTP
                String line;
                while ((line = in.readLine()) != null) {
                    String[] parts = line.split(" ");
                    String cmd = parts[0].toUpperCase();

                    switch (cmd) {
                        case "UPLOAD":
                            if (parts.length < 2) {
                                out.println("501 Uso: UPLOAD <nombre>");
                            } else {
                                handleUpload(parts[1], in, out);
                            }
                            break;
                        case "DOWNLOAD":
                            if (parts.length < 2) {
                                out.println("501 Uso: DOWNLOAD <nombre>");
                            } else {
                                handleDownload(parts[1], out);
                            }
                            break;
                        case "LIST":
                            handleList(out);
                            break;
                        case "USERS":
                            handleUsers(out);
                            break;
                        case "EXIT":
                            out.println("221 Adiós.");
                            return; // salimos del run() → se cierra el hilo
                        default:
                            out.println("502 Comando no soportado.");
                            break;
                    }
                }

            } catch (IOException e) {
                System.out.println("Error con cliente: " + e.getMessage());
            } finally {
                // Al terminar la conexión, si había usuario logueado, lo quitamos
                if (currentUser != null) {
                    connectedUsers.remove(currentUser);
                }
                try {
                    socket.close();
                } catch (IOException ignored) {}
                System.out.println("Cliente desconectado: " + clientInfo);
            }
        }

        /**
         * Gestiona el proceso de LOGIN.
         * El cliente tiene hasta 3 intentos para autenticarse.
         */
        private boolean handleLogin(BufferedReader in, PrintWriter out) throws IOException {
            for (int i = 0; i < 3; i++) { // 3 intentos máximo
                out.println("331 Introduzca credenciales. Formato: LOGIN <user> <pass>");
                String line = in.readLine();
                if (line == null) return false; // cliente colgó

                String[] parts = line.split(" ");
                if (parts.length != 3 || !parts[0].equalsIgnoreCase("LOGIN")) {
                    out.println("501 Formato incorrecto.");
                    continue;
                }

                String user = parts[1];
                String pass = parts[2];

                // Validamos usando UserCredentials (salted hash)
                if (UserCredentials.validate(user, pass)) {
                    this.currentUser = user;
                    connectedUsers.add(user);  // Añadimos a la lista de conectados
                    return true;
                } else {
                    out.println("530 Usuario o contraseña incorrectos.");
                }
            }
            // Si llega aquí, falló todos los intentos
            return false;
        }

        /**
         * Maneja el comando UPLOAD.
         * El cliente envía líneas de texto y termina con una línea "<EOF>".
         */
        private void handleUpload(String filename, BufferedReader in, PrintWriter out) throws IOException {
            out.println("150 Envíe el contenido. Termine con una línea que contenga solo <EOF>");

            File dest = new File(ROOT_DIR, filename);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(dest))) {
                String line;
                while ((line = in.readLine()) != null) {
                    if ("<EOF>".equals(line)) break; // Fin de archivo
                    writer.write(line);
                    writer.newLine();
                }
            }
            out.println("226 Archivo subido: " + filename);
        }

        /**
         * Maneja el comando DOWNLOAD.
         * Envía el contenido del archivo y termina con "<EOF>".
         */
        private void handleDownload(String filename, PrintWriter out) throws IOException {
            File src = new File(ROOT_DIR, filename);
            if (!src.exists()) {
                out.println("550 Archivo no encontrado.");
                return;
            }
            out.println("150 Enviando archivo. Terminación con <EOF>");
            try (BufferedReader reader = new BufferedReader(new FileReader(src))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    out.println(line);
                }
            }
            out.println("<EOF>");
            out.println("226 Transferencia completada.");
        }

        /**
         * Maneja el comando LIST.
         * Muestra los archivos disponibles en el directorio raíz.
         */
        private void handleList(PrintWriter out) {
            File[] files = ROOT_DIR.listFiles();
            out.println("150 Listado de archivos:");
            if (files != null) {
                for (File f : files) {
                    out.println(f.getName() + " (" + f.length() + " bytes)");
                }
            }
            out.println("226 Fin de LIST.");
        }

        /**
         * Maneja el comando USERS.
         * Solo el usuario "admin" puede ver la lista de usuarios conectados.
         */
        private void handleUsers(PrintWriter out) {
            if (!"admin".equals(currentUser)) {
                out.println("550 Permiso denegado. Solo admin.");
                return;
            }
            out.println("212 Usuarios conectados:");
            for (String u : connectedUsers) {
                out.println("- " + u);
            }
            out.println("226 Fin de USERS.");
        }
    }
}
