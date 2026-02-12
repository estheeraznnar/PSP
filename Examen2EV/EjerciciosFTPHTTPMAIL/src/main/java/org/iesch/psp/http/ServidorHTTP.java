package org.iesch.psp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServidorHTTP {

    private static final int PUERTO = 8080;

    public static void main(String[] args) {
        System.out.println("=== SERVIDOR HTTP ===");
        System.out.println("Iniciando servidor en puerto " + PUERTO + "...");

        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor HTTP escuchando en http://localhost:" + PUERTO);
            System.out.println("Presiona Ctrl+C para detener el servidor\n");

            // Aceptar peticiones en bucle
            while (true) {
                Socket cliente = servidor.accept();

                // Crear un hilo para atender cada petición
                new Thread(new ManejadorPeticion(cliente)).start();
            }

        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

class ManejadorPeticion implements Runnable {

    private Socket cliente;

    public ManejadorPeticion(Socket cliente) {
        this.cliente = cliente;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(cliente.getInputStream())
                );
                PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
        ) {
            // Leer la petición HTTP
            String linea = in.readLine();

            if (linea == null || linea.isEmpty()) {
                return;
            }

            System.out.println("Petición recibida: " + linea);

            // Parsear la petición (GET /ruta HTTP/1.1)
            String[] partes = linea.split(" ");
            if (partes.length < 2) {
                enviarError(out, 400, "Bad Request");
                return;
            }

            String metodo = partes[0];
            String ruta = partes[1];

            // Solo aceptamos GET
            if (!metodo.equals("GET")) {
                enviarError(out, 405, "Method Not Allowed");
                return;
            }

            // Leer el resto de headers (aunque no los usemos)
            while (!(linea = in.readLine()).isEmpty()) {
                // Leer hasta la línea en blanco
            }

            // Procesar la ruta solicitada
            procesarRuta(out, ruta);

        } catch (IOException e) {
            System.err.println("Error al procesar petición: " + e.getMessage());
        } finally {
            try {
                cliente.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void procesarRuta(PrintWriter out, String ruta) {
        switch (ruta) {
            case "/":
                enviarPaginaBienvenida(out);
                break;
            case "/hora":
                enviarHoraActual(out);
                break;
            case "/info":
                enviarInfoSistema(out);
                break;
            default:
                enviarError(out, 404, "Not Found");
        }
    }

    private void enviarPaginaBienvenida(PrintWriter out) {
        String html = "<html lang=\"es\">\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>Bienvenida - Servidor HTTP</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; margin: 50px; background-color: #f0f0f0; }\n" +
                "        h1 { color: #333; }\n" +
                "        .container { background-color: white; padding: 20px; border-radius: 8px; }\n" +
                "        a { color: #0066cc; text-decoration: none; }\n" +
                "        a:hover { text-decoration: underline; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <h1>¡Bienvenido al Servidor HTTP!</h1>\n" +
                "        <p>Este es un servidor HTTP básico desarrollado en Java.</p>\n" +
                "        <h2>Rutas disponibles:</h2>\n" +
                "        <ul>\n" +
                "            <li><a href=\"/\">/ - Página de bienvenida</a></li>\n" +
                "            <li><a href=\"/hora\">/hora - Fecha y hora actual</a></li>\n" +
                "            <li><a href=\"/info\">/info - Información del sistema</a></li>\n" +
                "        </ul>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        enviarRespuesta(out, 200, "OK", html);
    }

    private void enviarHoraActual(PrintWriter out) {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        String html = "<html lang=\"es\">\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>Hora Actual</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; margin: 50px; background-color: #f0f0f0; }\n" +
                "        .container { background-color: white; padding: 20px; border-radius: 8px; }\n" +
                "        .hora { font-size: 24px; color: #0066cc; font-weight: bold; }\n" +
                "        a { color: #0066cc; text-decoration: none; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <h1>Fecha y Hora Actual del Sistema</h1>\n" +
                "        <p class=\"hora\">" + ahora.format(formato) + "</p>\n" +
                "        <p><a href=\"/\">← Volver al inicio</a></p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        enviarRespuesta(out, 200, "OK", html);
    }

    private void enviarInfoSistema(PrintWriter out) {
        String nombreEquipo = System.getenv("COMPUTERNAME");
        if (nombreEquipo == null) {
            nombreEquipo = System.getenv("HOSTNAME");
        }
        if (nombreEquipo == null) {
            nombreEquipo = "Desconocido";
        }

        String sistemaOperativo = System.getProperty("os.name");
        String version = System.getProperty("os.version");
        String arquitectura = System.getProperty("os.arch");

        String html = "<html lang=\"es\">\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>Información del Sistema</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; margin: 50px; background-color: #f0f0f0; }\n" +
                "        .container { background-color: white; padding: 20px; border-radius: 8px; }\n" +
                "        table { border-collapse: collapse; width: 100%; margin-top: 20px; }\n" +
                "        td { padding: 10px; border: 1px solid #ddd; }\n" +
                "        td:first-child { font-weight: bold; background-color: #f9f9f9; width: 40%; }\n" +
                "        a { color: #0066cc; text-decoration: none; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <h1>Información del Sistema</h1>\n" +
                "        <table>\n" +
                "            <tr><td>Nombre del Equipo</td><td>" + nombreEquipo + "</td></tr>\n" +
                "            <tr><td>Sistema Operativo</td><td>" + sistemaOperativo + "</td></tr>\n" +
                "            <tr><td>Versión</td><td>" + version + "</td></tr>\n" +
                "            <tr><td>Arquitectura</td><td>" + arquitectura + "</td></tr>\n" +
                "        </table>\n" +
                "        <p style=\"margin-top: 20px;\"><a href=\"/\">← Volver al inicio</a></p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        enviarRespuesta(out, 200, "OK", html);
    }

    private void enviarError(PrintWriter out, int codigo, String mensaje) {
        String html = "<html lang=\"es\">\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>Error " + codigo + "</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; margin: 50px; background-color: #f0f0f0; }\n" +
                "        .container { background-color: white; padding: 20px; border-radius: 8px; }\n" +
                "        h1 { color: #cc0000; }\n" +
                "        a { color: #0066cc; text-decoration: none; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <h1>Error " + codigo + " - " + mensaje + "</h1>\n" +
                "        <p>La página solicitada no está disponible.</p>\n" +
                "        <p><a href=\"/\">← Volver al inicio</a></p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        enviarRespuesta(out, codigo, mensaje, html);
    }

    private void enviarRespuesta(PrintWriter out, int codigo, String mensaje, String contenido) {
        // Enviar cabeceras HTTP
        out.println("HTTP/1.1 " + codigo + " " + mensaje);
        out.println("Content-Type: text/html; charset=UTF-8");
        out.println("Content-Length: " + contenido.getBytes().length);
        out.println("Connection: close");
        out.println(); // Línea en blanco

        // Enviar contenido
        out.println(contenido);
        out.flush();

        System.out.println("Respuesta enviada: " + codigo + " " + mensaje);
    }
}