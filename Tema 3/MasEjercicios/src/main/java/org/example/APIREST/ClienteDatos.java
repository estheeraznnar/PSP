package org.example.APIREST;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

//Obtiene datos de URLs
public class ClienteDatos {

    private static final HttpClient cli = HttpClient.newHttpClient();

    public static String getDatos(String uri) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    cli.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo datos de " + uri, e);
        }
    }
}
