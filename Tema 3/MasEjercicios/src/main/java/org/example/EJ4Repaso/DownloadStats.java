package org.example.EJ4Repaso;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Clase que mantiene estadísticas de descarga de forma segura
 * en entornos con múltiples hilos (thread-safe).
 *
 * - Número de archivos descargados.
 * - Número total de bytes descargados.
 *
 * Se usan AtomicInteger y AtomicLong para garantizar operaciones atómicas.
 */
public class DownloadStats {

    // Contador de archivos descargados correctamente
    private final AtomicInteger filesDownloaded = new AtomicInteger(0);

    // Contador de bytes totales descargados
    private final AtomicLong bytesDownloaded = new AtomicLong(0);

    /**
     * Se llama cuando un archivo se ha descargado correctamente.
     *
     * @param bytes Número de bytes descargados para ese archivo.
     */
    public void addFile(long bytes) {
        filesDownloaded.incrementAndGet(); // suma 1 de forma atómica
        bytesDownloaded.addAndGet(bytes);  // suma los bytes de forma atómica
    }

    public int getFilesDownloaded() {
        return filesDownloaded.get();
    }

    public long getBytesDownloaded() {
        return bytesDownloaded.get();
    }

    @Override
    public String toString() {
        return "Archivos descargados: " + getFilesDownloaded() +
                ", Bytes descargados: " + getBytesDownloaded();
    }
}
