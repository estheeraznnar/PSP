package org.iesch.psp.ftp;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Scanner;

public class ClienteFTPBasico {

    private static FTPSClient ftp;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        ftp = new FTPSClient("TLS", false);

        try {
            // Aceptamos certificado autofirmado (NO seguro en producción)
            ftp.setTrustManager(new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) {}
                public void checkServerTrusted(X509Certificate[] xcs, String string) {}
                public X509Certificate[] getAcceptedIssuers() { return null; }
            });

            // Solicitar datos de conexión
            System.out.println("=== CLIENTE FTP ===");
            System.out.print("Dirección del servidor: ");
            String servidor = sc.nextLine();

            System.out.print("Usuario: ");
            String usuario = sc.nextLine();

            System.out.print("Contraseña: ");
            String password = sc.nextLine();

            // Conectar al servidor
            conectar(servidor, usuario, password);

            // Menú de opciones
            boolean continuar = true;
            while (continuar) {
                System.out.println("\n=== MENÚ ===");
                System.out.println("1. Listar directorio actual");
                System.out.println("2. Subir archivo");
                System.out.println("3. Descargar archivo");
                System.out.println("4. Cambiar directorio");
                System.out.println("5. Salir");
                System.out.print("Seleccione una opción: ");

                int opcion = sc.nextInt();
                sc.nextLine(); // Limpiar buffer

                switch (opcion) {
                    case 1:
                        listarDirectorio();
                        break;
                    case 2:
                        subirArchivo();
                        break;
                    case 3:
                        descargarArchivo();
                        break;
                    case 4:
                        cambiarDirectorio();
                        break;
                    case 5:
                        continuar = false;
                        break;
                    default:
                        System.out.println("Opción no válida");
                }
            }

            // Desconectar
            ftp.logout();
            ftp.disconnect();
            System.out.println("Desconectado del servidor FTP");

        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void conectar(String servidor, String usuario, String password)
            throws IOException {
        // Conectar
        ftp.connect(servidor);

        // Verificar conexión
        if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
            ftp.disconnect();
            System.out.println("No se pudo conectar al servidor");
            System.exit(1);
        }

        System.out.println("Conectado al servidor: " + servidor);

        // Login
        boolean loginExitoso = ftp.login(usuario, password);

        if (!loginExitoso) {
            System.err.println("Error de autenticación");
            ftp.disconnect();
            System.exit(1);
        }

        System.out.println("Autenticación exitosa");

        // Configuración para FTPS
        ftp.execPBSZ(0);
        ftp.execPROT("P"); // protección de datos
        ftp.enterLocalPassiveMode();

        System.out.println("Conexión establecida correctamente");
    }

    private static void listarDirectorio() {
        try {
            System.out.println("\n=== CONTENIDO DEL DIRECTORIO ===");
            FTPFile[] archivos = ftp.listFiles();

            if (archivos.length == 0) {
                System.out.println("El directorio está vacío");
            } else {
                for (FTPFile archivo : archivos) {
                    String tipo = archivo.isDirectory() ? "[DIR]" : "[FILE]";
                    System.out.println(tipo + " " + archivo.getName());
                }
            }

            System.out.println("Total de elementos: " + archivos.length);

        } catch (IOException e) {
            System.err.println("Error al listar directorio: " + e.getMessage());
        }
    }

    private static void subirArchivo() {
        try {
            System.out.print("Ruta completa del archivo local a subir: ");
            String archivoLocal = sc.nextLine();

            System.out.print("Nombre del archivo en el servidor: ");
            String archivoRemoto = sc.nextLine();

            // Subir archivo
            try (FileInputStream fis = new FileInputStream(archivoLocal)) {
                boolean subido = ftp.storeFile(archivoRemoto, fis);

                if (subido) {
                    System.out.println("Archivo subido correctamente: " + archivoRemoto);
                } else {
                    System.err.println("Error al subir el archivo");
                }
            }

        } catch (IOException e) {
            System.err.println("Error al subir archivo: " + e.getMessage());
        }
    }

    private static void descargarArchivo() {
        try {
            // Listar archivos disponibles
            listarDirectorio();

            System.out.print("\nNombre del archivo a descargar: ");
            String archivoRemoto = sc.nextLine();

            System.out.print("Ruta completa donde guardar el archivo: ");
            String archivoLocal = sc.nextLine();

            // Descargar archivo
            try (FileOutputStream fos = new FileOutputStream(archivoLocal)) {
                boolean descargado = ftp.retrieveFile(archivoRemoto, fos);

                if (descargado) {
                    System.out.println("Archivo descargado correctamente: " + archivoLocal);
                } else {
                    System.err.println("Error al descargar el archivo");
                }
            }

        } catch (IOException e) {
            System.err.println("Error al descargar archivo: " + e.getMessage());
        }
    }

    private static void cambiarDirectorio() {
        try {
            System.out.print("Nombre del directorio: ");
            String directorio = sc.nextLine();

            boolean cambiado = ftp.changeWorkingDirectory(directorio);

            if (cambiado) {
                System.out.println("Directorio cambiado a: " + directorio);
                listarDirectorio();
            } else {
                System.err.println("No se pudo cambiar al directorio especificado");
            }

        } catch (IOException e) {
            System.err.println("Error al cambiar directorio: " + e.getMessage());
        }
    }
}