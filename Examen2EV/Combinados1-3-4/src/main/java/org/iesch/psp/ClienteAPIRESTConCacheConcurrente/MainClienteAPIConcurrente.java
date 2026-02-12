package org.iesch.psp.ClienteAPIRESTConCacheConcurrente;// --- MainClienteAPIConcurrente.java ---
import java.net.http.HttpClient;
import java.util.*;
import java.util.concurrent.*;

public class MainClienteAPIConcurrente {

    public static void main(String[] args) {
        // URLs de ejemplo (puedes cambiar por endpoints reales)
        String[] urls = {
                "https://jsonplaceholder.typicode.com/posts/1",
                "https://jsonplaceholder.typicode.com/posts/2",
                "https://jsonplaceholder.typicode.com/posts/1", // Repetida para probar caché
                "https://jsonplaceholder.typicode.com/posts/3",
                "https://jsonplaceholder.typicode.com/posts/2", // Repetida
                "https://jsonplaceholder.typicode.com/posts/4",
                "https://jsonplaceholder.typicode.com/posts/1", // Repetida
                "https://jsonplaceholder.typicode.com/posts/5"
        };

        CacheRespuestas cache = new CacheRespuestas();
        HttpClient cliente = HttpClient.newHttpClient();

        // Crear tareas
        List<ConsultaAPI> tareas = new ArrayList<>();
        for (String url : urls) {
            tareas.add(new ConsultaAPI(url, cache, cliente));
        }

        // Ejecutar con ThreadPoolExecutor
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

        System.out.println("=== CONSULTAS CONCURRENTES A API REST ===\n");

        long inicio = System.currentTimeMillis();

        try {
            List<Future<String>> resultados = executor.invokeAll(tareas);

            System.out.println("\n=== RESULTADOS ===");
            for (int i = 0; i < resultados.size(); i++) {
                String resultado = resultados.get(i).get();
                // Mostrar solo los primeros 100 caracteres
                String resumen = resultado.length() > 100
                        ? resultado.substring(0, 100) + "..."
                        : resultado;
                System.out.println("URL " + (i + 1) + ": " + resumen);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        executor.shutdown();

        long fin = System.currentTimeMillis();

        // Estadísticas
        System.out.println("\n=== ESTADÍSTICAS DE CACHÉ ===");
        System.out.println("Cache Hits:  " + cache.getHits());
        System.out.println("Cache Misses: " + cache.getMisses());
        System.out.println("Tiempo total: " + (fin - inicio) + " ms");
    }
}