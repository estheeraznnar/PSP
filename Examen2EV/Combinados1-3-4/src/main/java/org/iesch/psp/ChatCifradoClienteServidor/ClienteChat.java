package org.iesch.psp.ChatCifradoClienteServidor;// --- ClienteChat.java ---
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteChat {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Tu nombre:");
        String nombre = sc.nextLine();
        System.out.println("Host del servidor:");
        String host = sc.nextLine();
        System.out.println("Puerto:");
        int puerto = Integer.parseInt(sc.nextLine());

        // Generar par de claves RSA
        System.out.println("Generando claves RSA...");
        RSAUtil.RSAKeys misClaves = RSAUtil.createRSAKeys(2048);
        System.out.println("Claves generadas.");

        try (Socket socket = new Socket(host, puerto)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // Enviar clave pública al servidor
            MensajeCifrado msgClave = new MensajeCifrado(
                    MensajeCifrado.TipoMensaje.CLAVE_PUBLICA,
                    nombre,
                    misClaves.publicKey
            );
            out.writeObject(msgClave);
            out.flush();

            System.out.println("Conectado al chat. Escribe mensajes (escribe 'salir' para salir):\n");

            // Hilo para recibir mensajes
            Thread receptor = new Thread(() -> {
                try {
                    while (true) {
                        MensajeCifrado msgRecibido = (MensajeCifrado) in.readObject();
                        String contenido = msgRecibido.getContenido();

                        // Intentar descifrar con nuestra clave privada
                        try {
                            String descifrado = RSAUtil.decrypt(contenido, misClaves.privateKey);
                            System.out.println("[" + msgRecibido.getRemitente() + "]: " + descifrado);
                        } catch (Exception e) {
                            // Si no se puede descifrar, mostrar tal cual
                            System.out.println("[" + msgRecibido.getRemitente() + "]: " + contenido);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Desconectado del servidor.");
                }
            });
            receptor.setDaemon(true);
            receptor.start();

            // Enviar mensajes
            String linea;
            while ((linea = sc.nextLine()) != null) {
                if (linea.equalsIgnoreCase("salir")) {
                    MensajeCifrado msgSalir = new MensajeCifrado(
                            MensajeCifrado.TipoMensaje.SALIR, nombre, null);
                    out.writeObject(msgSalir);
                    break;
                }

                // Enviar mensaje (en producción se cifraría con clave del destinatario)
                MensajeCifrado msg = new MensajeCifrado(
                        MensajeCifrado.TipoMensaje.MENSAJE, nombre, linea);
                out.writeObject(msg);
                out.flush();
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}