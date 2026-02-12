package org.iesch.psp.LanzadorComandos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LaunchPing {
    public static void main(String[] args) {
        try {
            // Lanzamos el proceso que permite ejecutar comandos cmd
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "ping www.google.es");
            Process p = pb.start();

            // Accedemos al buffer de lectura del proceso lanzado
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            // Leemos el resultado de los comandos y lo mostramos por consola
            String line = "";
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Esperamos a que el proceso termine
            p.waitFor();
            System.out.println("Fin de la ejecución");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}