package org.iesch.psp.Ej03;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.Scanner;

public class MainCrypto {

    // Clave de cifrado (16 bytes = 128 bits)
    private static final byte[] key = {
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
            0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16
    };

    // Vector de inicialización (IV)
    private static final byte[] iv = {
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
            0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16
    };

    private static final String path = "archivo_cifrado.dat";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Introduce el texto a cifrar:");
        String texto = sc.nextLine();

        cifrado(texto);
        descifrado();
    }

    private static void cifrado(String texto) {
        try {
            FileOutputStream fileOut = new FileOutputStream(path);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(cipherOut, "UTF-8"));

            writer.write(texto);
            writer.newLine();

            writer.close();
            cipherOut.close();
            fileOut.close();

            System.out.println("Fichero cifrado correctamente");

        } catch (Exception e) {
            System.out.println("Error en el cifrado");
            e.printStackTrace();
        }
    }

    private static void descifrado() {
        try {
            FileInputStream fileIn = new FileInputStream(path);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
            BufferedReader reader = new BufferedReader(new InputStreamReader(cipherIn, "UTF-8"));

            System.out.println("Texto en claro contenido en el fichero:");
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            reader.close();
            cipherIn.close();
            fileIn.close();

        } catch (Exception e) {
            System.out.println("Lectura fichero errónea");
            e.printStackTrace();
        }
    }
}