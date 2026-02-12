// --- SistemaDeTareasDistribuidasUDPConProductorConsumidor/Consumidor.java ---
package org.iesch.psp.SistemaDeTareasDistribuidasUDPConProductorConsumidor;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Consumidor que procesa tareas y envía resultados por UDP
 * Combina patrón Productor-Consumidor con comunicación UDP
 */
public class Consumidor implements Runnable {

    private String nombre;
    private ColaTareas cola;
    private InetAddress destino;
    private int puertoDestino;
    private int procesadas = 0;

    public Consumidor(String nombre, ColaTareas cola,
                      InetAddress destino, int puertoDestino) {
        this.nombre = nombre;
        this.cola = cola;
        this.destino = destino;
        this.puertoDestino = puertoDestino;
    }

    @Override
    public void run() {
        System.out.println("[" + nombre + "] Iniciado");

        // Socket UDP para enviar resultados
        try (DatagramSocket socket = new DatagramSocket()) {

            Tarea tarea;

            // Tomar tareas mientras haya
            while ((tarea = cola.tomar()) != null) {

                // Medir tiempo de procesamiento
                long inicio = System.currentTimeMillis();

                // Procesar la tarea
                String resultado = procesarTarea(tarea);

                long fin = System.currentTimeMillis();

                // Crear objeto Resultado
                Resultado res = new Resultado(tarea, resultado, nombre, fin - inicio);

                System.out.println("[" + nombre + "] Procesada: " + res);

                // Serializar y enviar por UDP
                // Como en el ejemplo del PDF (página 21)
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bs);
                out.writeObject(res);
                out.close();
                byte[] bytes = bs.toByteArray();

                // Crear y enviar datagrama
                DatagramPacket packet = new DatagramPacket(
                        bytes, bytes.length, destino, puertoDestino
                );
                socket.send(packet);

                procesadas++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("[" + nombre + "] Finalizado. Tareas procesadas: " + procesadas);
    }

    /**
     * Procesa una tarea según su tipo
     */
    private String procesarTarea(Tarea tarea) {
        long valor = tarea.getValor();

        switch (tarea.getTipo()) {
            case FACTORIAL:
                return String.valueOf(factorial(valor));

            case FIBONACCI:
                return String.valueOf(fibonacci((int) valor));

            case PRIMO:
                return esPrimo(valor) ? "ES PRIMO" : "NO ES PRIMO";

            case CUADRADO:
                return String.valueOf(valor * valor);

            default:
                return "ERROR";
        }
    }

    // Funciones de cálculo
    private long factorial(long n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }

    private long fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    private boolean esPrimo(long n) {
        if (n < 2) return false;
        for (long i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    public int getProcesadas() { return procesadas; }
}