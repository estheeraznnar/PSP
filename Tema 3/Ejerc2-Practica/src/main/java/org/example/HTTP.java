package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HTTP {

    public static void main(String[] args) {

        try {
            ServerSocket servidor = new ServerSocket(8080);
            System.out.println("Servidor en http://localhost:8080");

            while (true) {
                Socket cliente = servidor.accept();
                atenderCliente(cliente);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void atenderCliente(Socket cliente) {
        try {
            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(cliente.getInputStream()));

            PrintWriter salida = new PrintWriter(cliente.getOutputStream());

            String linea = entrada.readLine();
            String ruta = linea.split(" ")[1];

            System.out.println("Peticion: " + ruta);

            String html = "";

            if (ruta.equals("/")) {
                html = "<html><body>" +
                        "<h1>Bienvenido</h1>" +
                        "<p><a href='/'>Inicio</a></p>" +
                        "<p><a href='/hora'>Hora</a></p>" +
                        "<p><a href='/info'>Info</a></p>" +
                        "</body></html>";

            } else if (ruta.equals("/hora")) {
                String hora = LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

                html = "<html><body>" +
                        "<h1>Hora Actual</h1>" +
                        "<h2>" + hora + "</h2>" +
                        "<p><a href='/'>Volver</a></p>" +
                        "</body></html>";

            } else if (ruta.equals("/info")) {
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
                html = "<html><body>" +
                        "<h1>404 - No encontrado</h1>" +
                        "<p><a href='/'>Volver</a></p>" +
                        "</body></html>";
            }

            salida.println("HTTP/1.1 200 OK");
            salida.println("Content-Type: text/html");
            salida.println();
            salida.println(html);
            salida.flush();

            cliente.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

