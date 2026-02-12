// --- SistemaDeTareasDistribuidasUDPConProductorConsumidor/Resultado.java ---
package org.iesch.psp.SistemaDeTareasDistribuidasUDPConProductorConsumidor;

import java.io.Serializable;

/**
 * Clase serializable que representa el resultado de una tarea
 */
public class Resultado implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Tarea tarea;          // Tarea original
    private final String resultado;     // Resultado del cálculo
    private final String trabajador;    // Identificador del trabajador
    private final long tiempoMs;        // Tiempo de procesamiento

    public Resultado(Tarea tarea, String resultado, String trabajador, long tiempoMs) {
        this.tarea = tarea;
        this.resultado = resultado;
        this.trabajador = trabajador;
        this.tiempoMs = tiempoMs;
    }

    public Tarea getTarea() { return tarea; }
    public String getResultado() { return resultado; }
    public String getTrabajador() { return trabajador; }
    public long getTiempoMs() { return tiempoMs; }

    @Override
    public String toString() {
        return "Resultado[" + tarea.getId() + "] = " + resultado
                + " (por " + trabajador + " en " + tiempoMs + "ms)";
    }
}