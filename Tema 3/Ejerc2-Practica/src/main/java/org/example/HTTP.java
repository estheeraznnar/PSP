package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HTTP {

    public static void main(String[] args) {
        try {
            // Se abre el puerto 8080 para escuchar peticiones entrantes
            ServerSocket servidor = new ServerSocket(8080);
            System.out.println("Servidor en http://localhost:8080");

            // Bucle infinito para mantener el servidor activo
            while (true) {
                // El programa se detiene aquí hasta que un cliente (navegador) conecta
                Socket cliente = servidor.accept();
                // Delegamos la gestión de la petición a un método aparte
                atenderCliente(cliente);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void atenderCliente(Socket cliente) {
        try {
            // Buffer para leer lo que el navegador nos envía
            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(cliente.getInputStream()));

            // Escritor para enviar la respuesta HTML al navegador
            PrintWriter salida = new PrintWriter(cliente.getOutputStream());

            // Leemos la primera línea de la cabecera HTTP (Ej: "GET /hora HTTP/1.1")
            String linea = entrada.readLine();
            // Extraemos la ruta (el índice 1 tras dividir por espacios)
            String ruta = linea.split(" ")[1];

            System.out.println("Peticion: " + ruta);

            String html = "";

            // --- SISTEMA DE ENRUTAMIENTO BÁSICO ---
            if (ruta.equals("/")) {
                html = "<html><body>" +
                        "<h1>Bienvenido</h1>" +
                        "<p><a href='/'>Inicio</a></p>" +
                        "<p><a href='/hora'>Hora</a></p>" +
                        "<p><a href='/info'>Info</a></p>" +
                        "</body></html>";

            } else if (ruta.equals("/hora")) {
                // Obtiene y formatea la fecha/hora del sistema
                String hora = LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

                html = "<html><body>" +
                        "<h1>Hora Actual</h1>" +
                        "<h2>" + hora + "</h2>" +
                        "<p><a href='/'>Volver</a></p>" +
                        "</body></html>";

            } else if (ruta.equals("/info")) {
                // Obtiene variables de entorno y propiedades de Java
                String equipo = System.getenv("COMPUTERNAME");
                if (equipo == null) equipo = "Desconocido";

                String so = System.getProperty("os.name");

                html = "<html><body>" +
                        "<h1>Info del Sistema</h1>" +
                        "<p>Equipo: " + equipo + "</p>" +
                        "<p>SO: " + so + "</p>" +
                        "<p><a href='/'>Volver</a></p>" +
                        "</body></html>";

            } else {
                // Respuesta por defecto si la ruta no existe
                html = "<html><body>" +
                        "<h1>404 - No encontrado</h1>" +
                        "<p><a href='/'>Volver</a></p>" +
                        "</body></html>";
            }

            // --- CONSTRUCCIÓN DE LA RESPUESTA HTTP ---
            salida.println("HTTP/1.1 200 OK"); // Línea de estado
            salida.println("Content-Type: text/html"); // Tipo de contenido
            salida.println(); // Línea en blanco obligatoria según el protocolo HTTP
            salida.println(html); // Cuerpo de la respuesta (el HTML)

            // Forzamos el envío y cerramos la conexión con el cliente
            salida.flush();
            cliente.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
