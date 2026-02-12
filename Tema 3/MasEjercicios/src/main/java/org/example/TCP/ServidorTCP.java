package org.example.TCP;

import java.net.*;
import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.Cipher;
import java.util.Base64;

/**
 * Servidor TCP multi-hilo que implementa comunicación cifrada con clientes.
 * Recibe la clave pública RSA del cliente, cifra mensajes con ella y los envía de vuelta.
 * Cada conexión de cliente se maneja en un hilo separado para permitir múltiples conexiones concurrentes.
 */
public class ServidorTCP {

    // Clave AES para implementación alternativa (actualmente no usada)
    private static final String CLAVE_AES = "1234567890123456";

    /**
     * Inicia el servidor TCP en el puerto 5000 y acepta conexiones entrantes.
     * Cada cliente se maneja en un hilo separado para permitir múltiples conexiones simultáneas.
     *
     * @param args Argumentos de línea de comandos (no utilizados)
     * @throws Exception si hay error al crear el ServerSocket o aceptar conexiones
     */
    public static void main(String[] args) throws Exception {

        // Crear socket servidor en el puerto 5000
        ServerSocket servidor = new ServerSocket(5000);
        System.out.println("Servidor iniciado...");

        // Bucle infinito para aceptar conexiones de clientes
        while (true) {
            // Esperar y aceptar nueva conexión de cliente (bloqueante)
            Socket socket = servidor.accept();

            // Crear nuevo hilo para manejar el cliente sin bloquear el servidor
            new Thread(() -> manejarCliente(socket)).start();
        }
    }

    /**
     * Maneja la comunicación con un cliente individual usando cifrado RSA.
     * Protocolo:
     * 1. Recibe clave pública RSA del cliente
     * 2. Solicita mensaje a enviar
     * 3. Cifra el mensaje con la clave pública del cliente
     * 4. Envía el mensaje cifrado en Base64
     *
     * @param socket Socket de conexión con el cliente
     */
    public static void manejarCliente(Socket socket) {
        try {

            // Configurar flujos de entrada y salida para comunicación texto
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true); // autoFlush activado

            // Paso 1: Solicitar y recibir la clave pública del cliente
            out.println("Envía tu clave pública en Base64:");
            String claveBase64 = in.readLine();
            System.out.println("Clave pública recibida del cliente: " + claveBase64.substring(0, 20) + "...");

            // Decodificar la clave pública desde Base64 a bytes
            byte[] claveBytes = Base64.getDecoder().decode(claveBase64);

            // Reconstruir el objeto PublicKey desde los bytes decodificados
            KeyFactory factory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec spec = new X509EncodedKeySpec(claveBytes); // Formato estándar para claves públicas
            PublicKey clavePublica = factory.generatePublic(spec);

            // Paso 2: Solicitar el mensaje a cifrar
            out.println("Escribe el mensaje que quieres enviar:");
            String mensaje = in.readLine();

            // Paso 3: Cifrar el mensaje con RSA usando la clave pública del cliente
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, clavePublica);

            byte[] cifrado = cipher.doFinal(mensaje.getBytes());

            // Codificar el mensaje cifrado en Base64 para transmisión segura
            String cifradoBase64 = Base64.getEncoder().encodeToString(cifrado);

            // Paso 4: Enviar el mensaje cifrado al cliente
            out.println("Mensaje cifrado:");
            out.println(cifradoBase64);

            // Cerrar la conexión con el cliente
            socket.close();

            /* IMPLEMENTACIÓN ALTERNATIVA CON AES (actualmente comentada)
             * Esta sección muestra cómo cifrar con AES simétrico en lugar de RSA.
             * AES es más rápido pero requiere que ambos lados conozcan la clave secreta.
             *
            SecretKeySpec key = new SecretKeySpec(CLAVE_AES.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] cifrado = cipher.doFinal(mensaje.getBytes());
            String cifradoBase64 = Base64.getEncoder().encodeToString(cifrado);
            **/

        } catch (Exception e) {
            // Imprimir traza completa de error para debugging (no recomendado en producción)
            e.printStackTrace();
        }
    }
}
