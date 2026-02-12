// --- SistemaDeTareasDistribuidasUDPConProductorConsumidor/ColaTareas.java ---
package org.iesch.psp.SistemaDeTareasDistribuidasUDPConProductorConsumidor;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Cola sincronizada para patrón Productor-Consumidor
 * Basado en el ejemplo del Tema 1 (productor-consumidor)
 */
public class ColaTareas {

    // Cola interna
    private Queue<Tarea> cola = new LinkedList<>();

    // Capacidad máxima
    private int capacidad;

    // Flag de producción terminada
    private boolean produccionTerminada = false;

    public ColaTareas(int capacidad) {
        this.capacidad = capacidad;
    }

    /**
     * Añade una tarea a la cola (bloquea si está llena)
     * Método sincronizado con wait/notify
     */
    public synchronized void poner(Tarea tarea) {
        // Esperar si la cola está llena
        while (cola.size() >= capacidad) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Añadir tarea
        cola.add(tarea);
        System.out.println("[Productor] Añadida: " + tarea + " (Cola: " + cola.size() + ")");

        // Notificar a los consumidores
        notifyAll();
    }

    /**
     * Toma una tarea de la cola (bloquea si está vacía)
     * @return Tarea o null si no hay más tareas
     */
    public synchronized Tarea tomar() {
        // Esperar si la cola está vacía y no ha terminado la producción
        while (cola.isEmpty() && !produccionTerminada) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Si no hay más tareas, devolver null
        if (cola.isEmpty()) {
            return null;
        }

        // Tomar tarea y notificar
        Tarea tarea = cola.poll();
        notifyAll();
        return tarea;
    }

    /**
     * Indica que no habrá más tareas
     */
    public synchronized void terminar() {
        produccionTerminada = true;
        notifyAll();
    }
}