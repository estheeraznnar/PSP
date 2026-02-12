package org.iesch.psp.eJ02;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServidorHTTP {

    private static final int PUERTO = 8080;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor HTTP escuchando en el puerto " + PUERTO);

            while (true) {
                // Esperamos conexión de un cliente
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                // Atendemos la petición en un hilo para permitir concurrencia
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
            // Leemos la primera línea de la petición HTTP (ej: GET /hora HTTP/1.1)
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                clientSocket.close();
                return;
            }

            System.out.println("Petición recibida: " + requestLine);

            // Extraemos el método y la ruta
            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String path = parts[1];

            // Solo atendemos peticiones GET
            if (!method.equals("GET")) {
                enviarRespuesta(out, 405, "text/html",
                        "<html><body><h1>405 - Método no permitido</h1></body></html>");
                clientSocket.close();
                return;
            }

            // Enrutamos según la ruta solicitada
            switch (path) {
                case "/":
                    enviarRespuesta(out, 200, "text/html",
                            "<html><body>"
                                    + "<h1>Bienvenido al Servidor HTTP</h1>"
                                    + "<p>Rutas disponibles:</p>"
                                    + "<ul>"
                                    + "<li><a href=\"/hora\">/hora</a> - Fecha y hora actual</li>"
                                    + "<li><a href=\"/info\">/info</a> - Información del sistema</li>"
                                    + "</ul>"
                                    + "</body></html>");
                    break;

                case "/hora":
                    String fechaHora = LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                    enviarRespuesta(out, 200, "text/html",
                            "<html><body>"
                                    + "<h1>Fecha y Hora del Sistema</h1>"
                                    + "<p>" + fechaHora + "</p>"
                                    + "</body></html>");
                    break;

                case "/info":
                    String nombreEquipo = System.getenv("COMPUTERNAME");
                    if (nombreEquipo == null) {
                        nombreEquipo = System.getenv("HOSTNAME");
                    }
                    String sistemaOperativo = System.getProperty("os.name")
                            + " " + System.getProperty("os.version");

                    enviarRespuesta(out, 200, "text/html",
                            "<html><body>"
                                    + "<h1>Información del Sistema</h1>"
                                    + "<p>Nombre del equipo: " + nombreEquipo + "</p>"
                                    + "<p>Sistema operativo: " + sistemaOperativo + "</p>"
                                    + "</body></html>");
                    break;

                default:
                    enviarRespuesta(out, 404, "text/html",
                            "<html><body><h1>404 - Recurso no encontrado</h1></body></html>");
                    break;
            }

            clientSocket.close();

        } catch (IOException e) {
            System.out.println("Error atendiendo petición: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void enviarRespuesta(OutputStream out, int statusCode, String contentType, String body)
            throws IOException {
        String statusText;
        switch (statusCode) {
            case 200: statusText = "OK"; break;
            case 404: statusText = "Not Found"; break;
            case 405: statusText = "Method Not Allowed"; break;
            default: statusText = "Unknown"; break;
        }

        // Construimos la respuesta HTTP completa
        String response = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n"
                + "Content-Type: " + contentType + "; charset=UTF-8\r\n"
                + "Content-Length: " + body.getBytes("UTF-8").length + "\r\n"
                + "Connection: close\r\n"
                + "\r\n"
                + body;

        out.write(response.getBytes("UTF-8"));
        out.flush();

        System.out.println("Respuesta enviada: " + statusCode + " " + statusText);
    }
}