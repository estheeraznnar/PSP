package org.iesch.psp.MapeoDeObjetosDataBinding;

import com.fasterxml.jackson.annotation.JsonProperty;

// POJO (Plain Old Java Object) que representa la estructura JSON de la AEMET
public class Respuesta {

    // Constante para validar el estado (Buenas prácticas del PDF)
    public static final int OK = 200;

    // Las anotaciones @JsonProperty son CRÍTICAS: vinculan el campo JSON con la variable Java
    @JsonProperty("descripcion")
    private String descripcion;

    @JsonProperty("estado")
    private int estado;

    @JsonProperty("datos")
    private String datos; // URL donde están los datos reales

    @JsonProperty("metadatos")
    private String metadatos;

    // --- Getters y Setters (Obligatorios para que Jackson funcione) ---

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }

    public String getDatos() { return datos; }
    public void setDatos(String datos) { this.datos = datos; }

    public String getMetadatos() { return metadatos; }
    public void setMetadatos(String metadatos) { this.metadatos = metadatos; }

    @Override
    public String toString() {
        return "Respuesta{" +
                "descripcion='" + descripcion + '\'' +
                ", estado=" + estado +
                ", datos='" + datos + '\'' +
                ", metadatos='" + metadatos + '\'' +
                '}';
    }
}