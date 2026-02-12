package org.example.APIREST;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

// Cliente principal de la API
/**
 * Cliente para consumir la API REST de AEMET (Agencia Estatal de Meteorología).
 * Gestiona las peticiones HTTP y la deserialización de respuestas JSON.
 */
public class ClienteAemet {

    // URL base de la API de datos abiertos de AEMET
    private static final String DIR = "https://opendata.aemet.es/opendata";

    // Prefijo del parámetro de autenticación en las URLs
    private static final String API_KEY_PARAM_PREFIX = "/?api_key=";

    // Cliente HTTP reutilizable para todas las peticiones
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    // Mapper JSON para convertir respuestas a objetos Java
    private static final ObjectMapper mapper = new ObjectMapper();

    // Clave API proporcionada por AEMET (debe asignarse antes de usar el cliente)
    public static String apiKey;

    /**
     * Construye el parámetro de API key para añadir a las URLs.
     *
     * @return String con el formato "/?api_key=XXXXX"
     * @throws IllegalStateException si la API key no ha sido asignada
     */
    private static String getApiKeyParam() {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("API key no asignada");
        }
        return API_KEY_PARAM_PREFIX + apiKey;
    }

    /**
     * Obtiene el inventario completo de estaciones meteorológicas de AEMET.
     * Realiza una petición HTTP, obtiene la URL de datos y descarga el JSON final.
     *
     * @return Lista de objetos Estacion con todas las estaciones disponibles
     * @throws RuntimeException si hay error en la petición HTTP, respuesta API o deserialización
     */
    public static List<Estacion> inventarioEstacionesTodas() {

        try {

            // Construir la URI completa del endpoint de inventario
            String uri = DIR +
                    "/api/valores/climatologicos/inventarioestaciones/todasestaciones"
                    + getApiKeyParam();

            // Crear petición GET con la URI construida
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .GET()
                    .build();

            // Ejecutar petición y obtener respuesta como String
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Validar código de estado HTTP (200 = OK)
            if (response.statusCode() != 200) {
                throw new RuntimeException("Error HTTP: " + response.statusCode());
            }

            // Deserializar la primera respuesta que contiene metadata y URL de datos
            Respuesta resp = mapper.readValue(response.body(), Respuesta.class);

            // Verificar que el estado de la API sea correcto (200 = OK en AEMET)
            if (resp.getEstado() != Respuesta.OK) {
                throw new RuntimeException("Error API: "
                        + resp.getEstado() + " - "
                        + resp.getDescripcion());
            }

            // Obtener los datos reales desde la URL proporcionada en la respuesta
            String datosJson = ClienteDatos.getDatos(resp.getDatos());

            // Deserializar el JSON de datos a una lista de objetos Estacion
            return mapper.readValue(datosJson,
                    new TypeReference<List<Estacion>>() {});

        } catch (Exception e) {
            // Encapsular cualquier excepción en RuntimeException con contexto
            throw new RuntimeException("Error obteniendo inventario", e);
        }
    }
}
