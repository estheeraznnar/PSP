package org.iesch.psp.ClienteAPIRESTConCacheConcurrente;// --- ConsultaAPI.java (Tarea Callable para consultar API) ---
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Callable;

public class ConsultaAPI implements Callable<String> {

    private String url;
    private CacheRespuestas cache;
    private HttpClient cliente;

    public ConsultaAPI(String url, CacheRespuestas cache, HttpClient cliente) {
        this.url = url;
        this.cache = cache;
        this.cliente = cliente;
    }

    @Override
    public String call() throws Exception {
        // Primero intentar obtener de caché
        String datosCache = cache.obtener(url);
        if (datosCache != null) {
            return datosCache;
        }

        // Si no está en caché, hacer petición HTTP
        System.out.println("[" + Thread.currentThread().getName() + "] Consultando: " + url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String datos = response.body();
            // Guardar en caché
            cache.guardar(url, datos);
            return datos;
        } else {
            return "ERROR: " + response.statusCode();
        }
    }
}