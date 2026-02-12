package org.iesch.psp.UDPConRespuesta;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class UDPVowelServer implements Runnable {
    private static volatile boolean started = false;
    private static volatile boolean stop = false;
    private static int port = 1234;

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Server started on port " + socket.getLocalPort() + " at " + LocalDateTime.now());
            started = true;
            socket.setSoTimeout(3000);

            while (!stop) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                try {
                    socket.receive(packet);
                } catch (SocketTimeoutException e) {
                    continue;
                }

                // Procesar el dato recibido y generar una respuesta
                byte[] response = processData(packet.getData(), packet.getLength());

                // Obtener origen del mensaje
                InetAddress origAddress = packet.getAddress();
                int origPort = packet.getPort();

                // Crear datagrama de respuesta
                DatagramPacket sendPacket = new DatagramPacket(response, response.length, origAddress, origPort);

                // Enviar mensaje
                socket.send(sendPacket);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Contar vocales en lugar de letras 'a'
    private byte[] processData(byte[] data, int length) {
        int count = 0;
        String vowels = "aeiouAEIOUáéíóúÁÉÍÓÚ";

        for (int i = 0; i < length; i++) {
            char c = (char) data[i];
            if (vowels.indexOf(c) != -1) {
                count++;
            }
        }

        String response = "Number of vowels: " + count;
        return response.getBytes();
    }

    public static void main(String[] args) {
        Thread t = new Thread(new UDPVowelServer());
        t.start();

        while (!started) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Press ENTER to stop the server...");
        Scanner sc = new Scanner(System.in);
        sc.hasNextLine();
        stop = true;
        sc.close();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Execution terminated");
    }
}