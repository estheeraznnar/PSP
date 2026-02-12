package org.example.EJCorreosHilos;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Cliente SMTP que envía UN correo usando sockets y comandos de texto.
 *
 * Implementa el flujo:
 *  1) Conectar al servidor SMTP
 *  2) Leer banner 220
 *  3) EHLO
 *  4) AUTH LOGIN (usuario y contraseña en Base64)
 *  5) MAIL FROM
 *  6) RCPT TO
 *  7) DATA (cabeceras + cuerpo + ".")
 *  8) QUIT
 *
 * Se utiliza para que cada hilo envíe un correo con MailTask.
 */
public class BulkMailClient {

    private final String smtpHost;
    private final int smtpPort;
    private final String from;
    private final String password; // contraseña o password de aplicación
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public BulkMailClient(String smtpHost, int smtpPort, String from, String password) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.from = from;
        this.password = password;
    }

    /**
     * Envía UN único correo al destinatario indicado.
     *
     * @param to      destinatario
     * @param subject asunto del correo
     * @param body    cuerpo del mensaje
     * @return true si se envió correctamente, false si hubo error
     */
    public boolean sendMail(String to, String subject, String body) {
        try {
            // 1. Conectar al servidor SMTP
            socket = new Socket(smtpHost, smtpPort);
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream(), StandardCharsets.US_ASCII));
            out = new PrintWriter(new OutputStreamWriter(
                    socket.getOutputStream(), StandardCharsets.US_ASCII), true);

            // 2. Leer banner inicial (220 Servicio listo)
            readLineExpect(220);

            // 3. EHLO (identificación del cliente y capacidades del servidor)
            sendCommand("EHLO localhost");
            readMultiline(250); // Respuesta 250- ... 250- ... 250 ...

            // NOTA: En un servidor real (Gmail 587) aquí vendría STARTTLS y cambio a TLS.
            // Para simplificar el ejercicio, omitimos STARTTLS.

            // 4. Autenticación con AUTH LOGIN (usuario/clave en Base64)
            sendCommand("AUTH LOGIN");
            readLineExpect(334); // 334 VXNlcm5hbWU6

            // Enviar usuario (dirección de correo) en Base64
            String userB64 = Base64.getEncoder()
                    .encodeToString(from.getBytes(StandardCharsets.US_ASCII));
            sendCommand(userB64);
            readLineExpect(334); // 334 UGFzc3dvcmQ6

            // Enviar contraseña en Base64
            String passB64 = Base64.getEncoder()
                    .encodeToString(password.getBytes(StandardCharsets.US_ASCII));
            sendCommand(passB64);
            readLineExpect(235); // 235 Authentication successful

            // 5. MAIL FROM (remitente)
            sendCommand("MAIL FROM:<" + from + ">");
            readLineExpect(250); // 250 OK

            // 6. RCPT TO (destinatario)
            sendCommand("RCPT TO:<" + to + ">");
            readLineExpect(250); // 250 OK

            // 7. DATA (inicio de los datos del mensaje)
            sendCommand("DATA");
            readLineExpect(354); // 354 End data with <CR><LF>.<CR><LF>

            // 8. Cabeceras del mensaje
            out.println("From: <" + from + ">");
            out.println("To: <" + to + ">");
            out.println("Subject: " + subject);
            out.println("Content-Type: text/plain; charset=UTF-8");
            out.println(); // Línea en blanco para separar cabeceras y cuerpo

            // 9. Cuerpo del mensaje
            out.println(body);

            // 10. Final de los datos: línea con solo un punto
            out.println(".");
            out.flush();

            // Respuesta del servidor al mensaje
            readLineExpect(250); // 250 Message accepted

            // 11. QUIT (cerrar la sesión SMTP)
            sendCommand("QUIT");
            readLineExpect(221); // 221 Bye

            // Cerrar recursos
            close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            close();
            return false;
        }
    }

    /**
     * Envía un comando al servidor SMTP y lo muestra en consola (debug).
     */
    private void sendCommand(String cmd) {
        System.out.println("C: " + cmd);
        out.println(cmd);
    }

    /**
     * Lee una línea del servidor y comprueba que el código de estado
     * coincide con el esperado (por ejemplo, 220, 250, 354, etc.).
     */
    private void readLineExpect(BufferedReader in, int expectedCode) throws IOException {
        String line = in.readLine();
        System.out.println("S: " + line);
        if (line == null || line.length() < 3) {
            throw new IOException("Respuesta SMTP inválida");
        }
        int code = Integer.parseInt(line.substring(0, 3));
        if (code != expectedCode) {
            throw new IOException("Código esperado " + expectedCode + " pero recibido " + code);
        }
    }

    /**
     * Versión interna que usa el lector ya guardado en la clase.
     */
    private void readLineExpect(int expectedCode) throws IOException {
        readLineExpect(this.in, expectedCode);
    }

    /**
     * Lee una respuesta multilínea, típica de EHLO:
     * 250-xxxx
     * 250-xxxx
     * 250 xxxxx
     *
     * Todas las líneas deben tener el mismo código de estado (ej. 250).
     */
    private void readMultiline(int expectedCode) throws IOException {
        String line;
        do {
            line = in.readLine();
            System.out.println("S: " + line);
            if (line == null || line.length() < 3) {
                throw new IOException("Respuesta SMTP inválida");
            }
            int code = Integer.parseInt(line.substring(0, 3));
            if (code != expectedCode) {
                throw new IOException("Código esperado " + expectedCode + " pero recibido " + code);
            }
            // Si el cuarto carácter es '-', significa que hay más líneas (250-)
        } while (line.charAt(3) == '-');
    }

    /**
     * Cierra streams y socket de forma segura.
     */
    private void close() {
        try { if (in != null) in.close(); } catch (IOException ignored) {}
        if (out != null) out.close();
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
    }
}

