package org.example.Bueno;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Scanner;

//correo: eazdi13@gmail.com
//contraseña: cahn hntf bpve fyyd

/**
 * Cliente SMTP que implementa el protocolo usando comandos de texto
 * Basado en la teoría de Protocolos de Correo Electrónico
 *
 * El protocolo SMTP (Simple Mail Transfer Protocol) funciona enviando comandos
 * de texto al servidor. Cada comando recibe una respuesta con un código numérico
 * y un mensaje descriptivo.
 *
 * Comandos principales implementados:
 * - EHLO: Identificación del cliente al servidor
 * - AUTH LOGIN: Autenticación con usuario y contraseña
 * - MAIL FROM: Especifica el remitente del correo
 * - RCPT TO: Especifica el destinatario del correo
 * - DATA: Inicia el envío del contenido del mensaje
 * - QUIT: Cierra la conexión con el servidor
 */
public class EnvioCorreoSMTP {

    public static void main(String[] args) {
        // Scanner para leer entrada del usuario
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== APLICACIÓN DE ENVÍO DE CORREO ELECTRÓNICO ===\n");

        try {
            // ============================================================
            // SOLICITAR DATOS AL USUARIO (requisitos del ejercicio)
            // ============================================================

            // Dirección de correo del emisor (quien envía)
            System.out.print("Dirección de correo EMISOR: ");
            String correoEmisor = scanner.nextLine();

            // Contraseña del correo emisor para autenticación SMTP
            System.out.print("Contraseña del correo emisor: ");
            String password = scanner.nextLine();

            // Dirección de correo del destinatario (quien recibe)
            System.out.print("\nDirección de correo DESTINATARIO: ");
            String correoDestinatario = scanner.nextLine();

            // Asunto del correo electrónico
            System.out.print("ASUNTO del correo: ");
            String asunto = scanner.nextLine();

            // Cuerpo del mensaje (contenido principal)
            System.out.println("CUERPO del mensaje:");
            String cuerpo = scanner.nextLine();

            // Preguntar si desea adjuntar un archivo (requisito opcional)
            System.out.print("\n¿Deseas adjuntar un archivo? (S/N): ");
            String respuesta = scanner.nextLine().toUpperCase();
            String rutaArchivo = null;

            if (respuesta.equals("S")) {
                System.out.print("Ruta completa del archivo a adjuntar: ");
                rutaArchivo = scanner.nextLine();
            }

            // ============================================================
            // ENVIAR EL CORREO USANDO EL PROTOCOLO SMTP
            // ============================================================
            enviarCorreoSMTP(correoEmisor, password, correoDestinatario, asunto, cuerpo, rutaArchivo);

            // Mensaje de confirmación (requisito del ejercicio)
            System.out.println("\n✓ CORREO ENVIADO EXITOSAMENTE");

        } catch (Exception e) {
            // Gestión de errores (requisito del ejercicio)
            System.out.println("\n✗ ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    /**
     * Método principal que envía un correo electrónico usando comandos SMTP
     *
     * Este método implementa el protocolo SMTP directamente usando sockets TCP.
     * SMTP es un protocolo basado en texto donde el cliente envía comandos y
     * el servidor responde con códigos numéricos.
     *
     * Flujo de comunicación SMTP:
     * 1. Conexión TCP al servidor (puerto 587 para STARTTLS)
     * 2. Saludo inicial (EHLO)
     * 3. Iniciar cifrado TLS (STARTTLS)
     * 4. Autenticación (AUTH LOGIN)
     * 5. Especificar remitente (MAIL FROM)
     * 6. Especificar destinatario (RCPT TO)
     * 7. Enviar datos del mensaje (DATA)
     * 8. Cerrar conexión (QUIT)
     *
     * @param emisor Dirección de correo del remitente
     * @param password Contraseña para autenticación SMTP
     * @param destinatario Dirección de correo del destinatario
     * @param asunto Asunto del correo electrónico
     * @param cuerpo Contenido del mensaje
     * @param rutaArchivo Ruta del archivo a adjuntar (null si no hay adjunto)
     */
    public static void enviarCorreoSMTP(String emisor, String password, String destinatario,
                                        String asunto, String cuerpo, String rutaArchivo)
            throws Exception {

        System.out.println("\n--- Procesando envío ---");

        // ============================================================
        // DETERMINAR SERVIDOR SMTP SEGÚN EL DOMINIO DEL CORREO
        // ============================================================
        // El servidor SMTP depende del proveedor de correo
        // Por ejemplo: Gmail usa smtp.gmail.com

        String servidorSmtp;
        int puerto = 587; // Puerto estándar para STARTTLS (TLS explícito)

        // Extraer el dominio del correo (la parte después de @)
        String dominio = emisor.substring(emisor.indexOf("@") + 1);

        // Detectar automáticamente el servidor según el proveedor
        if (emisor.contains("@gmail.com")) {
            servidorSmtp = "smtp.gmail.com";
            System.out.println("Servidor detectado: Gmail");
        } else if (emisor.contains("@outlook.") || emisor.contains("@hotmail.")) {
            servidorSmtp = "smtp.live.com";
            System.out.println("Servidor detectado: Outlook/Hotmail");
        } else if (emisor.contains("@yahoo.")) {
            servidorSmtp = "smtp.mail.yahoo.com";
            System.out.println("Servidor detectado: Yahoo");
        } else {
            // Para otros dominios, intentar con smtp.dominio
            servidorSmtp = "smtp." + dominio;
            System.out.println("Servidor: " + servidorSmtp);
        }

        System.out.println("Conectando a " + servidorSmtp + ":" + puerto + "...");

        // ============================================================
        // ESTABLECER CONEXIÓN TCP CON EL SERVIDOR SMTP
        // ============================================================
        // Socket = canal de comunicación bidireccional sobre TCP/IP
        Socket socket = new Socket(servidorSmtp, puerto);

        // BufferedReader para leer respuestas del servidor
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        // PrintWriter para enviar comandos al servidor
        PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream()), true);

        // ============================================================
        // LEER RESPUESTA INICIAL DEL SERVIDOR (código 220)
        // ============================================================
        // El servidor envía un mensaje de bienvenida al conectarse
        String respuesta = leerRespuesta(reader);
        System.out.println("S: " + respuesta);

        // ============================================================
        // COMANDO EHLO - IDENTIFICACIÓN DEL CLIENTE
        // ============================================================
        // EHLO (Extended HELO) se usa al inicio para identificar el cliente
        // El servidor responde con sus capacidades (extensiones soportadas)
        // Según teoría: "Hay que usarla siempre al inicio de la conexión"
        enviarComando(writer, "EHLO " + dominio);
        respuesta = leerRespuestaMultilinea(reader);
        System.out.println("S: " + respuesta);

        // ============================================================
        // COMANDO STARTTLS - INICIAR CONEXIÓN SEGURA
        // ============================================================
        // STARTTLS eleva la conexión no cifrada a una conexión TLS
        // Puerto 587 requiere STARTTLS (cifrado explícito)
        // Puerto 465 usa SSL/TLS desde el inicio (cifrado implícito)
        enviarComando(writer, "STARTTLS");
        respuesta = leerRespuesta(reader);
        System.out.println("S: " + respuesta);

        // Convertir el socket normal a socket SSL/TLS
        socket = iniciarTLS(socket, servidorSmtp);

        // Recrear los streams sobre la conexión cifrada
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        System.out.println("✓ Conexión TLS establecida");

        // ============================================================
        // EHLO NUEVAMENTE DESPUÉS DE INICIAR TLS
        // ============================================================
        // Después de STARTTLS hay que volver a identificarse con EHLO
        enviarComando(writer, "EHLO " + dominio);
        respuesta = leerRespuestaMultilinea(reader);
        System.out.println("S: " + respuesta);

        // ============================================================
        // AUTENTICACIÓN CON AUTH LOGIN
        // ============================================================
        // AUTH LOGIN es un método de autenticación que usa Base64
        // El servidor pide usuario y contraseña en pasos separados
        enviarComando(writer, "AUTH LOGIN");
        respuesta = leerRespuesta(reader);
        System.out.println("S: " + respuesta);

        // Enviar usuario codificado en Base64
        // El servidor responde con código 334 pidiendo la contraseña
        String usuarioBase64 = Base64.getEncoder().encodeToString(emisor.getBytes());
        enviarComando(writer, usuarioBase64);
        respuesta = leerRespuesta(reader);
        System.out.println("S: " + respuesta);

        // Enviar contraseña codificada en Base64
        // Si la autenticación es exitosa, el servidor responde con código 235
        String passwordBase64 = Base64.getEncoder().encodeToString(password.getBytes());
        enviarComando(writer, passwordBase64);
        respuesta = leerRespuesta(reader);
        System.out.println("S: " + respuesta);

        // Verificar que la autenticación fue exitosa (código 235)
        if (!respuesta.startsWith("235")) {
            throw new Exception("Error de autenticación - Verifica usuario y contraseña");
        }

        System.out.println("✓ Autenticación exitosa");

        // ============================================================
        // COMANDO MAIL FROM - ESPECIFICAR REMITENTE
        // ============================================================
        // MAIL FROM indica al servidor quién envía el correo
        // Según teoría: "Informa al servidor de la dirección del que envía el mensaje"
        // Se espera respuesta con código 250 (OK)
        enviarComando(writer, "MAIL FROM:<" + emisor + ">");
        respuesta = leerRespuesta(reader);
        System.out.println("S: " + respuesta);

        // ============================================================
        // COMANDO RCPT TO - ESPECIFICAR DESTINATARIO
        // ============================================================
        // RCPT TO indica al servidor quién debe recibir el correo
        // Se espera respuesta con código 250 (OK)
        // Se puede usar múltiples veces para enviar a varios destinatarios
        enviarComando(writer, "RCPT TO:<" + destinatario + ">");
        respuesta = leerRespuesta(reader);
        System.out.println("S: " + respuesta);

        // ============================================================
        // COMANDO DATA - INICIAR ENVÍO DEL MENSAJE
        // ============================================================
        // DATA indica que a continuación se enviará el contenido del mensaje
        // El servidor responde con código 354 indicando que está listo
        // Según teoría: "Para finalizar el mensaje se escribe: <CRLF>.<CRLF>"
        enviarComando(writer, "DATA");
        respuesta = leerRespuesta(reader);
        System.out.println("S: " + respuesta);

        System.out.println("✓ Preparando mensaje...");

        // ============================================================
        // CONSTRUIR Y ENVIAR EL MENSAJE CON CABECERAS MIME
        // ============================================================
        // El mensaje consta de:
        // 1. Cabeceras (From, To, Subject, Content-Type, etc.)
        // 2. Línea en blanco
        // 3. Cuerpo del mensaje
        // 4. Finalización con punto en línea sola: "."

        if (rutaArchivo != null && new File(rutaArchivo).exists()) {
            // Si hay archivo adjunto, usar formato MIME multipart/mixed
            // MIME permite enviar múltiples partes con diferentes tipos de contenido
            enviarMensajeConAdjunto(writer, emisor, destinatario, asunto, cuerpo, rutaArchivo);
        } else {
            // Mensaje simple de texto plano sin archivos adjuntos
            enviarMensajeSimple(writer, emisor, destinatario, asunto, cuerpo);
        }

        // ============================================================
        // FINALIZAR EL MENSAJE CON PUNTO EN LÍNEA SOLA
        // ============================================================
        // Según teoría: "Para finalizar se escribe un salto de línea,
        // un punto y otro salto de línea"
        // El servidor responde con código 250 si el mensaje fue aceptado
        writer.println(".");
        respuesta = leerRespuesta(reader);
        System.out.println("S: " + respuesta);

        System.out.println("✓ Mensaje enviado");

        // ============================================================
        // COMANDO QUIT - CERRAR LA CONEXIÓN
        // ============================================================
        // QUIT indica al servidor que queremos terminar la sesión
        // Según teoría: "Indica al servidor que el emisor quiere cerrar la conexión"
        // El servidor responde con código 221 (adiós)
        enviarComando(writer, "QUIT");
        respuesta = leerRespuesta(reader);
        System.out.println("S: " + respuesta);

        // Cerrar streams y socket
        writer.close();
        reader.close();
        socket.close();

        System.out.println("✓ Desconectado del servidor");
    }

    /**
     * Envía un mensaje simple sin archivos adjuntos
     *
     * Estructura del mensaje:
     * - Cabeceras: From, To, Subject, Content-Type
     * - Línea en blanco (separa cabeceras del cuerpo)
     * - Cuerpo del mensaje
     *
     * @param writer Stream de salida al servidor SMTP
     * @param emisor Dirección del remitente
     * @param destinatario Dirección del destinatario
     * @param asunto Asunto del correo
     * @param cuerpo Contenido del mensaje
     */
    private static void enviarMensajeSimple(PrintWriter writer, String emisor,
                                            String destinatario, String asunto, String cuerpo) {
        // Cabecera From: indica el remitente
        writer.println("From: <" + emisor + ">");

        // Cabecera To: indica el destinatario
        writer.println("To: <" + destinatario + ">");

        // Cabecera Subject: asunto del correo
        writer.println("Subject: " + asunto);

        // Cabecera Content-Type: indica que es texto plano en UTF-8
        writer.println("Content-Type: text/plain; charset=UTF-8");

        // Línea en blanco obligatoria que separa cabeceras del cuerpo
        writer.println();

        // Cuerpo del mensaje
        writer.println(cuerpo);
    }

    /**
     * Envía un mensaje con archivo adjunto usando formato MIME multipart/mixed
     *
     * MIME (Multipurpose Internet Mail Extensions) permite enviar:
     * - Texto en diferentes codificaciones
     * - Archivos adjuntos de cualquier tipo
     * - Mensajes con múltiples partes
     *
     * Según teoría: "Las extensiones MIME van dirigidas al intercambio a través
     * de internet de todo tipo de archivos usando texto ASCII de 7 bits"
     *
     * Estructura multipart/mixed:
     * - Cabeceras del mensaje
     * - Boundary (separador entre partes)
     * - Parte 1: Cuerpo del mensaje (texto plano)
     * - Parte 2: Archivo adjunto (codificado en Base64)
     * - Boundary final
     *
     * @param writer Stream de salida al servidor SMTP
     * @param emisor Dirección del remitente
     * @param destinatario Dirección del destinatario
     * @param asunto Asunto del correo
     * @param cuerpo Contenido del mensaje
     * @param rutaArchivo Ruta del archivo a adjuntar
     */
    private static void enviarMensajeConAdjunto(PrintWriter writer, String emisor,
                                                String destinatario, String asunto,
                                                String cuerpo, String rutaArchivo) throws IOException {
        File archivo = new File(rutaArchivo);
        String nombreArchivo = archivo.getName();

        // Boundary: cadena única que separa las diferentes partes del mensaje
        String boundary = "----=_Part_" + System.currentTimeMillis();

        System.out.println("Adjuntando archivo: " + nombreArchivo);

        // ============================================================
        // CABECERAS DEL MENSAJE
        // ============================================================
        writer.println("From: <" + emisor + ">");
        writer.println("To: <" + destinatario + ">");
        writer.println("Subject: " + asunto);

        // MIME-Version: indica que usamos extensiones MIME
        writer.println("MIME-Version: 1.0");

        // Content-Type: multipart/mixed indica mensaje con múltiples partes
        // boundary define el separador entre partes
        writer.println("Content-Type: multipart/mixed; boundary=\"" + boundary + "\"");

        // Línea en blanco que separa cabeceras del cuerpo
        writer.println();

        // ============================================================
        // PARTE 1: CUERPO DEL MENSAJE (TEXTO PLANO)
        // ============================================================
        // Cada parte comienza con --boundary
        writer.println("--" + boundary);

        // Cabeceras de esta parte
        writer.println("Content-Type: text/plain; charset=UTF-8");
        writer.println("Content-Transfer-Encoding: 7bit");
        writer.println();

        // Contenido de esta parte
        writer.println(cuerpo);
        writer.println();

        // ============================================================
        // PARTE 2: ARCHIVO ADJUNTO
        // ============================================================
        writer.println("--" + boundary);

        // Content-Type: indica el tipo de archivo (genérico: application/octet-stream)
        writer.println("Content-Type: application/octet-stream; name=\"" + nombreArchivo + "\"");

        // Content-Transfer-Encoding: Base64 codifica el archivo en texto ASCII
        // Según teoría: "MIME permite archivos usando texto ASCII de 7 bits"
        writer.println("Content-Transfer-Encoding: base64");

        // Content-Disposition: indica que es un archivo adjunto
        writer.println("Content-Disposition: attachment; filename=\"" + nombreArchivo + "\"");
        writer.println();

        // ============================================================
        // LEER ARCHIVO Y CODIFICAR EN BASE64
        // ============================================================
        // Base64 convierte bytes binarios a caracteres ASCII imprimibles
        byte[] contenidoArchivo = Files.readAllBytes(archivo.toPath());
        String archivoBase64 = Base64.getEncoder().encodeToString(contenidoArchivo);

        // Enviar en líneas de 76 caracteres (estándar MIME)
        // MIME requiere que las líneas no excedan 76 caracteres
        for (int i = 0; i < archivoBase64.length(); i += 76) {
            int fin = Math.min(i + 76, archivoBase64.length());
            writer.println(archivoBase64.substring(i, fin));
        }

        writer.println();

        // ============================================================
        // BOUNDARY FINAL
        // ============================================================
        // El boundary final lleva -- al inicio y al final
        // Indica que no hay más partes en el mensaje
        writer.println("--" + boundary + "--");
    }

    /**
     * Envía un comando SMTP al servidor
     *
     * Muestra el comando enviado precedido de "C:" (Cliente)
     * para facilitar el seguimiento de la comunicación
     *
     * @param writer Stream de salida al servidor
     * @param comando Comando SMTP a enviar
     */
    private static void enviarComando(PrintWriter writer, String comando) {
        System.out.println("C: " + comando);
        writer.println(comando);
    }

    /**
     * Lee una respuesta de una sola línea del servidor SMTP
     *
     * Las respuestas del servidor tienen el formato:
     * [código] [mensaje]
     *
     * Códigos comunes:
     * - 220: Servicio listo
     * - 250: Comando completado exitosamente
     * - 235: Autenticación exitosa
     * - 334: Respuesta intermedia de autenticación
     * - 354: Listo para recibir datos del mensaje
     * - 221: Cerrando conexión
     * - 5xx: Errores
     *
     * @param reader Stream de entrada del servidor
     * @return Línea de respuesta del servidor
     */
    private static String leerRespuesta(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    /**
     * Lee una respuesta multilínea del servidor SMTP
     *
     * Algunas respuestas del servidor ocupan múltiples líneas.
     * En respuestas multilínea:
     * - Las líneas intermedias tienen un guión después del código: "250-mensaje"
     * - La última línea tiene un espacio después del código: "250 mensaje"
     *
     * Ejemplo de respuesta EHLO:
     * 250-smtp.gmail.com at your service
     * 250-SIZE 35882577
     * 250-8BITMIME
     * 250-STARTTLS
     * 250 SMTPUTF8
     *
     * @param reader Stream de entrada del servidor
     * @return Todas las líneas de respuesta concatenadas
     */
    private static String leerRespuestaMultilinea(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String linea;

        while ((linea = reader.readLine()) != null) {
            sb.append(linea).append("\n");

            // Las respuestas multilínea terminan cuando el código no tiene guión
            // Es decir, cuando el carácter en la posición 3 es un espacio
            if (linea.length() >= 4 && linea.charAt(3) == ' ') {
                break;
            }
        }

        return sb.toString();
    }

    /**
     * Inicia una conexión TLS sobre el socket existente
     *
     * STARTTLS permite elevar una conexión no cifrada a una conexión TLS
     * Este proceso se llama "cifrado explícito" o "TLS oportunista"
     *
     * Según teoría: "El puerto 587 requiere que el cliente utilice STARTTLS
     * para elevar la conexión al protocolo TLS"
     *
     * Pasos:
     * 1. Obtener una fábrica de sockets SSL
     * 2. Crear un socket SSL envolviendo el socket existente
     * 3. Iniciar el handshake TLS
     *
     * @param socket Socket TCP existente
     * @param host Nombre del host (para verificación del certificado)
     * @return Socket SSL/TLS ya establecido
     */
    private static Socket iniciarTLS(Socket socket, String host) throws Exception {
        // Obtener la fábrica de sockets SSL por defecto
        javax.net.ssl.SSLSocketFactory factory =
                (javax.net.ssl.SSLSocketFactory) javax.net.ssl.SSLSocketFactory.getDefault();

        // Crear un socket SSL envolviendo el socket existente
        // El parámetro 'true' indica que se cierre el socket subyacente al cerrar este
        javax.net.ssl.SSLSocket sslSocket =
                (javax.net.ssl.SSLSocket) factory.createSocket(
                        socket, host, socket.getPort(), true);

        // Iniciar el handshake TLS (negociación de cifrado)
        sslSocket.startHandshake();

        return sslSocket;
    }
}