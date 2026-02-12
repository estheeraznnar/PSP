package org.iesch.psp.ClienteServidorUDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPUpperServer {
    public static void main(String[] args) {
        int port = 1234;

        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Servidor esperando en puerto " + socket.getLocalPort() + "....");

            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // Esperamos la recepción de un mensaje del cliente
            socket.receive(packet);

            // Obtenemos el dato recibido
            String msg = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Mensaje recibido: " + msg);

            // Convertimos a mayúsculas
            String upperMsg = msg.toUpperCase();
            System.out.println("Mensaje en mayúsculas: " + upperMsg);

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}