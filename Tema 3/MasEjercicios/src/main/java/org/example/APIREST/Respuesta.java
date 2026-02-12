package org.example.APIREST;

import com.fasterxml.jackson.annotation.JsonProperty;

//Modelo para la respuesta inicial de AEMET
//Esta clase representa la primera respuesta que devuelve la API de AEMET.
//Cuando haces una petición a AEMET, NO te devuelve directamente los datos, sino un JSON
public class Respuesta {

    public static final int OK = 200;

    @JsonProperty("descripcion")
    private String descripcion;

    @JsonProperty("estado")
    private int estado;

    @JsonProperty("datos")
    private String datos;

    @JsonProperty("metadatos")
    private String metadatos;

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }

    public String getDatos() { return datos; }
    public void setDatos(String datos) { this.datos = datos; }

    public String getMetadatos() { return metadatos; }
    public void setMetadatos(String metadatos) { this.metadatos = metadatos; }
}
