package org.iesch.psp.EncriptacionAsimetricaRSA;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import javax.crypto.Cipher;

public class RSAUtil {

    // Clase interna para almacenar el par de claves en formato Base64 [cite: 648]
    public static class RSAKeys {
        public final String privateKey;
        public final String publicKey;

        public RSAKeys(String privateKey, String publicKey) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
        }
    }

    /**
     * Crea un par de claves RSA del tamaño especificado[cite: 658].
     */
    public static RSAKeys createRSAKeys(int size) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(size);
            KeyPair pair = keyGen.generateKeyPair();

            // Convertimos las claves a String Base64 para facilitar su almacenamiento [cite: 664, 666]
            String privateKey = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
            String publicKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());

            return new RSAKeys(privateKey, publicKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Cifra un texto utilizando la clave pública[cite: 681].
     */
    public static String encrypt(String text, String base64PublicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey publicKey = kf.generatePublic(spec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            // Se utiliza UTF_16LE para equivalencia con Encoding.Unicode de .NET [cite: 689, 690]
            byte[] bytes = text.getBytes(StandardCharsets.UTF_16LE);
            byte[] cipherBytes = cipher.doFinal(bytes);

            return Base64.getEncoder().encodeToString(cipherBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Descifra un texto utilizando la clave privada[cite: 697].
     */
    public static String decrypt(String cipherText, String base64PrivateKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(spec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] cipherBytes = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(cipherBytes);
            return new String(decrypted, StandardCharsets.UTF_16LE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Ejemplo de uso [cite: 718]
    public static void main(String[] args) {
        // Generamos claves de 2048 bits
        RSAKeys keys = createRSAKeys(2048);
        System.out.println("Clave pública: " + keys.publicKey);
        System.out.println("Clave privada: " + keys.privateKey);

        String mensaje = "Hola Mundo con RSA";

        // Encriptación
        String cifrado = encrypt(mensaje, keys.publicKey);
        System.out.println("Cifrado: " + cifrado);

        // Desencriptación
        String descifrado = decrypt(cifrado, keys.privateKey);
        System.out.println("Descifrado: " + descifrado);
    }
}