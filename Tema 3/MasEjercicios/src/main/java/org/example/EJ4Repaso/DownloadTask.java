package org.example.EJ4Repaso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Tarea que descarga un archivo desde una URL.
 *
 * Flujo:
 *   1. Abrir conexión HTTP (HttpURLConnection)
 *   2. Comprobar código de respuesta (200 OK)
 *   3. Leer el contenido y guardarlo en un fichero local
 *   4. Calcular hash SHA-256 del fichero descargado
 *   5. Actualizar estadísticas (DownloadStats)
 *
 * Implementa Runnable para poder ejecutarse en un hilo del ExecutorService.
 */
public class DownloadTask implements Runnable {

    private final String urlString;   // URL de la descarga
    private final File outputFile;    // Fichero local donde guardar
    private final DownloadStats stats; // Referencia a estadísticas globales

    // Hash calculado del fichero (se puede usar luego para verificación)
    private String fileHash;

    public DownloadTask(String urlString, File outputFile, DownloadStats stats) {
        this.urlString = urlString;
        this.outputFile = outputFile;
        this.stats = stats;
    }

    @Override
    public void run() {
        System.out.println("Iniciando descarga: " + urlString +
                " -> " + outputFile.getName());

        long bytes = 0; // contador de bytes descargados para este fichero

        try {
            // 1. Crear objeto URL y abrir conexión
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Configuración básica de la conexión
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000); // 15 segundos para conectar
            conn.setReadTimeout(30000);    // 30 segundos para leer

            // 2. Comprobar código de estado HTTP
            int code = conn.getResponseCode();
            System.out.println("Respuesta HTTP " + code + " para " + urlString);
            if (code != HttpURLConnection.HTTP_OK) {
                System.err.println("No se puede descargar " + urlString +
                        ": HTTP " + code);
                return; // abortamos esta tarea
            }

            // 3. Descargar el contenido de la respuesta y guardarlo en disco
            try (BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
                 FileOutputStream fos = new FileOutputStream(outputFile)) {

                byte[] buffer = new byte[8192];
                int read;
                // Leemos bloques de 8 KB hasta que no haya más datos
                while ((read = in.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                    bytes += read; // sumamos bytes descargados
                }
            }

            // 4. Calcular el hash SHA-256 del fichero descargado
            fileHash = HashVerifier.calculateSHA256(outputFile);
            System.out.println("Hash SHA-256 (" + outputFile.getName() + "): " + fileHash);

            // 5. Actualizar estadísticas globales
            stats.addFile(bytes);

        } catch (IOException e) {
            System.err.println("Error descargando " + urlString + ": " + e.getMessage());
        }
    }

    /**
     * Devuelve el hash calculado para este archivo (Base64),
     * o null si hubo un error y no se pudo calcular.
     */
    public String getFileHash() {
        return fileHash;
    }
}
