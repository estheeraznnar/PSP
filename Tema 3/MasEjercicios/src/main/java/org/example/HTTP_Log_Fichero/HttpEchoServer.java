package org.example.HTTP_Log_Fichero;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Servidor HTTP muy simple que:
 *  - Escucha en el puerto 8080
 *  - Atiende peticiones de un único cliente cada vez (bucle while)
 *  - Si recibe GET /echo?msg=... responde con HTML mostrando el mensaje
 *    y registra la petición en requests.log
 *  - Si recibe GET /stats responde con HTML mostrando el número total
 *    de peticiones atendidas (leyendo de RequestLog)
 */
public class HttpEchoServer {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println("=== HttpEchoServer escuchando en puerto " + PORT + " ===");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                // Esperar a que un cliente se conecte
                Socket clientSocket = serverSocket.accept();
                String clientIp = clientSocket.getInetAddress().getHostAddress();
                System.out.println("Cliente conectado desde: " + clientIp);

                // Atender al cliente (un único cliente cada vez)
                handleClient(clientSocket, clientIp);
            }

        } catch (IOException e) {
            System.err.println("Error en servidor: " + e.getMessage());
        }
    }

    /**
     * Atiende una conexión HTTP sencilla.
     */
    private static void handleClient(Socket clientSocket, String clientIp) {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                BufferedWriter out = new BufferedWriter(
                        new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))
        ) {
            // Leer línea de petición (ej: GET /echo?msg=Hola HTTP/1.1)
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }
            System.out.println("Petición: " + requestLine);

            // Leer cabeceras hasta línea en blanco
            String header;
            while ((header = in.readLine()) != null && !header.isEmpty()) {
                // Podemos ignorar las cabeceras en este ejercicio
                // System.out.println("Header: " + header);
            }

            // Parsear método, ruta y versión HTTP
            String[] parts = requestLine.split(" ");
            if (parts.length < 3) {
                sendBadRequest(out);
                return;
            }

            String method = parts[0];      // GET
            String fullPath = parts[1];    // /echo?msg=...
            // String httpVersion = parts[2]; // HTTP/1.1 (no lo necesitamos ahora)

            if (!method.equals("GET")) {
                sendNotAllowed(out);
                return;
            }

            // Comprobar ruta
            if (fullPath.startsWith("/echo")) {
                // /echo?msg=...
                handleEcho(fullPath, clientIp, out);
            } else if (fullPath.startsWith("/stats")) {
                // /stats
                handleStats(out);
            } else {
                sendNotFound(out);
            }

        } catch (IOException e) {
            System.err.println("Error atendiendo cliente " + clientIp + ": " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Cliente desconectado: " + clientIp + "\n");
            } catch (IOException ignored) {}
        }
    }

    /**
     * Maneja la ruta /echo?msg=...
     */
    private static void handleEcho(String fullPath, String clientIp, BufferedWriter out) throws IOException {
        // Valor por defecto si no viene msg=
        String msg = "(sin mensaje)";

        // Buscar el parámetro msg en la query
        int idx = fullPath.indexOf("?");
        if (idx != -1 && idx < fullPath.length() - 1) {
            String query = fullPath.substring(idx + 1); // msg=Hola%20mundo
            String[] params = query.split("&");
            for (String param : params) {
                String[] kv = param.split("=", 2);
                if (kv.length == 2 && kv[0].equals("msg")) {
                    // Decodificar URL (espacios, acentos, etc.)
                    msg = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                    break;
                }
            }
        }

        // Registrar en el log
        RequestLog.logRequest(clientIp, msg);

        // Generar HTML de respuesta
        String body = """
                <html>
                  <head><title>Echo</title></head>
                  <body>
                    <h1>Echo HTTP Server</h1>
                    <p>Mensaje recibido:</p>
                    <h2>%s</h2>
                  </body>
                </html>
                """.formatted(escapeHtml(msg));

        sendHttpOk(out, body);
    }

    /**
     * Maneja la ruta /stats
     */
    private static void handleStats(BufferedWriter out) throws IOException {
        int total = RequestLog.getTotalRequests();

        String body = """
                <html>
                  <head><title>Estadísticas</title></head>
                  <body>
                    <h1>Estadísticas del servidor</h1>
                    <p>Total de peticiones registradas en requests.log:</p>
                    <h2>%d</h2>
                    <p>Ruta /echo?msg=...</p>
                  </body>
                </html>
                """.formatted(total);

        sendHttpOk(out, body);
    }

    /**
     * Envía una respuesta 200 OK con cuerpo HTML.
     */
    private static void sendHttpOk(BufferedWriter out, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        out.write("HTTP/1.1 200 OK\r\n");
        out.write("Content-Type: text/html; charset=UTF-8\r\n");
        out.write("Content-Length: " + bytes.length + "\r\n");
        out.write("Connection: close\r\n");
        out.write("\r\n");
        out.write(body);
        out.flush();
    }

    /**
     * Envía una respuesta 400 Bad Request.
     */
    private static void sendBadRequest(BufferedWriter out) throws IOException {
        String body = "<html><body><h1>400 Bad Request</h1></body></html>";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        out.write("HTTP/1.1 400 Bad Request\r\n");
        out.write("Content-Type: text/html; charset=UTF-8\r\n");
        out.write("Content-Length: " + bytes.length + "\r\n");
        out.write("Connection: close\r\n");
        out.write("\r\n");
        out.write(body);
        out.flush();
    }

    /**
     * Envía una respuesta 405 Method Not Allowed.
     */
    private static void sendNotAllowed(BufferedWriter out) throws IOException {
        String body = "<html><body><h1>405 Method Not Allowed</h1></body></html>";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        out.write("HTTP/1.1 405 Method Not Allowed\r\n");
        out.write("Content-Type: text/html; charset=UTF-8\r\n");
        out.write("Content-Length: " + bytes.length + "\r\n");
        out.write("Connection: close\r\n");
        out.write("\r\n");
        out.write(body);
        out.flush();
    }

    /**
     * Envía una respuesta 404 Not Found.
     */
    private static void sendNotFound(BufferedWriter out) throws IOException {
        String body = "<html><body><h1>404 Not Found</h1></body></html>";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        out.write("HTTP/1.1 404 Not Found\r\n");
        out.write("Content-Type: text/html; charset=UTF-8\r\n");
        out.write("Content-Length: " + bytes.length + "\r\n");
        out.write("Connection: close\r\n");
        out.write("\r\n");
        out.write(body);
        out.flush();
    }

    /**
     * Escapa caracteres especiales de HTML básicos.
     */
    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
