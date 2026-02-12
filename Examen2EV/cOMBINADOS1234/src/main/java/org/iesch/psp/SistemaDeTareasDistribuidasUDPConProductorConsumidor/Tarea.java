// --- SistemaDeTareasDistribuidasUDPConProductorConsumidor/Tarea.java ---
package org.iesch.psp.SistemaDeTareasDistribuidasUDPConProductorConsumidor;

import java.io.Serializable;

/**
 * Clase serializable que representa una tarea a procesar
 */
public class Tarea implements Serializable {

    private static final long serialVersionUID = 1L;

    // Tipos de tarea
    public enum TipoTarea {
        FACTORIAL,      // Calcular factorial
        FIBONACCI,      // Calcular fibonacci
        PRIMO,          // Verificar si es primo
        CUADRADO        // Calcular cuadrado
    }

    private final int id;           // Identificador único
    private final TipoTarea tipo;   // Tipo de cálculo
    private final long valor;       // Valor de entrada

    public Tarea(int id, TipoTarea tipo, long valor) {
        this.id = id;
        this.tipo = tipo;
        this.valor = valor;
    }

    public int getId() { return id; }
    public TipoTarea getTipo() { return tipo; }
    public long getValor() { return valor; }

    @Override
    public String toString() {
        return "Tarea[" + id + "] " + tipo + "(" + valor + ")";
    }
}