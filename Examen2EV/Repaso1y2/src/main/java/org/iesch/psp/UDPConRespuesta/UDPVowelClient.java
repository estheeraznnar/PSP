package org.iesch.psp.UDPConRespuesta;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class UDPVowelClient {
    private static final String BYE = ".";

    public static void main(String[] args) {
        String svrIP = "localhost";
        int svrPort = 1234;

        System.out.println("El servidor contará el número de vocales");

        try (DatagramSocket socket = new DatagramSocket();
             Scanner sc = new Scanner(System.in)) {

            InetAddress dest = InetAddress.getByName(svrIP);
            String s;

            do {
                System.out.println("Introduce una frase (\".\" para salir)");
                s = sc.nextLine();

                byte[] msg = s.getBytes();
                DatagramPacket packet = new DatagramPacket(msg, msg.length, dest, svrPort);
                socket.send(packet);

                // Recibir respuesta
                byte[] buf = new byte[1024];
                DatagramPacket res = new DatagramPacket(buf, buf.length);
                socket.receive(res);

                // Mostrar la respuesta
                System.out.println(new String(res.getData(), 0, res.getLength()));

            } while (!s.equals(BYE));

            System.out.println("Cerrando conexión...");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Ejecución terminada");
    }
}