package org.example.HTTP_Log_Fichero;

import java.io.*;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Cliente HTTP que:
 *  - Pide por consola un mensaje
 *  - Conecta al servidor HttpEchoServer (localhost:8080)
 *  - Envía: GET /echo?msg=... HTTP/1.1
 *  - Muestra por consola el código de estado y el cuerpo de la respuesta
 */
public class HttpEchoClient {

    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== HttpEchoClient ===");
        System.out.print("Introduce el mensaje a enviar: ");
        String msg = sc.nextLine();
        sc.close();

        try (
                Socket socket = new Socket(HOST, PORT);
                BufferedWriter out = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
        ) {
            // Codificar el mensaje en formato URL (espacios, acentos, etc.)
            String encodedMsg = URLEncoder.encode(msg, StandardCharsets.UTF_8);

            // Línea de petición
            String requestLine = "GET /echo?msg=" + encodedMsg + " HTTP/1.1";
            System.out.println("C: " + requestLine);

            // Enviar petición HTTP
            out.write(requestLine + "\r\n");
            out.write("Host: " + HOST + "\r\n");
            out.write("Connection: close\r\n");
            out.write("\r\n"); // línea en blanco -> fin cabeceras
            out.flush();

            // Leer línea de estado: HTTP/1.1 200 OK
            String statusLine = in.readLine();
            System.out.println("S: " + statusLine);
            if (statusLine == null || !statusLine.startsWith("HTTP/1.1")) {
                System.out.println("Respuesta inválida del servidor");
                return;
            }

            // Mostrar código de estado
            String[] parts = statusLine.split(" ");
            if (parts.length >= 3) {
                System.out.println("Código de estado: " + parts[1] + " " + parts[2]);
            }

            // Leer cabeceras hasta línea en blanco
            String line;
            int contentLength = -1;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                System.out.println("H: " + line);
                String lower = line.toLowerCase();
                if (lower.startsWith("content-length:")) {
                    String val = lower.substring("content-length:".length()).trim();
                    try {
                        contentLength = Integer.parseInt(val);
                    } catch (NumberFormatException ignored) {}
                }
            }

            // Leer el cuerpo de la respuesta
            StringBuilder body = new StringBuilder();
            char[] buffer = new char[1024];
            int read;
            int totalChars = 0;
            while ((read = in.read(buffer)) != -1) {
                body.append(buffer, 0, read);
                totalChars += read;
            }

            System.out.println("\n=== Cuerpo de la respuesta ===");
            System.out.println(body);
            System.out.println("Longitud recibida (caracteres): " + totalChars);
            if (contentLength != -1) {
                System.out.println("Content-Length anunciado (bytes): " + contentLength);
            }

        } catch (IOException e) {
            System.err.println("Error en cliente: " + e.getMessage());
        }
    }
}

