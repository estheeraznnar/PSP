package org.example.EJ2Repaso;

import java.io.*;
import java.net.Socket;

/**
 * Cliente sencillo para probar el servidor FTP.
 * Hace:
 *   - Conexión al servidor
 *   - LOGIN user/pass
 *   - UPLOAD de un archivo de texto
 *   - EXIT
 */
public class FTPClient {

    private final String host;
    private final int port;

    public FTPClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Conecta, hace login y sube un archivo de prueba.
     */
    public void uploadTestFile(String user, String pass, String filename, String content) {
        try (
                Socket socket = new Socket(host, port);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Mensaje de bienvenida del servidor
            System.out.println("Servidor dice: " + in.readLine()); // 220

            // Proceso de LOGIN
            System.out.println("Servidor dice: " + in.readLine()); // 331
            out.println("LOGIN " + user + " " + pass);
            String resp = in.readLine();
            System.out.println("Servidor dice: " + resp);
            if (!resp.startsWith("230")) {
                // Si no empieza por 230, el login ha fallado
                return;
            }

            // Comando UPLOAD
            out.println("UPLOAD " + filename);
            System.out.println("Servidor dice: " + in.readLine()); // 150
            // Enviamos el contenido línea a línea
            for (String linea : content.split("\n")) {
                out.println(linea);
            }
            // Indicamos fin del archivo
            out.println("<EOF>");
            System.out.println("Servidor dice: " + in.readLine()); // 226

            // Cerramos sesión
            out.println("EXIT");
            System.out.println("Servidor dice: " + in.readLine()); // 221

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
