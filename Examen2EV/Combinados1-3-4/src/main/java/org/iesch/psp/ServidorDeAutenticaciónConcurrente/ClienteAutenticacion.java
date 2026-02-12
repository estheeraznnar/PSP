package org.iesch.psp.ServidorDeAutenticaciónConcurrente;// --- ClienteAutenticacion.java (Para probar) ---
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteAutenticacion {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Host del servidor:");
        String host = sc.nextLine();
        System.out.println("Puerto:");
        int puerto = Integer.parseInt(sc.nextLine());

        try (Socket socket = new Socket(host, puerto)) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Mostrar mensaje de bienvenida del servidor
            System.out.println(in.readLine());
            System.out.println(in.readLine());

            String comando;
            while (true) {
                System.out.print("> ");
                comando = sc.nextLine();

                out.println(comando);
                String respuesta = in.readLine();
                System.out.println(respuesta);

                if (comando.toUpperCase().equals("LOGOUT")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}