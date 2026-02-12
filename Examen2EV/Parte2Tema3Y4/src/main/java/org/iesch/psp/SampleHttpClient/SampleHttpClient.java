package org.iesch.psp.SampleHttpClient;

import javax.net.ssl.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SampleHttpClient {

    /**
     * HttpClient general (valida certificados) [cite: 200]
     */
    private static final HttpClient client = HttpClient.newHttpClient();

    /**
     * HttpClient para sitios de confianza (no valida certificados) [cite: 204]
     */
    private static final HttpClient trustedClient;

    /**
     * Listado de sitios de confianza [cite: 208]
     */
    public static final List<String> TrustedSites;

    /**
     * Inicialización estática [cite: 212]
     */
    static {
        TrustedSites = new ArrayList<>();
        trustedClient = createTrustedHttpClient();
    }

    /**
     * Crea un HttpClient que acepta cualquier certificado SSL [cite: 220]
     */
    private static HttpClient createTrustedHttpClient() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            return HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error creando HttpClient de confianza", e);
        }
    }

    /**
     * Obtiene el contenido de una dirección HTTP [cite: 240]
     */
    public static String get(String address) {
        try {
            // Decide qué cliente usar basándose en si la URL está en TrustedSites [cite: 244]
            HttpClient cli = TrustedSites.contains(address)
                    ? trustedClient
                    : client;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(address))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    cli.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException("Error realizando la petición HTTP", e);
        }
    }

    // --- MAIN PARA PROBAR EL EJERCICIO (Basado en pág 11) ---
    public static void main(String[] args) {
        // 1. Añadimos el sitio a la lista de confianza como pide el ejercicio [cite: 263]
        String urlObjetivo = "https://iesch.org";
        SampleHttpClient.TrustedSites.add(urlObjetivo);

        System.out.println("Realizando petición a: " + urlObjetivo);

        // 2. Llamada al HttpClient [cite: 269]
        String resultado = SampleHttpClient.get(urlObjetivo);

        // 3. Imprimir resultado
        System.out.println("Cuerpo de la respuesta:");
        System.out.println(resultado);
    }
}