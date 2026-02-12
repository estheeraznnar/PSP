package org.example.EJ1Repaso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clase que guarda datos de uso del servidor SecureMessageServer
 * Almacena contador de mensajes recibidos y lista de mensajes
 *
 * IMPORTANTE: Esta clase debe ser thread-safe porque múltiples hilos
 * accederán a ella simultáneamente
 */
/*public class MessageData {

    // Contador de mensajes recibidos
    private int mensajesRecibidos;

    // Lista de mensajes almacenados
    private List<String> mensajes;

    /**
     * Constructor
     */
    /*public MessageData() {
        this.mensajesRecibidos = 0;
        // Usamos Collections.synchronizedList para thread-safety básico
        this.mensajes = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Añade un mensaje a la lista y incrementa el contador
     * SYNCHRONIZED para evitar condiciones de carrera
     */
    /*public synchronized void agregarMensaje(String mensaje) {
        mensajes.add(mensaje);
        mensajesRecibidos++;
    }

    /**
     * Obtiene el número total de mensajes recibidos
     * SYNCHRONIZED para garantizar visibilidad entre hilos
     */
    /*public synchronized int getMensajesRecibidos() {
        return mensajesRecibidos;
    }

    /**
     * Obtiene la lista de mensajes
     * Devolvemos una copia para evitar modificaciones externas
     */
    /*public synchronized List<String> getMensajes() {
        return new ArrayList<>(mensajes);
    }

    /**
     * Obtiene estadísticas formateadas
     */
    /*public synchronized String getEstadisticas() {
        return String.format("Total mensajes recibidos: %d\nMensajes en memoria: %d",
                mensajesRecibidos, mensajes.size());
    }
}*/

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TAREA 4: MessageData con sincronización correcta
 *
 * Cambios para garantizar thread-safety:
 * - Usa AtomicInteger para el contador (operaciones atómicas)
 * - Mantiene sincronización en métodos que acceden a la lista
 * - Garantiza visibilidad entre hilos
 */
public class MessageData {

    // AtomicInteger para contador thread-safe sin synchronized
    private AtomicInteger mensajesRecibidos;

    // Lista sincronizada para almacenar mensajes
    private List<String> mensajes;

    /**
     * Constructor
     */
    public MessageData() {
        this.mensajesRecibidos = new AtomicInteger(0);
        this.mensajes = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Añade un mensaje a la lista y incrementa el contador
     *
     * IMPORTANTE: Aunque usamos estructuras thread-safe,
     * sincronizamos el método completo para garantizar
     * atomicidad de la operación completa
     */
    public synchronized void agregarMensaje(String mensaje) {
        mensajes.add(mensaje);
        mensajesRecibidos.incrementAndGet(); // Incremento atómico
    }

    /**
     * Obtiene el número total de mensajes recibidos
     * AtomicInteger garantiza visibilidad, no necesita synchronized
     */
    public int getMensajesRecibidos() {
        return mensajesRecibidos.get();
    }

    /**
     * Obtiene la lista de mensajes
     * Devolvemos una copia para evitar modificaciones externas
     */
    public synchronized List<String> getMensajes() {
        return new ArrayList<>(mensajes);
    }

    /**
     * Obtiene estadísticas formateadas
     */
    public synchronized String getEstadisticas() {
        return String.format("Total mensajes recibidos: %d\nMensajes en memoria: %d",
                mensajesRecibidos.get(), mensajes.size());
    }

    /**
     * Reinicia las estadísticas (útil para testing)
     */
    public synchronized void reiniciar() {
        mensajesRecibidos.set(0);
        mensajes.clear();
    }
}

