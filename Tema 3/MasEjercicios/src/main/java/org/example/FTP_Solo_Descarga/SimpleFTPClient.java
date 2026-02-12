package org.example.FTP_Solo_Descarga;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Cliente FTP muy básico en modo texto.
 *
 * Se conecta al puerto 21 del servidor FTP y permite ejecutar:
 *  - LOGIN:  USER / PASS
 *  - PWD:    Mostrar directorio actual
 *  - LIST:   Listar contenido del directorio actual (modo texto)
 *  - RETR:   Simular descarga de un fichero y guardarlo en disco
 *
 * NOTA: Para simplificar, se usa SOLO canal de control.
 * El comando LIST/RETR se trata como si el servidor mandara texto
 * por el mismo socket (no es FTP real, pero sirve para el ejercicio).
 */
public class SimpleFTPClient {

    private final String host;
    private final int port;
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public SimpleFTPClient(String host) {
        this(host, 21);
    }

    public SimpleFTPClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Abre la conexión con el servidor FTP.
     */
    public void connect() throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
        out = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));

        // Leer banner inicial (ej: 220 FTP server ready)
        String banner = in.readLine();
        System.out.println("S: " + banner);
        if (banner == null || !banner.startsWith("220")) {
            throw new IOException("Error al conectar: " + banner);
        }
    }

    /**
     * Cierra la conexión con el servidor.
     */
    public void disconnect() {
        try {
            if (out != null) {
                sendCommand("QUIT");
            }
        } catch (IOException ignored) {}

        try { if (in != null) in.close(); } catch (IOException ignored) {}
        try { if (out != null) out.close(); } catch (IOException ignored) {}
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
    }

    /**
     * LOGIN: Envía USER y PASS, y comprueba la respuesta (230, 331, etc.).
     */
    public boolean login(String user, String pass) throws IOException {
        // USER
        sendCommand("USER " + user);
        String resp = readResponse();
        if (resp.startsWith("230")) {
            // 230 User logged in, no password needed
            return true;
        } else if (!resp.startsWith("331")) {
            // 331 User name okay, need password
            System.out.println("Login rechazado: " + resp);
            return false;
        }

        // PASS
        sendCommand("PASS " + pass);
        resp = readResponse();
        if (resp.startsWith("230")) {
            System.out.println("Login correcto");
            return true;
        } else {
            System.out.println("Login fallido: " + resp);
            return false;
        }
    }

    /**
     * Comando PWD: muestra el directorio actual del servidor.
     */
    public void pwd() throws IOException {
        sendCommand("PWD");
        String resp = readResponse();
        System.out.println("Directorio actual: " + resp);
    }

    /**
     * Comando LIST: muestra listado del directorio actual.
     *
     * NOTA: En FTP real se usaría un canal de datos aparte (PASV/PORT).
     * Aquí asumimos que el servidor manda el listado en texto seguido
     * en el mismo canal, finalizado con una línea especial, por ejemplo:
     * "226 End of list".
     */
    public void list() throws IOException {
        sendCommand("LIST");
        String line;
        System.out.println("Listado de directorio:");
        while ((line = in.readLine()) != null) {
            System.out.println("S: " + line);
            // Simulamos fin de listado cuando el servidor envía 226
            if (line.startsWith("226")) {
                break;
            }
        }
    }

    /**
     * Simula descarga de un fichero remoto y lo guarda en disco.
     *
     * En FTP real, RETR abre un canal de datos. Aquí se asume
     * que el servidor devuelve el contenido del fichero como texto
     * en el mismo canal de control, terminando con una línea "226 End of file".
     */
    public void downloadFile(String remoteName, String localName) throws IOException {
        sendCommand("RETR " + remoteName);

        // Comprobamos primera respuesta (por ejemplo 150 Opening data connection)
        String resp = readResponse();
        if (!resp.startsWith("150") && !resp.startsWith("125")) {
            System.out.println("No se puede descargar: " + resp);
            return;
        }

        // Abrimos fichero local para escribir
        try (BufferedWriter fileOut = new BufferedWriter(new FileWriter(localName))) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("S: " + line);
                // Si llega el código 226, interpretamos que ha terminado el fichero
                if (line.startsWith("226")) {
                    break;
                }
                // Escribimos la línea en el fichero
                fileOut.write(line);
                fileOut.newLine();
            }
        }

        System.out.println("Descarga simulada completada. Guardado en: " + localName);
    }

    /**
     * Envía un comando FTP al servidor.
     */
    private void sendCommand(String cmd) throws IOException {
        System.out.println("C: " + cmd);
        out.write(cmd + "\r\n");
        out.flush();
    }

    /**
     * Lee una línea de respuesta del servidor FTP.
     */
    private String readResponse() throws IOException {
        String resp = in.readLine();
        System.out.println("S: " + resp);
        return resp;
    }
}

