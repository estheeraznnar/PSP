package org.example.TCP;

import java.net.*;
import java.io.*;
import java.security.*;
import javax.crypto.Cipher;
import java.util.Base64;

import java.net.*;
import java.io.*;
import java.security.*;

/**
 * Cliente TCP que establece comunicación cifrada con un servidor usando RSA.
 * Genera un par de claves RSA, envía la clave pública al servidor,
 * recibe mensajes cifrados con esa clave pública y los descifra con su clave privada.
 */
public class ClienteTCP {

    // Clave AES para implementación alternativa (actualmente no usada)
    private static final String CLAVE_AES = "1234567890123456";

    /**
     * Establece conexión con el servidor, intercambia claves RSA,
     * envía un mensaje y recibe la respuesta cifrada.
     *
     * @param args Argumentos de línea de comandos (no utilizados)
     * @throws Exception si hay error en la conexión, generación de claves o cifrado
     */
    public static void main(String[] args) throws Exception {

        // Establecer conexión TCP con el servidor en localhost puerto 5000
        Socket socket = new Socket("localhost", 5000);

        // Configurar flujos de entrada/salida para comunicación con el servidor
        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true); // autoFlush activado

        // Configurar flujo de entrada para leer del teclado
        BufferedReader teclado = new BufferedReader(
                new InputStreamReader(System.in));

        // Generar par de claves RSA (pública y privada) de 2048 bits
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048); // Tamaño de clave seguro (mínimo recomendado)

        KeyPair pair = generator.generateKeyPair();
        PublicKey publica = pair.getPublic();   // Para enviar al servidor
        PrivateKey privada = pair.getPrivate(); // Para descifrar mensajes recibidos (mantener secreta)

        // Paso 1: Recibir solicitud del servidor y enviar clave pública
        System.out.println(in.readLine()); // Mostrar: "Envía tu clave pública en Base64:"
        String claveBase64 = Base64.getEncoder().encodeToString(publica.getEncoded());
        out.println(claveBase64);

        // Paso 2: Recibir solicitud del servidor y enviar mensaje desde teclado
        System.out.println(in.readLine()); // Mostrar: "Escribe el mensaje que quieres enviar:"
        String mensaje = teclado.readLine();
        out.println(mensaje);

        // Paso 3: Recibir encabezado y mensaje cifrado del servidor
        System.out.println(in.readLine()); // Mostrar: "Mensaje cifrado:"
        String cifradoBase64 = in.readLine();
        System.out.printf(cifradoBase64); // Mostrar el mensaje cifrado recibido

        // Decodificar el mensaje cifrado desde Base64 a bytes
        byte[] cifrado = Base64.getDecoder().decode(cifradoBase64);

        // Paso 4: Descifrar el mensaje usando la clave privada del cliente
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privada); // Modo descifrado con clave privada
        byte[] descifrado = cipher.doFinal(cifrado);

        // Mostrar el mensaje descifrado en texto plano
        System.out.println("Mensaje recibido descifrado:");
        System.out.println(new String(descifrado));

        // Cerrar la conexión con el servidor
        socket.close();

        /* IMPLEMENTACIÓN ALTERNATIVA CON AES (actualmente comentada)
         * Esta sección muestra cómo descifrar con AES simétrico en lugar de RSA.
         * Con AES, ambos (cliente y servidor) deben conocer la misma clave secreta.
         * RSA es asimétrico: el servidor cifra con clave pública, el cliente descifra con clave privada.
         *
        byte[] cifrado = Base64.getDecoder().decode(cifradoBase64);

        SecretKeySpec key = new SecretKeySpec(CLAVE_AES.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] descifrado = cipher.doFinal(cifrado);
        **/
    }
}
