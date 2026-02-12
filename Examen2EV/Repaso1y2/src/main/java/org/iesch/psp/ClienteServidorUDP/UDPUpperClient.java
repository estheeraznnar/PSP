package org.iesch.psp.ClienteServidorUDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPUpperClient {
    public static void main(String[] args) {
        String svrIP = "localhost";
        int svrPort = 1234;

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress dest = InetAddress.getByName(svrIP);

            byte[] msg = "hola servidor udp".getBytes();

            DatagramPacket packet = new DatagramPacket(msg, msg.length, dest, svrPort);

            System.out.println("Enviando datagrama al servidor...");
            socket.send(packet);

            System.out.println("Conexión cerrada");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Terminado");
    }
}