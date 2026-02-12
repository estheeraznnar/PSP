package org.example.APIREST;

import com.fasterxml.jackson.annotation.JsonProperty;

//Modelo para representar una estación meteorológica
public class Estacion {

    @JsonProperty("latitud")
    private String latitud;

    @JsonProperty("provincia")
    private String provincia;

    @JsonProperty("altitud")
    private String altitud;

    @JsonProperty("indicativo")
    private String indicativo;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("indsinop")
    private String indsinop;

    @JsonProperty("longitud")
    private String longitud;

    @Override
    public String toString() {
        return nombre + " (" + provincia + ") - " + indicativo;
    }
}