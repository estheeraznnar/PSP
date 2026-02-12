package org.example.APIREST;

import java.util.List;

//Clase principal para probar
public class Main {

    public static void main(String[] args) {

        ClienteAemet.apiKey = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG9taW5nb2JAaWVzY2gub3JnIiwianRpIjoiYTA3NWIwMWMtYzFlZi00MDVjLTgyYmItZTIwOWE0MjMxMTgwIiwiaXNzIjoiQUVNRVQiLCJpYXQiOjE3NjMwMzI3NjQsInVzZXJJZCI6ImEwNzViMDFjLWMxZWYtNDA1Yy04MmJiLWUyMDlhNDIzMTE4MCIsInJvbGUiOiIifQ.9U3a2vpAuC8g5LWLQ38tamA5bIVhKkfBLetgsXA7lOA";

        try {

            List<Estacion> estaciones =
                    ClienteAemet.inventarioEstacionesTodas();

            System.out.println("Número de estaciones: " + estaciones.size());

            for (int i = 0; i < 10; i++) {
                System.out.println(estaciones.get(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
