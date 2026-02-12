package org.iesch.psp.SistemaProductorConsumidorParaEnvíodeCorreos;// --- ColaCorreos.java (Buffer compartido con sincronización) ---
import java.util.LinkedList;
import java.util.Queue;

public class ColaCorreos {

    private Queue<String> cola = new LinkedList<>();
    private int capacidad;
    private boolean produccionTerminada = false;

    public ColaCorreos(int capacidad) {
        this.capacidad = capacidad;
    }

    public synchronized void poner(String correo) {
        // Esperar si la cola está llena
        while (cola.size() >= capacidad) {
            try {
                wait();
            } catch (InterruptedException e) { }
        }
        cola.add(correo);
        System.out.println("[Productor] Añadido: " + correo + " (Cola: " + cola.size() + ")");
        notifyAll();
    }

    public synchronized String tomar() {
        // Esperar si la cola está vacía y no ha terminado la producción
        while (cola.isEmpty() && !produccionTerminada) {
            try {
                wait();
            } catch (InterruptedException e) { }
        }

        if (cola.isEmpty()) {
            return null; // Señal de fin
        }

        String correo = cola.poll();
        notifyAll();
        return correo;
    }

    public synchronized void terminarProduccion() {
        produccionTerminada = true;
        notifyAll();
    }

    public synchronized boolean hayMasCorreos() {
        return !cola.isEmpty() || !produccionTerminada;
    }
}