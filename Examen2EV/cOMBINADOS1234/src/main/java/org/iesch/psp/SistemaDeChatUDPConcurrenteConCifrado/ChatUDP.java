// --- SistemaDeChatUDPConcurrenteConCifrado/ChatUDP.java ---
package org.iesch.psp.SistemaDeChatUDPConcurrenteConCifrado;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

/**
 * Modelo del chat UDP con cifrado AES
 * Basado en la clase Chat del PDF (páginas 25-27)
 *
 * Combina:
 * - Tema 1: Hilo para escucha de mensajes
 * - Tema 2: Sockets UDP multicast
 * - Tema 4: Cifrado AES de mensajes
 */
public class ChatUDP implements Runnable {

    // Acción para el evento de recepción
    public static final String RECEIVE = "RECEIVE";

    // Atributos del chat
    private String nick;                    // Nick del usuario
    private MulticastSocket socket;         // Socket UDP multicast
    private int port;                       // Puerto UDP
    private InetAddress group;              // Dirección del grupo multicast
    private ActionListener controller;      // Controlador para eventos
    private boolean stop = false;           // Orden de parada
    private volatile boolean stopped = false; // Confirmación de parada
    private byte[] buf = new byte[4096];    // Buffer para mensajes
    private boolean usarCifrado;            // Indica si se usa cifrado

    /**
     * Constructor del chat
     * @param nick Nombre de usuario
     * @param ip Dirección IP del grupo multicast (224.0.0.0 - 239.255.255.255)
     * @param port Puerto UDP
     * @param usarCifrado true para cifrar mensajes
     */
    public ChatUDP(String nick, String ip, int port, boolean usarCifrado) throws Exception {
        this.nick = nick;
        this.port = port;
        this.usarCifrado = usarCifrado;

        // Obtener dirección del grupo multicast
        this.group = InetAddress.getByName(ip);

        // Crear socket multicast y unirse al grupo
        // Como en el ejemplo del PDF (página 20)
        socket = new MulticastSocket(port);
        socket.joinGroup(group);

        // Timeout de 1 segundo para poder responder a orden de parada
        socket.setSoTimeout(1000);

        System.out.println("Chat UDP iniciado");
        System.out.println("Grupo multicast: " + ip + ":" + port);
        System.out.println("Cifrado: " + (usarCifrado ? "ACTIVADO" : "DESACTIVADO"));
    }

    /**
     * Hilo de escucha de mensajes
     * Basado en el método run() del PDF (página 26)
     */
    @Override
    public void run() {
        // Escuchar mientras no haya orden de parada
        while (!stop) {
            try {
                // Preparar datagrama para recibir
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                // Esperar recepción (con timeout)
                socket.receive(packet);

                // Deserializar el objeto MensajeChat
                // Como en el ejemplo del PDF (página 21)
                ByteArrayInputStream bs = new ByteArrayInputStream(buf);
                ObjectInputStream in = new ObjectInputStream(bs);
                MensajeChat msg = (MensajeChat) in.readObject();
                in.close();

                // Preparar texto a mostrar según el tipo de mensaje
                String texto;
                if (msg.getTipo().equals(MensajeChat.TipoMensaje.MSG)) {
                    // Descifrar si es necesario
                    String contenido = msg.getContenido();
                    if (msg.isCifrado()) {
                        contenido = CifradoAES.descifrar(contenido);
                    }
                    texto = msg.getNick() + " >> " + contenido;
                } else {
                    // Mensaje de entrada o salida
                    texto = ">> " + msg.getTipo() + " " + msg.getNick();
                }

                // Notificar al controlador (evento)
                if (controller != null) {
                    controller.actionPerformed(
                            new ActionEvent(texto, ActionEvent.ACTION_PERFORMED, RECEIVE)
                    );
                }

            } catch (SocketTimeoutException e) {
                // Timeout - permite verificar condición de parada
            } catch (IOException e) {
                if (!stop) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Cerrar socket al salir
        socket.close();
        stopped = true;
    }

    /**
     * Envía mensaje de entrada al chat
     */
    public void enter() {
        MensajeChat mensaje = new MensajeChat(nick, MensajeChat.TipoMensaje.ENTER);
        enviar(mensaje);
    }

    /**
     * Envía mensaje de texto al chat
     * @param texto Texto del mensaje
     */
    public void enviarMensaje(String texto) {
        // Cifrar si está activado
        String contenido = texto;
        if (usarCifrado) {
            contenido = CifradoAES.cifrar(texto);
        }

        MensajeChat mensaje = new MensajeChat(nick, contenido, usarCifrado);
        enviar(mensaje);
    }

    /**
     * Envía un mensaje al grupo multicast
     * Basado en el método send() del PDF (página 27)
     */
    private void enviar(MensajeChat mensaje) {
        if (!stop) {
            try {
                // Serializar el objeto mensaje
                // Como en el ejemplo del PDF (página 21)
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bs);
                out.writeObject(mensaje);
                out.close();
                byte[] bytes = bs.toByteArray();

                // Crear datagrama con destino al grupo multicast
                DatagramPacket packet = new DatagramPacket(
                        bytes, bytes.length, group, port
                );

                // Enviar el datagrama
                socket.send(packet);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Añade el controlador para eventos de recepción
     */
    public void addController(ActionListener controller) {
        this.controller = controller;
    }

    /**
     * Detiene el servicio de chat
     */
    public void stopService() {
        // Enviar mensaje de salida
        MensajeChat mensaje = new MensajeChat(nick, MensajeChat.TipoMensaje.QUIT);
        enviar(mensaje);

        // Ordenar parada
        this.stop = true;

        // Esperar a que el hilo de escucha termine
        while (!stopped) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}