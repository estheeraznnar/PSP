// --- ServidorTCPDeFicherosConPoolDeHilosYHash/FileClient.java ---
package org.iesch.psp.ServidorTCPDeFicherosConPoolDeHilosYHash;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Cliente de ficheros TCP
 * Verifica integridad de descargas con hash SHA-256
 */
public class FileClient {

    public static void main(String[] args) {
        // Obtener parámetros
        String host;
        int port;

        try {
            host = args[0];
            port = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.out.println("Uso: java FileClient <host> <puerto>");
            return;
        }

        // Conectar al servidor
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             Scanner sc = new Scanner(System.in)) {

            System.out.println("Conectado a " + socket.getInetAddress() + ":" + socket.getPort());

            boolean continuar = true;

            while (continuar) {
                // Mostrar menú
                System.out.println("\n--- MENÚ ---");
                System.out.println("1. Listar ficheros");
                System.out.println("2. Descargar fichero");
                System.out.println("0. Salir");
                System.out.print("Opción: ");

                String opcion = sc.nextLine().trim();
                FileRequest req = null;

                switch (opcion) {
                    case "1":
                        req = new FileRequest(FileRequest.RequestType.LIST);
                        break;

                    case "2":
                        System.out.print("Nombre del fichero: ");
                        String nombre = sc.nextLine().trim();
                        req = new FileRequest(FileRequest.RequestType.DOWNLOAD, nombre);
                        break;

                    case "0":
                        req = new FileRequest(FileRequest.RequestType.QUIT);
                        continuar = false;
                        break;

                    default:
                        System.out.println("Opción no válida");
                        continue;
                }

                // Enviar petición
                out.reset();
                out.writeObject(req);

                // Recibir respuesta
                FileResponse resp = (FileResponse) in.readObject();

                // Procesar respuesta
                procesarRespuesta(resp, sc);
            }

            System.out.println("\nDesconectado.");

        } catch (IOException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Error: clase no encontrada");
        }
    }

    /**
     * Procesa la respuesta del servidor
     */
    private static void procesarRespuesta(FileResponse resp, Scanner sc) {
        if (!resp.isOk()) {
            System.out.println("✗ Error: " + resp.getMensaje());
            return;
        }

        switch (resp.getRequest().getType()) {
            case LIST:
                // Mostrar lista de ficheros
                System.out.println("\n" + resp.getMensaje());
                String[] files = resp.getFileList();
                if (files != null && files.length > 0) {
                    for (int i = 0; i < files.length; i++) {
                        System.out.println("  " + (i + 1) + ". " + files[i]);
                    }
                }
                break;

            case DOWNLOAD:
                // Guardar fichero y verificar hash
                byte[] data = resp.getFileData();
                String hashRecibido = resp.getFileHash();

                System.out.println("\n✓ Fichero recibido: " + data.length + " bytes");
                System.out.println("Hash recibido: " + hashRecibido);

                // Verificar integridad con hash
                if (HashUtil.checkHash(data, hashRecibido)) {
                    System.out.println("✓ Verificación de integridad: OK");

                    // Preguntar si guardar
                    System.out.print("¿Guardar fichero? (s/n): ");
                    if (sc.nextLine().trim().equalsIgnoreCase("s")) {
                        System.out.print("Nombre para guardar: ");
                        String nombreLocal = sc.nextLine().trim();

                        try {
                            Files.write(Paths.get(nombreLocal), data);
                            System.out.println("✓ Fichero guardado como: " + nombreLocal);
                        } catch (IOException e) {
                            System.out.println("✗ Error al guardar: " + e.getMessage());
                        }
                    }
                } else {
                    System.out.println("✗ ERROR: El hash no coincide - fichero corrupto");
                }
                break;

            case QUIT:
                System.out.println("✓ " + resp.getMensaje());
                break;
        }
    }
}