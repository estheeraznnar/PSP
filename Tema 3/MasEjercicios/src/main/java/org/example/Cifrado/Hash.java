package org.example.Cifrado;

import java.security.MessageDigest;
import java.util.Base64;
public class Hash {
    public static void main(String[] args) throws Exception {
        String mensaje = "HOLA";

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] hashBytes = digest.digest(mensaje.getBytes("UTF-8"));

        String hashBase64 = Base64.getEncoder().encodeToString(hashBytes);

        System.out.println("Mensaje original: " + mensaje);
        System.out.println("Hash SHA-256: " + hashBase64);
    }

}