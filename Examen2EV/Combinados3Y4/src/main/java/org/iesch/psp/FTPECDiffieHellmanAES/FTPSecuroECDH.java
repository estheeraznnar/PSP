package org.iesch.psp.FTPECDiffieHellmanAES;// --- FTPSecuroECDH.java ---
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.security.KeyPair;
import java.util.Base64;
import java.util.Scanner;

public class FTPSecuroECDH {

    public static void main(String[] args) {
        FTPSClient ftp = new FTPSClient("TLS", false);
        Scanner sc = new Scanner(System.in);

        try {
            // Aceptamos certificado autofirmado
            ftp.setTrustManager(new X509TrustManager() {
                public void checkClientTrusted(java.security.cert.X509Certificate[] xcs, String s) {}
                public void checkServerTrusted(java.security.cert.X509Certificate[] xcs, String s) {}
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
            });

            // --- INTERCAMBIO DE CLAVES ECDH ---
            System.out.println("=== INTERCAMBIO DE CLAVES ECDH ===");

            // Alice genera sus claves
            System.out.println("\n--- ALICE genera sus claves ---");
            KeyPair aliceKeyPair = ECDHUtil.generateECKeyPair();
            String alicePubKeyBase64 = Base64.getEncoder().encodeToString(
                    aliceKeyPair.getPublic().getEncoded());
            System.out.println("Clave pública Alice: " + alicePubKeyBase64);

            // Bob genera sus claves
            System.out.println("\n--- BOB genera sus claves ---");
            KeyPair bobKeyPair = ECDHUtil.generateECKeyPair();
            String bobPubKeyBase64 = Base64.getEncoder().encodeToString(
                    bobKeyPair.getPublic().getEncoded());
            System.out.println("Clave pública Bob: " + bobPubKeyBase64);

            // Ambos derivan la clave compartida
            byte[] aliceSharedKey = ECDHUtil.deriveSharedSecret(
                    aliceKeyPair.getPrivate(), bobKeyPair.getPublic());
            byte[] bobSharedKey = ECDHUtil.deriveSharedSecret(
                    bobKeyPair.getPrivate(), aliceKeyPair.getPublic());

            System.out.println("\nClave compartida Alice: " +
                    Base64.getEncoder().encodeToString(aliceSharedKey));
            System.out.println("Clave compartida Bob:   " +
                    Base64.getEncoder().encodeToString(bobSharedKey));
            System.out.println("¿Coinciden? " +
                    Base64.getEncoder().encodeToString(aliceSharedKey)
                            .equals(Base64.getEncoder().encodeToString(bobSharedKey)));

            // --- CONEXIÓN FTP ---
            System.out.println("\n=== CONEXIÓN FTP ===");
            System.out.println("Introduce la dirección del servidor:");
            String servidor = sc.nextLine().trim();

            System.out.println("Introduce el usuario:");
            String user = sc.nextLine().trim();

            System.out.println("Introduce la contraseña:");
            String psw = sc.nextLine().trim();

            System.out.println("Conectando a " + servidor + "...");
            ftp.connect(servidor);
            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                ftp.disconnect();
                System.out.println("No se pudo conectar al servidor");
                return;
            }
            System.out.println("Conexión establecida.");

            if (!ftp.login(user, psw)) {
                System.out.println("Error de autenticación.");
                ftp.disconnect();
                return;
            }
            System.out.println("Login correcto.");

            ftp.execPBSZ(0);
            ftp.execPROT("P");
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            // --- MENÚ ---
            boolean salir = false;
            while (!salir) {
                System.out.println("\n--- MENÚ FTP SEGURO (ECDH + AES) ---");
                System.out.println("1. Listar directorio");
                System.out.println("2. ALICE sube fichero cifrado");
                System.out.println("3. BOB descarga y descifra fichero");
                System.out.println("0. Salir");
                System.out.print("Elige una opción: ");
                String opcion = sc.nextLine().trim();

                switch (opcion) {
                    case "1":
                        System.out.println("--- Listado de: " + ftp.printWorkingDirectory() + " ---");
                        ftp.enterLocalPassiveMode();
                        FTPFile[] files = ftp.listFiles();
                        for (FTPFile file : files) {
                            System.out.println("- " + file.getName() + " (" + file.getSize() + " bytes)");
                        }
                        break;

                    case "2":
                        // Alice cifra y sube
                        System.out.println("ALICE - Introduce el texto a cifrar y subir:");
                        String texto = sc.nextLine();

                        String textoCifrado = ECDHUtil.encrypt(texto, aliceSharedKey);
                        System.out.println("Texto cifrado con clave compartida: " + textoCifrado);

                        System.out.println("Nombre del fichero en el servidor:");
                        String nombreRemoto = sc.nextLine().trim();

                        ByteArrayInputStream bis = new ByteArrayInputStream(
                                textoCifrado.getBytes("UTF-8"));
                        ftp.enterLocalPassiveMode();
                        if (ftp.storeFile(nombreRemoto, bis)) {
                            System.out.println(">> ÉXITO: Fichero cifrado subido por Alice.");
                        } else {
                            System.out.println(">> ERROR: No se pudo subir.");
                        }
                        bis.close();
                        break;

                    case "3":
                        // Bob descarga y descifra
                        System.out.println("BOB - Nombre del fichero cifrado a descargar:");
                        String fileDown = sc.nextLine().trim();

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        ftp.enterLocalPassiveMode();
                        if (ftp.retrieveFile(fileDown, bos)) {
                            String contenidoCifrado = bos.toString("UTF-8");
                            System.out.println("Contenido cifrado: " + contenidoCifrado);

                            // Bob descifra con su clave compartida
                            String contenidoDescifrado = ECDHUtil.decrypt(contenidoCifrado, bobSharedKey);
                            System.out.println("Contenido descifrado por Bob: " + contenidoDescifrado);
                        } else {
                            System.out.println(">> ERROR: No se pudo descargar.");
                        }
                        bos.close();
                        break;

                    case "0":
                        salir = true;
                        break;

                    default:
                        System.out.println("Opción no válida");
                        break;
                }
            }

            ftp.logout();
            ftp.disconnect();
            System.out.println("Desconectado.");

        } catch (IOException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }
}