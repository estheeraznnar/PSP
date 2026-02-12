package org.example.FTP_Solo_Descarga;

import java.io.IOException;
import java.util.Scanner;

/**
 * Clase principal que:
 *  - Pide por consola host, usuario, contraseña y nombre de fichero remoto.
 *  - Se conecta al servidor FTP usando SimpleFTPClient.
 *  - Muestra PWD, LIST y simula la descarga del fichero.
 */
public class DownloadManager {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== DownloadManager FTP (solo descarga, modo texto) ===");
        System.out.print("Host FTP (ej: ftp.example.com): ");
        String host = sc.nextLine().trim();

        System.out.print("Usuario: ");
        String user = sc.nextLine().trim();

        System.out.print("Contraseña: ");
        String pass = sc.nextLine().trim();

        System.out.print("Nombre de fichero remoto (RETR): ");
        String remoteName = sc.nextLine().trim();

        System.out.print("Nombre de fichero local (donde guardar): ");
        String localName = sc.nextLine().trim();

        sc.close();

        SimpleFTPClient ftp = new SimpleFTPClient(host);

        try {
            // 1. Conectar
            ftp.connect();

            // 2. Login
            if (!ftp.login(user, pass)) {
                System.out.println("No se pudo iniciar sesión. Saliendo.");
                ftp.disconnect();
                return;
            }

            // 3. Mostrar directorio actual
            ftp.pwd();

            // 4. Listar contenido
            ftp.list();

            // 5. Descargar fichero
            ftp.downloadFile(remoteName, localName);

            // 6. Desconectar
            ftp.disconnect();

        } catch (IOException e) {
            System.err.println("Error en DownloadManager: " + e.getMessage());
            ftp.disconnect();
        }
    }
}

