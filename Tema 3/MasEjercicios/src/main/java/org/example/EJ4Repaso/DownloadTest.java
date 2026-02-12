package org.example.EJ4Repaso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de prueba que:
 *  - Define 10 URLs.
 *  - Usa FileDownloader para descargarlas en paralelo.
 *  - Verifica los hashes de los archivos descargados contra los propios
 *    hashes calculados por cada DownloadTask (simulación de verificación).
 */
public class DownloadTest {

    public static void main(String[] args) {
        // Lista de URLs de ejemplo (deberías usar URLs reales accesibles)
        List<String> urls = new ArrayList<>();
        urls.add("https://www.rfc-editor.org/rfc/rfc2616.txt");
        urls.add("https://www.rfc-editor.org/rfc/rfc7230.txt");
        urls.add("https://www.rfc-editor.org/rfc/rfc7540.txt");
        urls.add("https://www.rfc-editor.org/rfc/rfc959.txt");
        urls.add("https://www.rfc-editor.org/rfc/rfc791.txt");
        urls.add("https://www.rfc-editor.org/rfc/rfc793.txt");
        urls.add("https://www.rfc-editor.org/rfc/rfc5321.txt");
        urls.add("https://www.rfc-editor.org/rfc/rfc5322.txt");
        urls.add("https://www.rfc-editor.org/rfc/rfc1035.txt");
        urls.add("https://www.rfc-editor.org/rfc/rfc1918.txt");

        // Crear el gestor de descargas
        FileDownloader downloader = new FileDownloader();

        // Ejecutar todas las descargas
        downloader.downloadAll(urls);

        // Verificación de hashes
        System.out.println("\n=== VERIFICACIÓN DE HASHES ===");

        int index = 1;
        for (DownloadTask task : downloader.getTasks()) {
            // Nombre local generado (misma lógica que en FileDownloader)
            String ext = ".txt"; // asumimos .txt por las URLs de RFC
            File file = new File("file_" + index + ext);
            if (!file.exists()) {
                // Si no existe con .txt, probamos con .dat
                file = new File("file_" + index + ".dat");
            }

            if (!file.exists()) {
                System.out.println("No se encontró el archivo para la tarea " + index);
                index++;
                continue;
            }

            // Hash esperado: el calculado por la propia tarea (simulación)
            String expectedHash = task.getFileHash();
            if (expectedHash == null) {
                System.out.println("Tarea " + index + " no calculó hash (posible fallo de descarga)");
                index++;
                continue;
            }

            // Verificar el hash del fichero
            boolean ok = HashVerifier.verifyHash(file, expectedHash);
            System.out.println("Archivo " + file.getName() + ": " +
                    (ok ? "HASH OK" : "HASH INCORRECTO"));

            index++;
        }
    }
}
