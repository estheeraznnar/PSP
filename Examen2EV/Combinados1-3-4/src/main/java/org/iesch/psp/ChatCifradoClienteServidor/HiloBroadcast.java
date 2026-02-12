package org.iesch.psp.ChatCifradoClienteServidor;// --- HiloBroadcast.java (Consumidor que distribuye mensajes) ---
import java.io.ObjectOutputStream;
import java.util.Map;

public class HiloBroadcast implements Runnable {

    private ColaMensajes cola;
    private GestorClientes gestor;

    public HiloBroadcast(ColaMensajes cola, GestorClientes gestor) {
        this.cola = cola;
        this.gestor = gestor;
    }

    @Override
    public void run() {
        while (true) {
            MensajeCifrado mensaje = cola.tomar();
            if (mensaje == null) continue;

            String remitente = mensaje.getRemitente();
            System.out.println("[Broadcast] Mensaje de " + remitente);

            // Enviar a todos los clientes excepto al remitente
            for (Map.Entry<String, ObjectOutputStream> entry : gestor.getClientes().entrySet()) {
                String destino = entry.getKey();
                if (!destino.equals(remitente)) {
                    try {
                        ObjectOutputStream out = entry.getValue();
                        // Obtener clave pública del destinatario
                        String claveDestino = gestor.getClavePublica(destino);

                        // Descifrar mensaje (con clave del servidor) y recifrar con clave del destino
                        // Nota: En implementación real, el servidor no debería poder descifrar
                        // Aquí simplificamos enviando el mensaje tal cual
                        synchronized (out) {
                            out.reset();
                            out.writeObject(mensaje);
                            out.flush();
                        }
                    } catch (Exception e) {
                        System.out.println("[Broadcast] Error enviando a " + destino);
                    }
                }
            }
        }
    }
}