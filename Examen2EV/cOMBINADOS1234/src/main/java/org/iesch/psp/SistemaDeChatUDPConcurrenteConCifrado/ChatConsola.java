// --- SistemaDeChatUDPConcurrenteConCifrado/ChatConsola.java ---
package org.iesch.psp.SistemaDeChatUDPConcurrenteConCifrado;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

/**
 * Cliente de chat por consola (sin interfaz gráfica)
 * Simplificación del ejemplo con GUI del PDF (páginas 23-29)
 */
public class ChatConsola implements ActionListener {

    private ChatUDP chat;

    /**
     * Método principal
     */
    public static void main(String[] args) {
        // Obtener configuración de línea de comandos
        String ip;
        int port;
        boolean cifrado;

        try {
            ip = args[0];
            port = Integer.parseInt(args[1]);
            cifrado = args.length > 2 && args[2].equalsIgnoreCase("cifrado");
        } catch (Exception e) {
            System.out.println("Uso: java ChatConsola <ip_multicast> <puerto> [cifrado]");
            System.out.println("Ejemplo: java ChatConsola 225.1.2.3 54321 cifrado");
            return;
        }

        // Pedir nick al usuario
        Scanner sc = new Scanner(System.in);
        System.out.print("Introduce tu nick: ");
        String nick = sc.nextLine().trim();

        if (nick.isEmpty()) {
            System.out.println("Nick vacío no permitido");
            return;
        }

        try {
            // Crear instancia del controlador
            ChatConsola app = new ChatConsola();

            // Crear el chat UDP
            app.chat = new ChatUDP(nick, ip, port, cifrado);

            // Añadir controlador para recibir eventos
            app.chat.addController(app);

            // Iniciar hilo de escucha
            Thread hiloEscucha = new Thread(app.chat);
            hiloEscucha.start();

            // Enviar mensaje de entrada al chat
            app.chat.enter();

            System.out.println("\n--- CHAT INICIADO ---");
            System.out.println("Escribe mensajes (escribe 'salir' para salir)\n");

            // Bucle de envío de mensajes
            String linea;
            while (!(linea = sc.nextLine()).equalsIgnoreCase("salir")) {
                if (!linea.trim().isEmpty()) {
                    app.chat.enviarMensaje(linea);
                }
            }

            // Parar el chat
            app.chat.stopService();

            System.out.println("\n--- CHAT FINALIZADO ---");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Maneja los eventos de recepción de mensajes
     * Como en el controlador del PDF (página 28)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(ChatUDP.RECEIVE)) {
            // Mostrar el mensaje recibido
            String texto = (String) e.getSource();
            System.out.println(texto);
        }
    }
}