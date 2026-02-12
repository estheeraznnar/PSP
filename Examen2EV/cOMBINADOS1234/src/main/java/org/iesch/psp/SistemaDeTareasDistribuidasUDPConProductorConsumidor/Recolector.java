// --- SistemaDeTareasDistribuidasUDPConProductorConsumidor/Recolector.java ---
package org.iesch.psp.SistemaDeTareasDistribuidasUDPConProductorConsumidor;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Recolector que recibe los resultados por UDP
 * Basado en el receptor UDP del PDF (páginas 15-19)
 */
public class Recolector implements Runnable {

    private int puerto;
    private int esperados;
    private List<Resultado> resultados = new ArrayList<>();
    private volatile boolean terminado = false;

    public Recolector(int puerto, int esperados) {
        this.puerto = puerto;
        this.esperados = esperados;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[4096];

        // Crear socket UDP para recibir
        try (DatagramSocket socket = new DatagramSocket(puerto)) {

            // Timeout para poder verificar condición de salida
            socket.setSoTimeout(1000);

            System.out.println("[Recolector] Escuchando en puerto " + puerto);
            System.out.println("[Recolector] Esperando " + esperados + " resultados\n");

            // Recibir resultados hasta tener todos
            while (resultados.size() < esperados) {
                try {
                    // Preparar datagrama para recibir
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    // Recibir (con timeout)
                    socket.receive(packet);

                    // Deserializar el objeto Resultado
                    ByteArrayInputStream bs = new ByteArrayInputStream(buffer);
                    ObjectInputStream in = new ObjectInputStream(bs);
                    Resultado res = (Resultado) in.readObject();
                    in.close();

                    // Guardar resultado
                    resultados.add(res);

                    System.out.println("[Recolector] Recibido: " + res);

                } catch (SocketTimeoutException e) {
                    // Timeout - continuar esperando
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        terminado = true;
        System.out.println("\n[Recolector] Todos los resultados recibidos");
    }

    public List<Resultado> getResultados() { return resultados; }
    public boolean isTerminado() { return terminado; }
}