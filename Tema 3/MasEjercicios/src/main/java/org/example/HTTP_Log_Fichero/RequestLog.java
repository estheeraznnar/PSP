package org.example.HTTP_Log_Fichero;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Clase encargada de registrar peticiones en el fichero requests.log
 * Formato de cada línea:
 * YYYY-MM-DD HH:MM:SS - IP_CLIENTE - msg=...
 */
public class RequestLog {

    private static final String LOG_FILE = "requests.log";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Registra una nueva petición en el log.
     *
     * @param clientIp IP del cliente
     * @param msg      mensaje recibido (valor del parámetro msg)
     */
    public static void logRequest(String clientIp, String msg) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String line = timestamp + " - " + clientIp + " - msg=" + msg;

        // Escribimos en modo append
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error escribiendo en el log: " + e.getMessage());
        }
    }

    /**
     * Devuelve el número total de peticiones registradas en el log.
     * Se cuenta simplemente el número de líneas del fichero.
     */
    public static int getTotalRequests() {
        try {
            Path path = Path.of(LOG_FILE);
            if (!Files.exists(path)) {
                return 0;
            }
            return (int) Files.lines(path).count();
        } catch (IOException e) {
            System.err.println("Error leyendo el log: " + e.getMessage());
            return 0;
        }
    }
}
