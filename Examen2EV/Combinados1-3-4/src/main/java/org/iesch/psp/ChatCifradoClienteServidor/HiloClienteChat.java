package org.iesch.psp.ChatCifradoClienteServidor;// --- HiloClienteChat.java (Atiende cada cliente) ---
import java.io.*;
import java.net.*;

public class HiloClienteChat implements Runnable {

    private Socket socket;
    private GestorClientes gestor;
    private ColaMensajes colaBroadcast;
    private String nombreCliente;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public HiloClienteChat(Socket socket, GestorClientes gestor, ColaMensajes colaBroadcast) {
        this.socket = socket;
        this.gestor = gestor;
        this.colaBroadcast = colaBroadcast;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // 1. Recibir clave pública del cliente
            MensajeCifrado msgClave = (MensajeCifrado) in.readObject();
            nombreCliente = msgClave.getRemitente();
            String clavePublicaCliente = msgClave.getContenido();

            // 2. Registrar cliente
            gestor.registrar(nombreCliente, out, clavePublicaCliente);

            System.out.println("[" + nombreCliente + "] Conectado y clave recibida.");

            // 3. Escuchar mensajes
            MensajeCifrado mensaje;
            while ((mensaje = (MensajeCifrado) in.readObject()) != null) {
                if (mensaje.getTipo() == MensajeCifrado.TipoMensaje.SALIR) {
                    break;
                }

                // Poner mensaje en cola para broadcast
                colaBroadcast.poner(mensaje);
            }

        } catch (Exception e) {
            System.out.println("[" + nombreCliente + "] Desconectado: " + e.getMessage());
        } finally {
            if (nombreCliente != null) {
                gestor.eliminar(nombreCliente);
            }
            try {
                socket.close();
            } catch (IOException e) { }
        }
    }
}