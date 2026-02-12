package org.example;
// ============================================
// 1. Clase ClienteAEMET.java - Servicio REST
// ============================================

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Cliente para consumir la API REST de AEMET OpenData
 * Basado en la teoría de Web APIs y protocolo HTTP
 *
 * La API de AEMET devuelve respuestas en formato JSON con estructura:
 * {
 *   "descripcion": "exito",
 *   "estado": 200,
 *   "datos": "url_con_datos",
 *   "metadatos": "url_con_metadatos"
 * }
 */
public class ClienteAEMET {

    // URL base de la API de AEMET
    private static final String BASE_URL = "https://opendata.aemet.es/opendata/api";

    // Tu API Key (obtenerla en: https://opendata.aemet.es/)
    private static String API_KEY = "TU_API_KEY_AQUI";

    // Cliente Gson para parsear JSON
    private static final Gson gson = new Gson();

    /**
     * Establece la API Key para las peticiones
     */
    public static void setApiKey(String key) {
        API_KEY = key;
    }

    /**
     * Realiza una petición HTTP GET a la API
     * Según teoría: "El método GET solicita una representación del recurso especificado"
     *
     * @param endpoint Endpoint de la API
     * @return Respuesta JSON como String
     */
    private static String peticionGET(String endpoint) throws Exception {
        // Construir URL con API key
        String urlCompleta = BASE_URL + endpoint + "?api_key=" + API_KEY;

        // Crear conexión HTTP
        URL url = new URL(urlCompleta);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

        // Configurar método GET
        conexion.setRequestMethod("GET");
        conexion.setRequestProperty("Accept", "application/json");

        // Leer respuesta
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conexion.getInputStream()));
        StringBuilder respuesta = new StringBuilder();
        String linea;

        while ((linea = reader.readLine()) != null) {
            respuesta.append(linea);
        }
        reader.close();

        return respuesta.toString();
    }

    /**
     * Obtiene los datos finales desde la URL proporcionada por la API
     * La API de AEMET usa un sistema de dos pasos:
     * 1. Primera petición devuelve una URL en el campo "datos"
     * 2. Segunda petición a esa URL obtiene los datos reales
     */
    private static String obtenerDatos(String jsonRespuesta) throws Exception {
        // Parsear la respuesta inicial
        RespuestaAEMET respuesta = gson.fromJson(jsonRespuesta, RespuestaAEMET.class);

        // Verificar estado exitoso (código 200)
        if (respuesta.estado != 200) {
            throw new Exception("Error en API: " + respuesta.descripcion);
        }

        // Hacer segunda petición a la URL de datos
        URL url = new URL(respuesta.datos);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conexion.getInputStream()));
        StringBuilder datos = new StringBuilder();
        String linea;

        while ((linea = reader.readLine()) != null) {
            datos.append(linea);
        }
        reader.close();

        return datos.toString();
    }

    // ============================================
    // MÉTODOS PARA CADA PESTAÑA DE LA APLICACIÓN
    // ============================================

    /**
     * PESTAÑA 1: Predicción del tiempo para España
     */
    public static String prediccionEspana() {
        try {
            String respuesta = peticionGET("/prediccion/nacional/hoy");
            return obtenerDatos(respuesta);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * PESTAÑA 2: Predicción por comunidades autónomas
     * @param codigoCCAA Código de la comunidad (ej: "and" para Andalucía)
     */
    public static String prediccionComunidad(String codigoCCAA) {
        try {
            String respuesta = peticionGET("/prediccion/ccaa/hoy/" + codigoCCAA);
            return obtenerDatos(respuesta);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * PESTAÑA 3: Predicción por provincias
     * @param codigoProvincia Código de provincia (ej: "28" para Madrid)
     */
    public static String prediccionProvincia(String codigoProvincia) {
        try {
            String respuesta = peticionGET("/prediccion/provincia/hoy/" + codigoProvincia);
            return obtenerDatos(respuesta);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * PESTAÑA 4: Predicción por localidades
     * @param codigoMunicipio Código del municipio (ej: "28079" para Madrid)
     */
    public static String prediccionLocalidad(String codigoMunicipio) {
        try {
            String respuesta = peticionGET("/prediccion/especifica/municipio/diaria/" + codigoMunicipio);
            return obtenerDatos(respuesta);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * PESTAÑA 5: Predicción por macizos montañosos
     * @param codigoMacizo Código del macizo (ej: "nev1" para Sierra Nevada)
     */
    public static String prediccionMontana(String codigoMacizo) {
        try {
            String respuesta = peticionGET("/prediccion/especifica/montaña/pasada/area/" + codigoMacizo);
            return obtenerDatos(respuesta);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * PESTAÑA 6: Predicción por playas
     * @param codigoPlaya Código de la playa
     */
    public static String prediccionPlaya(String codigoPlaya) {
        try {
            String respuesta = peticionGET("/prediccion/especifica/playa/" + codigoPlaya);
            return obtenerDatos(respuesta);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * PESTAÑA 7: Valores climatológicos diarios por estación
     * @param fechaInicio Fecha inicio (formato: AAAA-MM-DD)
     * @param fechaFin Fecha fin (formato: AAAA-MM-DD)
     * @param idEstacion ID de la estación meteorológica
     */
    public static List<ValoresDiarios> valoresClimatologicos(
            String fechaInicio, String fechaFin, String idEstacion) {
        try {
            String endpoint = String.format(
                    "/valores/climatologicos/diarios/datos/fechaini/%s/fechafin/%s/estacion/%s",
                    fechaInicio, fechaFin, idEstacion
            );

            String respuesta = peticionGET(endpoint);
            String datos = obtenerDatos(respuesta);

            // Parsear array JSON a lista de objetos
            return gson.fromJson(datos, new TypeToken<List<ValoresDiarios>>(){}.getType());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Obtiene el inventario completo de estaciones meteorológicas
     */
    public static List<Estacion> obtenerEstaciones() {
        try {
            String respuesta = peticionGET("/valores/climatologicos/inventarioestaciones/todasestaciones");
            String datos = obtenerDatos(respuesta);
            return gson.fromJson(datos, new TypeToken<List<Estacion>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

// ============================================
// 2. Modelos de datos (Clases POJO)
// ============================================

/**
 * Representa la respuesta inicial de la API de AEMET
 */
class RespuestaAEMET {
    String descripcion;
    int estado;
    String datos;  // URL con los datos reales
    String metadatos;  // URL con metadatos
}

/**
 * Representa una estación meteorológica
 */
class Estacion {
    String indicativo;
    String nombre;
    String provincia;
    String altitud;
    String latitud;
    String longitud;

    @Override
    public String toString() {
        return nombre + " (" + provincia + ")";
    }
}

/**
 * Representa valores climatológicos diarios
 */
class ValoresDiarios {
    String fecha;
    String indicativo;
    String nombre;
    String provincia;
    String tmed;  // Temperatura media
    String tmin;  // Temperatura mínima
    String tmax;  // Temperatura máxima
    String prec;  // Precipitación

    @Override
    public String toString() {
        return String.format("%s - %s: Tª media: %s°C, Tª max: %s°C, Tª min: %s°C, Precipitación: %s mm",
                fecha, nombre, tmed, tmax, tmin, prec);
    }
}

