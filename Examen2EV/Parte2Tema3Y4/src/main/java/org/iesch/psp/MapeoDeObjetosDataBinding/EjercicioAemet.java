package org.iesch.psp.MapeoDeObjetosDataBinding;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class EjercicioAemet {

    // Instancia estática de ObjectMapper (es pesado de crear, mejor reutilizarlo)
    private static final ObjectMapper mapper = new ObjectMapper();

    // Cliente HTTP estándar
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static void main(String[] args) {
        // URL ficticia para el ejercicio
        String urlApi = "https://opendata.aemet.es/opendata/api/valores/climatologicos/inventarioestaciones/todasestaciones/?api_key=TU_CLAVE_AQUI";

        try {
            System.out.println("1. Preparando petición a: " + urlApi);

            // --- PASO 1: Construir la petición HTTP ---
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlApi))
                    .header("cache-control", "no-cache") // Cabecera usada en el PDF
                    .GET()
                    .build();

            // --- PASO 2: Enviar petición y obtener respuesta como String ---
            // Nota: En un examen real sin internet, aquí saltaría excepción.
            // HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // --- SIMULACIÓN PARA EL EJERCICIO ---
            // Imaginemos que 'response.body()' devolvió este JSON exacto (formato AEMET):
            String jsonSimulado = "{\n" +
                    "  \"descripcion\": \"exito\",\n" +
                    "  \"estado\": 200,\n" +
                    "  \"datos\": \"https://opendata.aemet.es/datos/12345.json\",\n" +
                    "  \"metadatos\": \"https://opendata.aemet.es/meta/12345.json\"\n" +
                    "}";

            System.out.println("JSON Recibido (Simulado): \n" + jsonSimulado);

            // --- PASO 3: Deserialización (JSON -> Objeto Java) ---
            // ESTA ES LA LÍNEA CLAVE DEL EXAMEN [cite: 950]
            Respuesta resp = mapper.readValue(jsonSimulado, Respuesta.class);

            // --- PASO 4: Validación de Lógica de Negocio ---
            // El PDF insiste en verificar el estado interno de la respuesta JSON
            if (resp.getEstado() == Respuesta.OK) {
                System.out.println("\n--- Mapeo Exitoso ---");
                System.out.println("Estado API: " + resp.getEstado());
                System.out.println("URL para descargar datos: " + resp.getDatos());

                // Aquí, según el PDF, llamaríamos a ClienteDatos.getDatos(resp.getDatos())
                // para hacer la segunda petición y bajar los datos reales.
            } else {
                throw new RuntimeException("Error API: " + resp.getEstado() + " - " + resp.getDescripcion());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}