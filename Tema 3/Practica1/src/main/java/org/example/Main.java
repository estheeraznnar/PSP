package org.example;

/**
 * Aplicación Cliente Aemet
 * A partir del ejemplo desarrollado en clase vamos a desarrollar una aplicación cliente.
 * La aplicación tendrá 7 pestañas para consultar:
 * 1.
 * La predicción del tiempo para España
 * 2.
 * La predicción del tiempo por comunidades autónomas
 * 3.
 * La predicción del tiempo por provincias
 * 4.
 * La predicción del tiempo por localidades
 * 5.
 * La predicción del tiempo por macizos montañosos
 * 6.
 * La predicción del tiempo por playas
 * 7.
 * Los valores diarios climatológicos por estación meteorológica (ya desarrollado en clase).
 * Trabajaréis en dos equipos: las dos primeras filas un equipo y las dos últimas otro equipo.
 * Deberéis trabajar con git para poder desarrollar el proyecto en equipo. Podéis distribuiros
 * el trabajo como consideréis oportuno. Como punto de partida debéis utilizar el proyecto del
 * acceso a la Web API de Aemet que hemos desarrollado en clase.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}

//Modo uso
/**
 * Instrucciones de uso:
 *
 * Obtener API Key:
 * Ve a:
 * https://opendata.aemet.es/centrodedescargas/obtencionAPIKey
 * Regístrate y obtén tu clave API
 * La aplicación te la pedirá al iniciar
 *
 * Compilar y ejecutar:
 * # Si usas Maven
 * mvn clean compile
 * mvn exec:java -Dexec.mainClass="AplicacionAEMET"
 * # O compilar manualmente
 * javac -cp gson-2.10.1.jar *.java
 * java -cp .:gson-2.10.1.jar AplicacionAEMET
 *
 *
 * Características implementadas:
 * ✓ 7 pestañas según requisitos
 * ✓ Interfaz gráfica intuitiva
 * ✓ Consume API REST de AEMET
 * ✓ Parseo de JSON con Gson
 * ✓ Hilos para no bloquear la interfaz
 * ✓ Manejo de errores
 *
 *
 * Para trabajo en equipo con Git:
 * # Crear repositorio
 * git init
 * git add .
 * git commit -m "Aplicación Cliente AEMET - 7 pestañas"
 * git remote add origin <URL_REPOSITORIO>
 * git push -u origin main
 */