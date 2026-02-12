package org.iesch.psp.Ej06;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;

public class HmacSHA512Util {

    // Generar una clave secreta aleatoria
    public static byte[] generateKey() {
        byte[] key = new byte[64]; // 512 bits
        SecureRandom random = new SecureRandom();
        random.nextBytes(key);
        return key;
    }

    // Generar HMAC-SHA512
    public static String getHmac(String msg, byte[] key) {
        try {
            byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_16LE);

            SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA512");
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(secretKey);

            byte[] hmacValue = mac.doFinal(msgBytes);

            return Base64.getEncoder().encodeToString(hmacValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Verificar HMAC-SHA512
    public static boolean checkHmac(String msg, String hmac, byte[] key) {
        try {
            byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_16LE);
            byte[] hmacBytes = Base64.getDecoder().decode(hmac);

            SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA512");
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(secretKey);

            byte[] hmacValue = mac.doFinal(msgBytes);

            if (hmacValue.length != hmacBytes.length) {
                return false;
            }
            for (int i = 0; i < hmacBytes.length; i++) {
                if (hmacValue[i] != hmacBytes[i]) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Generar clave secreta
        byte[] key = generateKey();
        System.out.println("Clave secreta: " + Base64.getEncoder().encodeToString(key));

        // Firmar mensaje
        System.out.println("\nIntroduce un mensaje:");
        String mensaje = sc.nextLine();

        String hmac = getHmac(mensaje, key);
        System.out.println("HMAC-SHA512: " + hmac);

        // Verificar
        System.out.println("\nIntroduce un mensaje para verificar:");
        String verificar = sc.nextLine();

        if (checkHmac(verificar, hmac, key)) {
            System.out.println("El HMAC coincide. Integridad verificada.");
        } else {
            System.out.println("ERROR! El HMAC no coincide. El mensaje ha sido alterado.");
        }
    }
}