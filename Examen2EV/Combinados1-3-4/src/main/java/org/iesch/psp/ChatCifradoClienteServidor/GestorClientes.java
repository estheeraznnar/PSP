package org.iesch.psp.ChatCifradoClienteServidor;// --- GestorClientes.java (Almacena clientes conectados) ---
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;

public class GestorClientes {

    // Mapeo: nombre -> stream de salida
    private ConcurrentHashMap<String, ObjectOutputStream> clientes = new ConcurrentHashMap<>();

    // Mapeo: nombre -> clave pública
    private ConcurrentHashMap<String, String> clavesPublicas = new ConcurrentHashMap<>();

    public synchronized void registrar(String nombre, ObjectOutputStream out, String clavePublica) {
        clientes.put(nombre, out);
        clavesPublicas.put(nombre, clavePublica);
        System.out.println("[Servidor] Cliente registrado: " + nombre);
    }

    public synchronized void eliminar(String nombre) {
        clientes.remove(nombre);
        clavesPublicas.remove(nombre);
        System.out.println("[Servidor] Cliente eliminado: " + nombre);
    }

    public String getClavePublica(String nombre) {
        return clavesPublicas.get(nombre);
    }

    public ConcurrentHashMap<String, ObjectOutputStream> getClientes() {
        return clientes;
    }
}