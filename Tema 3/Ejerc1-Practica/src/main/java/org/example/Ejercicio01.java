package org.example;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import java.io.*;
import java.util.Scanner;

/**Ejercicio 1: Cliente FTP básico para gestión de archivos
 Desarrolla una aplicación cliente que se conecte a un servidor FTP utilizando los
 protocolos estándar. El programa deberá permitir al usuario realizar operaciones
 básicas de gestión de archivos remotos.
 Requisitos:
 • Conectarse a un servidor FTP indicando:
 • Dirección del servidor
 • Usuario
 • Contraseña
 • Mostrar el contenido del directorio actual del servidor.
 • Permitir:
 • Subir un archivo al servidor.
 • Descargar un archivo del servidor.
 • Gestionar correctamente errores de conexión y autenticación.

 • Mostrar por consola los mensajes de estado de la comunicación cliente-
 servidor.
 */

public class Ejercicio01 {

    public static void main(String[] args) {

        FTPClient ftp = new FTPClient();
        Scanner sc = new Scanner(System.in);

        try {
            // Pedir datos
            System.out.print("Servidor: ");
            String servidor = sc.nextLine();

            System.out.print("Usuario: ");
            String usuario = sc.nextLine();

            System.out.print("Contraseña: ");
            String password = sc.nextLine();

            // Conectar
            ftp.connect(servidor);
            ftp.login(usuario, password);
            ftp.enterLocalPassiveMode();

            System.out.println("Conectado\n");

            // Menú
            while (true) {
                System.out.println("1. Ver archivos");
                System.out.println("2. Subir archivo");
                System.out.println("3. Descargar archivo");
                System.out.println("4. Salir");
                System.out.print("Opción: ");

                int opcion = Integer.parseInt(sc.nextLine());

                if (opcion == 1) {
                    // Ver archivos
                    FTPFile[] archivos = ftp.listFiles();
                    System.out.println("\n--- ARCHIVOS ---");
                    for (FTPFile archivo : archivos) {
                        System.out.println(archivo.getName());
                    }
                    System.out.println();

                } else if (opcion == 2) {
                    // Subir
                    System.out.print("Archivo a subir: ");
                    String ruta = sc.nextLine();

                    File archivo = new File(ruta);
                    FileInputStream fis = new FileInputStream(archivo);

                    ftp.storeFile(archivo.getName(), fis);
                    fis.close();

                    System.out.println("Subido\n");

                } else if (opcion == 3) {
                    // Descargar
                    System.out.print("Archivo a descargar: ");
                    String nombre = sc.nextLine();

                    FileOutputStream fos = new FileOutputStream(nombre);
                    ftp.retrieveFile(nombre, fos);
                    fos.close();

                    System.out.println("Descargado\n");

                } else if (opcion == 4) {
                    // Salir
                    break;
                }
            }

            // Desconectar
            ftp.logout();
            ftp.disconnect();
            System.out.println("Desconectado");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        sc.close();
    }
}

