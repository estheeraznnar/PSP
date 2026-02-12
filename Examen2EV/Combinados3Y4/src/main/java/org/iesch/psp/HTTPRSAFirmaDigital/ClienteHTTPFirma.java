package org.iesch.psp.HTTPRSAFirmaDigital;// --- ClienteHTTPFirma.java ---
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class ClienteHTTPFirma {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String SERVER = "http://localhost:8080";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Generamos claves RSA
        System.out.println("Generando claves RSA...");
        RSAFirmaDigital.RSAKeys keys = RSAFirmaDigital.createRSAKeys(2048);
        System.out.println("Claves generadas.");

        try {
            // Registramos la clave pública en el servidor
            System.out.println("\nRegistrando clave pública en el servidor...");
            HttpRequest registerReq = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER + "/register-key"))
                    .header("Content-Type", "text/plain")
                    .POST(HttpRequest.BodyPublishers.ofString(keys.publicKey))
                    .build();

            HttpResponse<String> registerResp = client.send(registerReq, HttpResponse.BodyHandlers.ofString());
            System.out.println("Servidor responde: " + registerResp.statusCode());

            // Enviamos datos firmados
            System.out.println("\nIntroduce los datos a enviar al servidor:");
            String datos = sc.nextLine();

            // Firmamos con clave privada
            String firma = RSAFirmaDigital.sign(datos, keys.privateKey);
            System.out.println("Firma generada: " + firma);

            // Enviamos al servidor con la firma en la cabecera
            HttpRequest dataReq = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER + "/datos"))
                    .header("Content-Type", "text/plain")
                    .header("X-Signature", firma)
                    .POST(HttpRequest.BodyPublishers.ofString(datos))
                    .build();

            HttpResponse<String> dataResp = client.send(dataReq, HttpResponse.BodyHandlers.ofString());
            System.out.println("Código respuesta: " + dataResp.statusCode());
            System.out.println("Cuerpo respuesta: " + dataResp.body());

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}