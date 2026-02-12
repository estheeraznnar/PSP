package org.iesch.psp.FTPRSA;// --- FTPCifradoRSA.java ---
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.security.cert.X509Certificate;
import java.util.Scanner;

public class FTPCifradoRSA {

    public static void main(String[] args) {
        FTPSClient ftp = new FTPSClient("TLS", false);
        Scanner sc = new Scanner(System.in);

        try {
            // Aceptamos certificado autofirmado
            ftp.setTrustManager(new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) {}
                public void checkServerTrusted(X509Certificate[] xcs, String string) {}
                public X509Certificate[] getAcceptedIssuers() { return null; }
            });

            // Datos de conexión
            System.out.println("Introduce la dirección del servidor:");
            String servidor = sc.nextLine().trim();

            System.out.println("Introduce el usuario:");
            String user = sc.nextLine().trim();

            System.out.println("Introduce la contraseña:");
            String psw = sc.nextLine().trim();

            // Conectamos
            System.out.println("Conectando a " + servidor + "...");
            ftp.connect(servidor);
            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                ftp.disconnect();
                System.out.println("No se pudo conectar al servidor");
                return;
            }
            System.out.println("Conexión establecida.");

            // Login
            if (!ftp.login(user, psw)) {
                System.out.println("Error de autenticación.");
                ftp.disconnect();
                return;
            }
            System.out.println("Login correcto.");

            ftp.execPBSZ(0);
            ftp.execPROT("C");
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            // Generamos claves RSA
            System.out.println("\nGenerando claves RSA...");
            RSAUtil.RSAKeys keys = RSAUtil.createRSAKeys(2048);
            System.out.println("Claves generadas correctamente.");

            // Menú
            boolean salir = false;
            while (!salir) {
                System.out.println("\n--- MENÚ FTP CIFRADO ---");
                System.out.println("1. Listar directorio");
                System.out.println("2. Subir fichero cifrado con RSA");
                System.out.println("3. Descargar y descifrar fichero");
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
                        System.out.println("Introduce el texto a cifrar y subir:");
                        String texto = sc.nextLine();

                        // Ciframos con clave pública
                        String textoCifrado = RSAUtil.encrypt(texto, keys.publicKey);
                        System.out.println("Texto cifrado: " + textoCifrado);

                        System.out.println("Nombre del fichero en el servidor:");
                        String nombreRemoto = sc.nextLine().trim();

                        // Convertimos el texto cifrado a InputStream para subirlo
                        ByteArrayInputStream bis = new ByteArrayInputStream(textoCifrado.getBytes("UTF-8"));
                        ftp.enterLocalPassiveMode();
                        if (ftp.storeFile(nombreRemoto, bis)) {
                            System.out.println(">> ÉXITO: Fichero cifrado subido.");
                        } else {
                            System.out.println(">> ERROR: No se pudo subir.");
                        }
                        bis.close();
                        break;

                    case "3":
                        System.out.println("Nombre del fichero cifrado a descargar:");
                        String fileDown = sc.nextLine().trim();

                        // Descargamos a memoria
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        ftp.enterLocalPassiveMode();
                        if (ftp.retrieveFile(fileDown, bos)) {
                            String contenidoCifrado = bos.toString("UTF-8");
                            System.out.println("Contenido cifrado: " + contenidoCifrado);

                            // Desciframos con clave privada
                            String contenidoDescifrado = RSAUtil.decrypt(contenidoCifrado, keys.privateKey);
                            System.out.println("Contenido descifrado: " + contenidoDescifrado);
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