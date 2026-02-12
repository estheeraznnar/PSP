// --- ServidorTCPDeFicherosConPoolDeHilosYHash/FileServerThread.java ---
package org.iesch.psp.ServidorTCPDeFicherosConPoolDeHilosYHash;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Tarea Callable que atiende a un cliente
 * Usa Callable en lugar de Runnable para poder devolver resultado al pool
 * Combina conceptos del Tema 1 (Callable/ThreadPoolExecutor) y Tema 2 (Sockets TCP)
 */
public class FileServerThread implements Callable<String> {

    // Socket del cliente
    private Socket socket;

    // Directorio de ficheros del servidor
    private File directorio;

    /**
     * Constructor
     * @param socket Socket del cliente conectado
     * @param directorio Directorio donde están los ficheros
     */
    public FileServerThread(Socket socket, File directorio) {
        this.socket = socket;
        this.directorio = directorio;
    }

    @Override
    public String call() throws Exception {
        String clienteInfo = socket.getInetAddress() + ":" + socket.getPort();
        int operaciones = 0;

        System.out.println("[" + Thread.currentThread().getName()
                + "] Cliente conectado: " + clienteInfo);

        // Usar try-with-resources para los streams
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            boolean continuar = true;

            while (continuar) {
                // Leer petición del cliente
                FileRequest req = (FileRequest) in.readObject();

                // Procesar según el tipo de petición
                FileResponse resp = procesarPeticion(req);

                // Enviar respuesta
                out.reset();
                out.writeObject(resp);

                operaciones++;

                // Verificar si hay que terminar
                if (req.getType() == FileRequest.RequestType.QUIT) {
                    continuar = false;
                }
            }

        } catch (IOException e) {
            System.out.println("[" + Thread.currentThread().getName()
                    + "] Cliente desconectado: " + clienteInfo);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Cerrar socket
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Devolver resumen (para el ThreadPoolExecutor)
        return "Cliente " + clienteInfo + " - " + operaciones + " operaciones";
    }

    /**
     * Procesa una petición del cliente
     */
    private FileResponse procesarPeticion(FileRequest req) {
        switch (req.getType()) {

            case LIST:
                // Listar ficheros del directorio
                return procesarList(req);

            case DOWNLOAD:
                // Descargar fichero
                return procesarDownload(req);

            case QUIT:
                // Cerrar conexión
                System.out.println("[" + Thread.currentThread().getName()
                        + "] Cliente solicita desconexión");
                return new FileResponse(req, true, "Desconectado");

            default:
                return new FileResponse(req, false, "Operación no reconocida");
        }
    }

    /**
     * Procesa petición de listar ficheros
     */
    private FileResponse procesarList(FileRequest req) {
        // Obtener ficheros del directorio
        File[] ficheros = directorio.listFiles(File::isFile);

        if (ficheros == null || ficheros.length == 0) {
            return new FileResponse(req, true, "No hay ficheros disponibles", new String[0]);
        }

        // Crear lista de nombres
        List<String> nombres = new ArrayList<>();
        for (File f : ficheros) {
            nombres.add(f.getName() + " (" + f.length() + " bytes)");
        }

        System.out.println("[" + Thread.currentThread().getName()
                + "] LIST: " + nombres.size() + " ficheros");

        return new FileResponse(req, true, "Ficheros disponibles: " + nombres.size(),
                nombres.toArray(new String[0]));
    }

    /**
     * Procesa petición de descargar fichero
     */
    private FileResponse procesarDownload(FileRequest req) {
        String fileName = req.getFileName();
        File fichero = new File(directorio, fileName);

        // Verificar que el fichero existe
        if (!fichero.exists() || !fichero.isFile()) {
            return new FileResponse(req, false, "Fichero no encontrado: " + fileName);
        }

        try {
            // Leer contenido del fichero
            byte[] data = Files.readAllBytes(fichero.toPath());

            // Calcular hash SHA-256 para verificar integridad
            String hash = HashUtil.getHash(data);

            System.out.println("[" + Thread.currentThread().getName()
                    + "] DOWNLOAD: " + fileName + " (" + data.length + " bytes)");
            System.out.println("[" + Thread.currentThread().getName()
                    + "] Hash: " + hash);

            return new FileResponse(req, true, "Fichero enviado", data, hash);

        } catch (IOException e) {
            return new FileResponse(req, false, "Error al leer fichero: " + e.getMessage());
        }
    }
}