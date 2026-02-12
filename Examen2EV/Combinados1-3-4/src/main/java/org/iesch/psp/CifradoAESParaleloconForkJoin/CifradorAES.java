package org.iesch.psp.CifradoAESParaleloconForkJoin;// --- CifradorAES.java (Utilidad de cifrado) ---
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;

public class CifradorAES {

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

    public static void cifrarArchivo(String archivoEntrada, String archivoSalida) {
        try {
            // Leer contenido del archivo
            BufferedReader reader = new BufferedReader(new FileReader(archivoEntrada));
            StringBuilder contenido = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
            reader.close();

            // Cifrar y escribir
            FileOutputStream fileOut = new FileOutputStream(archivoSalida);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(cipherOut, "UTF-8"));

            writer.write(contenido.toString());

            writer.close();
            cipherOut.close();
            fileOut.close();

        } catch (Exception e) {
            System.out.println("Error cifrando " + archivoEntrada + ": " + e.getMessage());
        }
    }
}