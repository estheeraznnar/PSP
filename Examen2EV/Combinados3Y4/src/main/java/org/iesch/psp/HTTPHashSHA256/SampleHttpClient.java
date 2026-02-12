package org.iesch.psp.HTTPHashSHA256;// --- SampleHttpClient.java ---
import javax.net.ssl.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class SampleHttpClient {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final HttpClient trustedClient;
    public static final List<String> TrustedSites;

    static {
        TrustedSites = new ArrayList<>();
        trustedClient = createTrustedHttpClient();
    }

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

    public static String get(String address) {
        try {
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
}