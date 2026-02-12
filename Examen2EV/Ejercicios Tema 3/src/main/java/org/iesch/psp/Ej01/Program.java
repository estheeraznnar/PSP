package org.iesch.psp.Ej01;

import org.apache.commons.net.ftp.FTP; // IMPORTANTE: Para el modo binario
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Scanner;

public class Program {

    public static void main(String[] args) {
        // Usamos TLS explícito
        FTPSClient ftp = new FTPSClient("TLS", false);
        Scanner sc = new Scanner(System.in);

        try {
            // --- 1. GESTIÓN DE CERTIFICADOS (TRUST MANAGER) ---
            ftp.setTrustManager(new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) {}
                public void checkServerTrusted(X509Certificate[] xcs, String string) {}
                public X509Certificate[] getAcceptedIssuers() { return null; }
            });

            // --- 2. CONEXIÓN ---
            System.out.println("Introduce la dirección del servidor (ej: ftp.dlptest.com):");
            String servidor = sc.nextLine().trim();

            System.out.println("Introduce el usuario (ej: dlpuser):");
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

            // Login
            if (!ftp.login(user, psw)) {
                System.out.println("Error de autenticación.");
                ftp.disconnect();
                return;
            }
            System.out.println("Login correcto.");

            // --- 3. CONFIGURACIÓN DE SEGURIDAD Y MODOS ---
            ftp.execPBSZ(0);
            ftp.execPROT("C"); // Si falla con timeouts, cambia "P" por "C"
            ftp.enterLocalPassiveMode();

            // ¡CRUCIAL! Sin esto, los PDF e imágenes se descargan corruptos
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            // --- 4. BUCLE DEL MENÚ ---
            boolean salir = false;
            while (!salir) {
                System.out.println("\n---------------------------");
                System.out.println("   --- MENÚ FTP ---");
                System.out.println("1. Listar contenido (ls)");
                System.out.println("2. Cambiar directorio (cd)");
                System.out.println("3. Descargar archivo (get)");
                System.out.println("4. Subir archivo (put)");
                System.out.println("---------------------------");
                System.out.println("5. Crear carpeta (mkdir)");
                System.out.println("6. Eliminar archivo (rm)");
                System.out.println("7. Eliminar carpeta (rmdir)");
                System.out.println("8. Renombrar/Mover (mv)");
                System.out.println("---------------------------");
                System.out.println("0. Salir");
                System.out.print("\nElige una opción: ");

                String opcion = sc.nextLine().trim();

                switch (opcion) {
                    case "1":
                        System.out.println("--- Listado de: " + ftp.printWorkingDirectory() + " ---");
                        // Refrescamos modo pasivo por si acaso
                        ftp.enterLocalPassiveMode();
                        listDirectory(ftp);
                        break;

                    case "2":
                        System.out.println("--- CAMBIAR DE DIRECTORIO ---");
                        System.out.println("Carpetas disponibles:");

                        // 1. Mostramos las opciones
                        mostrarSoloCarpetas(ftp);

                        // 2. Preguntamos
                        System.out.println("\n¿A qué directorio quieres moverte? (Escribe '..' para subir un nivel)");
                        String path = sc.nextLine().trim();

                        if (!path.isEmpty()) {
                            // 3. Intentamos cambiar
                            if (ftp.changeWorkingDirectory(path)) {
                                // Si funciona, mostramos dónde estamos ahora
                                System.out.println(">> ÉXITO. Directorio actual: " + ftp.printWorkingDirectory());

                                // Opcional: Listar el contenido del nuevo directorio automáticamente
                                System.out.println("\nContenido de la nueva carpeta:");
                                listDirectory(ftp);
                            } else {
                                System.out.println(">> ERROR: No se pudo entrar en '" + path + "'. (¿Existe?)");
                            }
                        }
                        break;

                    case "3":
                        System.out.println("¿Qué fichero quieres descargar? (Nombre en el servidor)");
                        String fileDown = sc.nextLine().trim();

                        System.out.println("¿Ruta local COMPLETA donde guardar? (ej: C:\\Downloads\\miarchivo.pdf)");
                        // CORRECCIÓN: Limpiamos comillas y pedimos ruta completa para evitar errores de concatenación
                        String destino = sc.nextLine().replace("\"", "").trim();

                        if (!fileDown.isEmpty() && !destino.isEmpty()) {
                            try (FileOutputStream fos = new FileOutputStream(destino)) {
                                ftp.enterLocalPassiveMode(); // Refresco para evitar timeout
                                if (ftp.retrieveFile(fileDown, fos)) {
                                    System.out.println(">> ÉXITO: Fichero descargado.");
                                } else {
                                    System.out.println(">> ERROR: No se pudo descargar.");
                                }
                            } catch (IOException e) {
                                System.out.println("Error local (ruta mal escrita): " + e.getMessage());
                            }
                        }
                        break;

                    case "4":
                        System.out.println("¿Ruta local del fichero a subir?");
                        // CORRECCIÓN: .replace("\"", "") para evitar el error de sintaxis de Windows
                        String fileUp = sc.nextLine().replace("\"", "").trim();

                        System.out.println("¿Nombre con el que se guardará en el servidor?");
                        String nombreRemoto = sc.nextLine().trim();

                        if (!fileUp.isEmpty() && !nombreRemoto.isEmpty()) {
                            try (FileInputStream fis = new FileInputStream(fileUp)) {
                                ftp.enterLocalPassiveMode(); // Refresco para evitar timeout
                                if (ftp.storeFile(nombreRemoto, fis)) {
                                    System.out.println(">> ÉXITO: Fichero subido.");
                                } else {
                                    System.out.println(">> ERROR: No se pudo subir (¿Permisos? ¿Nombre inválido?).");
                                }
                            } catch (IOException e) {
                                System.out.println("Error al leer fichero local: " + e.getMessage());
                            }
                        }
                        break;

                    case "5": // Asumiendo que sea la opción 6
                        System.out.println("--- CREAR NUEVA CARPETA ---");
                        System.out.print("Nombre de la nueva carpeta: ");
                        String nuevaCarpeta = sc.nextLine().trim();

                        if (!nuevaCarpeta.isEmpty()) {
                            // Refrescamos modo pasivo por seguridad
                            ftp.enterLocalPassiveMode();
                            if (ftp.makeDirectory(nuevaCarpeta)) {
                                System.out.println(">> ÉXITO: Carpeta '" + nuevaCarpeta + "' creada.");
                            } else {
                                // El error 550 suele ser "Permiso denegado" o "La carpeta ya existe"
                                System.out.println(">> ERROR: No se pudo crear (¿Ya existe? ¿Permisos?).");
                            }
                        }
                        break;
                    case "6":
                        System.out.println("--- ELIMINAR ARCHIVO ---");
                        System.out.print("Nombre del archivo a borrar: ");
                        String archivoBorrar = sc.nextLine().trim();

                        if (!archivoBorrar.isEmpty()) {
                            System.out.print("¿Estás SEGURO? Esto no se puede deshacer (S/N): ");
                            String confirmacion = sc.nextLine().trim();

                            if (confirmacion.equalsIgnoreCase("S")) {
                                ftp.enterLocalPassiveMode();
                                if (ftp.deleteFile(archivoBorrar)) {
                                    System.out.println(">> ÉXITO: Archivo eliminado.");
                                } else {
                                    System.out.println(">> ERROR: No se pudo borrar (¿Existe? ¿Permisos?).");
                                }
                            }
                        }
                        break;
                    case "7":
                        System.out.println("--- ELIMINAR CARPETA ---");
                        System.out.println("NOTA: La carpeta debe estar vacía para poder borrarse.");
                        System.out.print("Nombre de la carpeta a borrar: ");
                        String carpetaBorrar = sc.nextLine().trim();

                        if (!carpetaBorrar.isEmpty()) {
                            ftp.enterLocalPassiveMode();
                            if (ftp.removeDirectory(carpetaBorrar)) {
                                System.out.println(">> ÉXITO: Carpeta eliminada.");
                            } else {
                                System.out.println(">> ERROR: No se pudo borrar (¿Tiene archivos dentro?).");
                            }
                        }
                        break;
                    case "8":
                        System.out.println("--- RENOMBRAR / MOVER ---");
                        System.out.print("Nombre actual del archivo/carpeta: ");
                        String nombreViejo = sc.nextLine().trim();

                        System.out.print("Nuevo nombre (o ruta completa para mover): ");
                        String nombreNuevo = sc.nextLine().trim();

                        if (!nombreViejo.isEmpty() && !nombreNuevo.isEmpty()) {
                            ftp.enterLocalPassiveMode();
                            if (ftp.rename(nombreViejo, nombreNuevo)) {
                                System.out.println(">> ÉXITO: Renombrado/Movido correctamente.");
                            } else {
                                System.out.println(">> ERROR: No se pudo renombrar.");
                            }
                        }
                        break;
                    case "9":
                        salir = true;
                        break;

                    default:
                        System.out.println("Opción no válida");
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

    private static void listDirectory(FTPSClient ftp) throws IOException {
        FTPFile[] files = ftp.listFiles();
        if (files != null && files.length > 0) {
            for (FTPFile file : files) {
                System.out.println("- " + file.getName() + " (" + file.getSize() + " bytes)");
            }
        } else {
            System.out.println("(Directorio vacío)");
        }
    }

    // Método auxiliar para mostrar SOLO las carpetas
    private static void mostrarSoloCarpetas(FTPSClient ftp) throws IOException {
        // IMPORTANTE: Refrescamos el modo pasivo para evitar el timeout "getsockopt"
        ftp.enterLocalPassiveMode();

        FTPFile[] files = ftp.listFiles();
        boolean hayCarpetas = false;

        if (files != null && files.length > 0) {
            for (FTPFile file : files) {
                if (file.isDirectory()) {
                    // Imprimimos solo si es carpeta
                    System.out.println(" > [DIR] " + file.getName());
                    hayCarpetas = true;
                }
            }
        }

        if (!hayCarpetas) {
            System.out.println("(No hay subdirectorios en esta carpeta)");
        }
    }
}