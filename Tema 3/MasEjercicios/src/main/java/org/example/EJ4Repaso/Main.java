package org.example.EJ4Repaso;

/**EJERCICIO 4: Descargador de Archivos Concurrente con Hash Verification (3 puntos)
Contexto
Necesitamos crear un sistema que descargue múltiples archivos de URLs simultáneamente y verifique su integridad usando SHA-256.
Clases: FileDownloader, DownloadTask, HashVerifier, DownloadStats.
Tareas
1. Implementa la clase DownloadTask que:
Implemente Runnable
Descargue un archivo desde una URL usando HttpURLConnection
Guarde el archivo en disco
Calcule el hash SHA-256 del archivo descargado
Actualice las estadísticas en DownloadStats (contador thread-safe) (2,5 puntos)
2. Implementa la clase FileDownloader que:
Reciba una lista de URLs para descargar
Use un ExecutorService con 5 hilos
Lance un DownloadTask por cada URL
Espere a que todas las descargas terminen
Muestre el tiempo total transcurrido (2 puntos)
3. Implementa la clase HashVerifier que:
Tenga un metodo static String calculateSHA256(File file) que calcule el hash de un archivo
Tenga un metodo static boolean verifyHash(File file, String expectedHash) que verifique si el hash coincide (2 puntos)
4. Implementa la clase DownloadStats que:
Mantenga un contador thread-safe de archivos descargados
Mantenga un contador thread-safe de bytes totales descargados
Use AtomicInteger y AtomicLong para garantizar thread-safety
Tenga métodos para incrementar contadores y obtener estadísticas (1,5 puntos)
5. Crea una clase DownloadTest que descargue simultáneamente 10 archivos y verifique sus hashes. (2 puntos)
**/
public class Main {
}
