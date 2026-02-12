package org.iesch.psp.Ej01;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

public class SHA256 {

    public static String getHash(String msg) {
        try {
            byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_16LE);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashValue = digest.digest(msgBytes);
            return Base64.getEncoder().encodeToString(hashValue);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkHash(String msg, String hash) {
        try {
            byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_16LE);
            byte[] hashBytes = Base64.getDecoder().decode(hash);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashValue = digest.digest(msgBytes);

            if (hashValue.length != hashBytes.length) {
                return false;
            }
            for (int i = 0; i < hashBytes.length; i++) {
                if (hashValue[i] != hashBytes[i]) {
                    return false;
                }
            }
            return true;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Introduce un mensaje:");
        String mensaje = sc.nextLine();

        String hash = getHash(mensaje);
        System.out.println("Hash SHA-256: " + hash);

        System.out.println("\nIntroduce un mensaje para verificar:");
        String verificar = sc.nextLine();

        if (checkHash(verificar, hash)) {
            System.out.println("El hash coincide");
        } else {
            System.out.println("ERROR! El hash no coincide");
        }
    }
}