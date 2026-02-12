package org.iesch.psp.HTTPRSAFirmaDigital;// --- ServidorHTTPFirma.java ---
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class ServidorHTTPFirma {

    private static final int PUERTO = 8080;
    // Clave pública del cliente (se registra al inicio)
    private static String clientPublicKey = "";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor HTTP con verificación de firma en puerto " + PUERTO);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());
                new Thread(() -> atenderPeticion(clientSocket)).start();
            }

        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void atenderPeticion(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream out = clientSocket.getOutputStream()
        ) {
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                clientSocket.close();
                return;
            }
            System.out.println("Petición recibida: " + requestLine);

            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String path = parts[1];

            // Leemos cabeceras
            Map<String, String> headers = new HashMap<>();
            String headerLine;
            int contentLength = 0;
            while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
                String[] headerParts = headerLine.split(": ", 2);
                if (headerParts.length == 2) {
                    headers.put(headerParts[0], headerParts[1]);
                }
                if (headerLine.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(headerLine.split(": ")[1].trim());
                }
            }

            // Leemos body
            String body = "";
            if (contentLength > 0) {
                char[] bodyChars = new char[contentLength];
                in.read(bodyChars, 0, contentLength);
                body = new String(bodyChars);
            }

            switch (method + " " + path) {
                case "POST /register-key":
                    // El cliente envía su clave pública
                    clientPublicKey = body.trim();
                    System.out.println("Clave pública del cliente registrada.");
                    enviarRespuesta(out, 200, "text/html",
                            "<html><body><h1>Clave pública registrada</h1></body></html>");
                    break;

                case "POST /datos":
                    // El cliente envía datos firmados
                    String firma = headers.get("X-Signature");

                    if (firma == null || firma.isEmpty()) {
                        enviarRespuesta(out, 401, "text/html",
                                "<html><body><h1>Error: Falta la firma digital</h1></body></html>");
                        break;
                    }

                    if (clientPublicKey.isEmpty()) {
                        enviarRespuesta(out, 401, "text/html",
                                "<html><body><h1>Error: No hay clave pública registrada</h1></body></html>");
                        break;
                    }

                    // Verificamos la firma
                    if (RSAFirmaDigital.verify(body, firma, clientPublicKey)) {
                        System.out.println("Firma válida. Datos recibidos: " + body);
                        enviarRespuesta(out, 200, "text/html",
                                "<html><body>"
                                        + "<h1>Datos recibidos correctamente</h1>"
                                        + "<p>Firma verificada. Datos: " + body + "</p>"
                                        + "</body></html>");
                    } else {
                        System.out.println("Firma NO válida. Datos rechazados.");
                        enviarRespuesta(out, 401, "text/html",
                                "<html><body><h1>Error: Firma digital no válida</h1></body></html>");
                    }
                    break;

                default:
                    enviarRespuesta(out, 404, "text/html",
                            "<html><body><h1>404 - No encontrado</h1></body></html>");
                    break;
            }

            clientSocket.close();

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void enviarRespuesta(OutputStream out, int statusCode, String contentType, String body)
            throws IOException {
        String statusText;
        switch (statusCode) {
            case 200: statusText = "OK"; break;
            case 401: statusText = "Unauthorized"; break;
            case 404: statusText = "Not Found"; break;
            default: statusText = "Unknown"; break;
        }

        String response = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n"
                + "Content-Type: " + contentType + "; charset=UTF-8\r\n"
                + "Content-Length: " + body.getBytes("UTF-8").length + "\r\n"
                + "Connection: close\r\n"
                + "\r\n"
                + body;

        out.write(response.getBytes("UTF-8"));
        out.flush();
    }
}