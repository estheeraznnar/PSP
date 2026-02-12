package org.iesch.psp.ServidorHTTPMultihiloconContadorSincronizado;// --- ManejadorPeticion.java (Hilo que atiende cada cliente) ---
import java.io.*;
import java.net.*;

public class ManejadorPeticion implements Runnable {

    private Socket clientSocket;
    private ContadorVisitas contador;

    public ManejadorPeticion(Socket clientSocket, ContadorVisitas contador) {
        this.clientSocket = clientSocket;
        this.contador = contador;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream();

            // Leer primera línea de la petición
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                clientSocket.close();
                return;
            }

            System.out.println("[" + Thread.currentThread().getName() + "] " + requestLine);

            // Parsear método y ruta
            String[] partes = requestLine.split(" ");
            String metodo = partes[0];
            String ruta = partes[1];

            // Consumir cabeceras
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                // Ignorar cabeceras
            }

            // Solo aceptamos GET
            if (!metodo.equals("GET")) {
                enviarRespuesta(out, 405, "text/html",
                        "<html><body><h1>405 - Método no permitido</h1></body></html>");
                clientSocket.close();
                return;
            }

            // Incrementar contador en cada petición
            contador.incrementar();

            // Enrutar según la ruta
            switch (ruta) {
                case "/":
                    enviarRespuesta(out, 200, "text/html",
                            "<html><body>"
                                    + "<h1>Servidor HTTP Multihilo</h1>"
                                    + "<p>Visitas totales: " + contador.getValor() + "</p>"
                                    + "<p><a href='/visitas'>Ver contador de visitas</a></p>"
                                    + "</body></html>");
                    break;

                case "/visitas":
                    enviarRespuesta(out, 200, "text/html",
                            "<html><body>"
                                    + "<h1>Contador de Visitas</h1>"
                                    + "<p>Total de peticiones recibidas: <strong>"
                                    + contador.getValor() + "</strong></p>"
                                    + "<p>Hilo actual: " + Thread.currentThread().getName() + "</p>"
                                    + "</body></html>");
                    break;

                default:
                    enviarRespuesta(out, 404, "text/html",
                            "<html><body><h1>404 - No encontrado</h1></body></html>");
                    break;
            }

            clientSocket.close();

        } catch (IOException e) {
            System.out.println("Error atendiendo petición: " + e.getMessage());
        }
    }

    private void enviarRespuesta(OutputStream out, int statusCode,
                                 String contentType, String body) throws IOException {
        String statusText;
        switch (statusCode) {
            case 200: statusText = "OK"; break;
            case 404: statusText = "Not Found"; break;
            case 405: statusText = "Method Not Allowed"; break;
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