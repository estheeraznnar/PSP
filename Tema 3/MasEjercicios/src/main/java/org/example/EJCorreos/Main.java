package org.example.EJCorreos;

/**
 * Crea una aplicación llamada BulkSMTPClient que:
 * Pida por consola:
 *  servidor SMTP (host)
 *  puerto
    correo remitente
 *  contraseña del remitente
 *  correo destinatario
 *  número de correos a enviar
 *
 * Abra una conexión SMTP por socket.
 *
 * Haga el handshake SMTP (banner 220, EHLO, AUTH LOGIN).
 *
 * Envíe en un bucle N correos al mismo destinatario usando los comandos:
 *  MAIL FROM
 *  RCPT TO
 *  DATA
 *  cuerpo del mensaje
 *
 * Finalice con QUIT.
 * Muestre por consola las respuestas del servidor y cuántos correos se han enviado correctamente.
 */

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

/**
 * Cliente SMTP sencillo que envía N correos seguidos
 * a un mismo destinatario usando comandos de texto:
 * EHLO, AUTH LOGIN, MAIL FROM, RCPT TO, DATA, QUIT.
 *
 * No usa hilos ni librerías externas de correo.
 */
public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== ENVÍO MÚLTIPLE DE CORREOS SMTP (SIN HILOS) ===");

        System.out.print("Servidor SMTP (ej: smtp.gmail.com): ");
        String smtpHost = sc.nextLine().trim();

        System.out.print("Puerto SMTP (ej: 587): ");
        int smtpPort = Integer.parseInt(sc.nextLine().trim());

        System.out.print("Correo REMITENTE: ");
        String from = sc.nextLine().trim();

        System.out.print("Contraseña / password de aplicación: ");
        String password = sc.nextLine().trim();

        System.out.print("Correo DESTINATARIO: ");
        String to = sc.nextLine().trim();

        System.out.print("Número de correos a enviar: ");
        int numCorreos = Integer.parseInt(sc.nextLine().trim());

        System.out.print("Asunto base: ");
        String subjectBase = sc.nextLine().trim();

        System.out.print("Cuerpo base del mensaje: ");
        String bodyBase = sc.nextLine().trim();

        sc.close();

        int enviadosOK = 0;

        try (
                Socket socket = new Socket(smtpHost, smtpPort);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII), true)
        ) {
            // 1. Leer banner inicial (220)
            readLineExpect(in, 220);

            // 2. EHLO
            sendCommand(out, "EHLO localhost");
            readMultiline(in, 250);

            // NOTA: Aquí faltaría STARTTLS + actualización a SSLSocket
            // si el servidor lo exige; se omite por simplicidad del ejercicio.

            // 3. AUTH LOGIN
            sendCommand(out, "AUTH LOGIN");
            readLineExpect(in, 334); // pide usuario

            // Usuario en Base64 (dirección de correo)
            String userB64 = Base64.getEncoder()
                    .encodeToString(from.getBytes(StandardCharsets.US_ASCII));
            sendCommand(out, userB64);
            readLineExpect(in, 334); // pide password

            // Password en Base64
            String passB64 = Base64.getEncoder()
                    .encodeToString(password.getBytes(StandardCharsets.US_ASCII));
            sendCommand(out, passB64);
            readLineExpect(in, 235); // autenticación correcta

            // 4. Bucle de envío de correos
            for (int i = 1; i <= numCorreos; i++) {
                System.out.println("\n=== Enviando correo #" + i + " ===");

                // MAIL FROM
                sendCommand(out, "MAIL FROM:<" + from + ">");
                readLineExpect(in, 250);

                // RCPT TO
                sendCommand(out, "RCPT TO:<" + to + ">");
                readLineExpect(in, 250);

                // DATA
                sendCommand(out, "DATA");
                readLineExpect(in, 354); // listo para recibir datos

                // Cabeceras
                out.println("From: <" + from + ">");
                out.println("To: <" + to + ">");
                out.println("Subject: " + subjectBase + " #" + i);
                out.println("Content-Type: text/plain; charset=UTF-8");
                out.println();

                // Cuerpo del mensaje
                out.println(bodyBase);
                out.println();
                out.println("Este es el mensaje número " + i + ".");
                out.println();

                // Fin de datos: línea con solo un punto
                out.println(".");
                out.flush();

                // Respuesta del servidor al DATA
                if (readLineExpect(in, 250)) {
                    enviadosOK++;
                }
            }

            // 5. QUIT
            sendCommand(out, "QUIT");
            readLineExpect(in, 221);

        } catch (Exception e) {
            System.err.println("Error en el envío: " + e.getMessage());
        }

        // Resumen
        System.out.println("\n=== RESUMEN ===");
        System.out.println("Correos solicitados: " + numCorreos);
        System.out.println("Correos enviados OK: " + enviadosOK);
        System.out.println("Correos fallidos:    " + (numCorreos - enviadosOK));
    }

    /**
     * Envía un comando al servidor y lo muestra por consola.
     */
    private static void sendCommand(PrintWriter out, String cmd) {
        System.out.println("C: " + cmd);
        out.println(cmd);
    }

    /**
     * Lee una línea del servidor y comprueba que el código de estado
     * coincide con el esperado. Devuelve true si coincide.
     */
    private static boolean readLineExpect(BufferedReader in, int expectedCode) throws IOException {
        String line = in.readLine();
        System.out.println("S: " + line);
        if (line == null || line.length() < 3) {
            throw new IOException("Respuesta SMTP inválida");
        }
        int code = Integer.parseInt(line.substring(0, 3));
        if (code != expectedCode) {
            throw new IOException("Código esperado " + expectedCode + " pero recibido " + code);
        }
        return true;
    }

    /**
     * Lee una respuesta multilinea (por ejemplo, la de EHLO),
     * todas deben tener el mismo código (ej: 250-... 250-... 250 ...).
     */
    private static void readMultiline(BufferedReader in, int expectedCode) throws IOException {
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
            // Si el cuarto carácter es '-', sigue habiendo más líneas (250-)
        } while (line.charAt(3) == '-');
    }
}

