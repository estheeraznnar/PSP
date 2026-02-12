package org.iesch.psp.Ej04;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.Cipher;

public class RSAUtil {

    public static class RSAKeys {
        public final String privateKey;
        public final String publicKey;

        public RSAKeys(String privateKey, String publicKey) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
        }
    }

    public static RSAKeys createRSAKeys(int size) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(size);
            KeyPair pair = keyGen.generateKeyPair();

            String privateKey = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
            String publicKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());

            return new RSAKeys(privateKey, publicKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encrypt(String text, String base64PublicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey publicKey = kf.generatePublic(spec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] bytes = text.getBytes(StandardCharsets.UTF_16LE);
            byte[] cipherBytes = cipher.doFinal(bytes);

            return Base64.getEncoder().encodeToString(cipherBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Generar claves
        RSAKeys keys = createRSAKeys(2048);
        System.out.println("Clave pública: " + keys.publicKey);
        System.out.println("Clave privada: " + keys.privateKey);

        // Cifrar
        System.out.println("\nIntroduce el mensaje a cifrar:");
        String mensaje = sc.nextLine();

        String cifrado = encrypt(mensaje, keys.publicKey);
        System.out.println("Cifrado: " + cifrado);

        // Descifrar
        String descifrado = decrypt(cifrado, keys.privateKey);
        System.out.println("Descifrado: " + descifrado);
    }
}