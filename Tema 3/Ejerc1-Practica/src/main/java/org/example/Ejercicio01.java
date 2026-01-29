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

/*FTP URL: ftp.dlptest.com or ftp://ftp.dlptest.com/
FTP User: dlpuser
Password: rNrKYTX9g7z3RgJRmxWuGHbeu


https://sftpcloud.io/tools/test-ftp-server
*/

public class Ejercicio01 {

    public static void main(String[] args) {
        // Instancia para gestionar la conexión FTP y el lector de consola
        FTPClient ftp = new FTPClient();
        Scanner sc = new Scanner(System.in);

        try {
            // --- BLOQUE DE AUTENTICACIÓN ---
            System.out.print("Servidor: ");
            String servidor = sc.nextLine();

            System.out.print("Usuario: ");
            String usuario = sc.nextLine();

            System.out.print("Contraseña: ");
            String password = sc.nextLine();

            // Establece conexión física con el servidor
            ftp.connect(servidor);
            // Realiza el login con las credenciales proporcionadas
            ftp.login(usuario, password);
            // Activa modo pasivo para evitar bloqueos de firewall/NAT en la transferencia de datos
            ftp.enterLocalPassiveMode();

            System.out.println("Conectado\n");

            // --- BUCLE DE OPERACIONES ---
            while (true) {
                System.out.println("1. Ver archivos");
                System.out.println("2. Subir archivo");
                System.out.println("3. Descargar archivo");
                System.out.println("4. Salir");
                System.out.print("Opción: ");

                // Conversión de entrada para evitar problemas con saltos de línea pendientes
                int opcion = Integer.parseInt(sc.nextLine());

                if (opcion == 1) {
                    // Obtiene la lista de archivos y directorios del servidor
                    FTPFile[] archivos = ftp.listFiles();
                    System.out.println("\n--- ARCHIVOS ---");
                    for (FTPFile archivo : archivos) {
                        System.out.println(archivo.getName());
                    }
                    System.out.println();

                } else if (opcion == 2) {
                    // --- SUBIDA DE ARCHIVOS ---
                    System.out.print("Ruta local del archivo a subir: ");
                    String ruta = sc.nextLine();

                    File archivo = new File(ruta);
                    // Crea un flujo de entrada para leer el archivo local
                    FileInputStream fis = new FileInputStream(archivo);

                    // Almacena el archivo en el servidor con su nombre original
                    if (ftp.storeFile(archivo.getName(), fis)) {
                        System.out.println("Subido correctamente\n");
                    }
                    fis.close(); // Siempre cerrar el stream

                } else if (opcion == 3) {
                    // --- DESCARGA DE ARCHIVOS ---
                    System.out.print("Nombre del archivo en el servidor a descargar: ");
                    String nombre = sc.nextLine();

                    // Crea un flujo de salida para escribir el archivo en el disco local
                    FileOutputStream fos = new FileOutputStream(nombre);
                    // Recupera el archivo del servidor y lo vuelca al flujo local
                    if (ftp.retrieveFile(nombre, fos)) {
                        System.out.println("Descargado correctamente\n");
                    }
                    fos.close(); // Siempre cerrar el stream

                } else if (opcion == 4) {
                    // Rompe el bucle para proceder al cierre de sesión
                    break;
                }
            }

            // --- FINALIZACIÓN ---
            ftp.logout(); // Cierra la sesión del usuario
            ftp.disconnect(); // Corta la conexión con el servidor
            System.out.println("Desconectado");

        } catch (Exception e) {
            // Captura errores de conexión, autenticación o IO
            System.out.println("Error: " + e.getMessage());
        }

        sc.close(); // Cierra el scanner de sistema
    }

}

