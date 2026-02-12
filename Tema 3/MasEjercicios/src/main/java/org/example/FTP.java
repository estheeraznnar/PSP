package org.example;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.nio.file.Path;
import java.security.cert.X509Certificate;
import java.util.Scanner;

/**
 * Cliente FTP para listar, descargar y subir archivos.
 */
public class FTP {

    public static void main(String[] args) {
        FTPClient ftp = new FTPClient();

        try {
            // Conectar al servidor FTP
            ftp.connect("ftp.dlptest.com");

            // Autenticar usuario
            ftp.login("dlpuser", "rNrKYTX9g7z3RgJRmxWuGHbeu");

            // Modo pasivo: cliente inicia conexiones (necesario tras firewalls)
            ftp.enterLocalPassiveMode();

            // Listar archivos del servidor
            listDirectory(ftp);

            Scanner sc = new Scanner(System.in);

            // Descargar archivos
            downloadFiles(sc, ftp);

            // Subir archivos
            uploadFiles(sc, ftp);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lista archivos del directorio actual en el servidor.
     */
    private static void listDirectory(FTPClient ftp) throws IOException {
        FTPFile[] files = ftp.listFiles();
        for (FTPFile file : files) {
            System.out.println(file.getName());
        }
    }

    /**
     * Descarga un archivo del servidor a Downloads local.
     */
    private static void downloadFiles(Scanner sc, FTPClient ftp) {
        System.out.println("Fichero a descargar: ");
        String file = sc.nextLine().trim();

        if (!file.isEmpty()) {
            System.out.println("Introduzca nombre de usuario: ");
            String username = sc.nextLine().trim();

            // Crear archivo local en Downloads y descargar desde servidor
            try (FileOutputStream fos = new FileOutputStream("C:/Users/" + username + "/Downloads/" + file)){
                ftp.retrieveFile(file, fos);
                System.out.println("Completado");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Sube un archivo local al servidor FTP.
     */
    private static void uploadFiles(Scanner sc, FTPClient ftp) throws IOException {
        System.out.println("Introduzca la ruta con el fichero: ");
        String path = sc.nextLine().trim();
        File archivoLocal = new File(path);

        if (!path.isEmpty()) {
            // Leer archivo local y subirlo al servidor
            try (FileInputStream in = new FileInputStream(archivoLocal)) {
                boolean ok = ftp.storeFile(archivoLocal.getName(), in);
                if (ok) {
                    System.out.println("Subido");
                } else {
                    System.out.println("Error");
                }
            }
        }
    }
}
