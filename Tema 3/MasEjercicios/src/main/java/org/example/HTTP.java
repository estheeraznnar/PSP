package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

/**
 * Servidor HTTP simple que proporciona información del sistema y hora actual.
 */
public class HTTP {

    public static void main(String[] args) throws IOException {
        // Crear servidor HTTP en puerto 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Registrar manejadores para cada ruta
        server.createContext("/", new RaizHandler());
        server.createContext("/hora", new horaHandler());
        server.createContext("/info", new info());

        // Usar executor por defecto (crear nuevo hilo por petición)
        server.setExecutor(null);
        System.out.println("Servidor iniciado en http://localhost:" + 8080);
        server.start();
    }

    /**
     * Manejador para la ruta raíz "/" - muestra página de bienvenida con enlaces.
     */
    static class RaizHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String respuesta = """
                    <h1>Bienvenido al Servidor Java Basico</h1>
                    <ul>
                        <li><a href="/hora">HORA</a></li>
                        <li><a href="/info">INICIO</a></li>
                    </ul>
                    """;
            enviarRespuesta(exchange, 200, respuesta);
        }
    }

    /**
     * Manejador para "/hora" - devuelve fecha y hora actual del servidor.
     */
    static class horaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String respuesta = LocalDateTime.now().toString();
            enviarRespuesta(exchange, 200, respuesta);
        }
    }

    /**
     * Manejador para "/info" - devuelve propiedades del sistema (JVM, SO, etc.).
     */
    static class info implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String respuesta = System.getProperties().toString();
            enviarRespuesta(exchange, 200, respuesta);
        }
    }

    /**
     * Envía respuesta HTTP con código de estado y contenido especificado.
     */
    private static void enviarRespuesta(HttpExchange exchange, int codigo, String respuesta) throws IOException {
        // Establecer código de estado y longitud del contenido
        exchange.sendResponseHeaders(codigo, respuesta.length());

        // Escribir respuesta en el cuerpo del mensaje
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(respuesta.getBytes());
        }
    }
}
