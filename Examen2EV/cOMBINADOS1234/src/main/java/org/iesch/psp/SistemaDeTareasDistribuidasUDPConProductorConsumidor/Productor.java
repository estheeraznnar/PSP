// --- SistemaDeTareasDistribuidasUDPConProductorConsumidor/Productor.java ---
package org.iesch.psp.SistemaDeTareasDistribuidasUDPConProductorConsumidor;

import java.util.Random;

/**
 * Productor que genera tareas aleatorias
 */
public class Productor implements Runnable {

    private ColaTareas cola;
    private int numTareas;

    public Productor(ColaTareas cola, int numTareas) {
        this.cola = cola;
        this.numTareas = numTareas;
    }

    @Override
    public void run() {
        Random random = new Random();
        Tarea.TipoTarea[] tipos = Tarea.TipoTarea.values();

        System.out.println("[Productor] Iniciando generación de " + numTareas + " tareas");

        for (int i = 1; i <= numTareas; i++) {
            // Generar tarea aleatoria
            Tarea.TipoTarea tipo = tipos[random.nextInt(tipos.length)];
            long valor = random.nextInt(20) + 1; // Valor entre 1 y 20

            Tarea tarea = new Tarea(i, tipo, valor);

            // Añadir a la cola (puede bloquear si está llena)
            cola.poner(tarea);

            // Simular tiempo de generación
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Indicar fin de producción
        cola.terminar();
        System.out.println("[Productor] Generación completada");
    }
}