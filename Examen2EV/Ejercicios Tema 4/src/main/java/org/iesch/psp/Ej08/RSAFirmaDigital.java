package org.iesch.psp.Ej08;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import java.util.Scanner;

public class RSAFirmaDigital {

    public static class RSAKeys {
        public final String privateKey;
        public final String publicKey;

        public RSAKeys(String privateKey, String publicKey) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
        }
    }

    // Crear par de claves RSA
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

    // Firmar mensaje con clave privada
    public static String sign(String message, String base64PrivateKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(spec);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(message.getBytes(StandardCharsets.UTF_16LE));

            byte[] signatureBytes = signature.sign();
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Verificar firma con clave pública
    public static boolean verify(String message, String signatureStr, String base64PublicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey publicKey = kf.generatePublic(spec);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(message.getBytes(StandardCharsets.UTF_16LE));

            byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);
            return signature.verify(signatureBytes);
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

        // Firmar mensaje
        System.out.println("\nIntroduce el mensaje a firmar:");
        String mensaje = sc.nextLine();

        String firma = sign(mensaje, keys.privateKey);
        System.out.println("Firma digital: " + firma);

        // Verificar firma con mensaje original
        System.out.println("\nIntroduce el mensaje para verificar la firma:");
        String verificar = sc.nextLine();

        if (verify(verificar, firma, keys.publicKey)) {
            System.out.println("Firma válida. El mensaje es auténtico y no ha sido alterado.");
        } else {
            System.out.println("ERROR! Firma no válida. El mensaje ha sido alterado.");
        }
    }
}