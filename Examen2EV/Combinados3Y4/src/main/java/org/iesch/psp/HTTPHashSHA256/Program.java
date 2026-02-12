package org.iesch.psp.HTTPHashSHA256;// --- Program.java ---
import java.util.Scanner;

public class Program {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Introduce la URL a descargar:");
        String url = sc.nextLine().trim();

        // Primera descarga
        System.out.println("Descargando contenido...");
        String contenido1 = SampleHttpClient.get(url);
        String hash1 = HashUtil.getHash(contenido1);
        System.out.println("Contenido descargado (" + contenido1.length() + " caracteres)");
        System.out.println("Hash SHA-256: " + hash1);

        // Esperamos a que el usuario quiera verificar
        System.out.println("\nPulsa ENTER para volver a descargar y verificar integridad...");
        sc.nextLine();

        // Segunda descarga
        System.out.println("Descargando contenido de nuevo...");
        String contenido2 = SampleHttpClient.get(url);
        String hash2 = HashUtil.getHash(contenido2);
        System.out.println("Hash SHA-256: " + hash2);

        // Verificación
        if (HashUtil.checkHash(contenido2, hash1)) {
            System.out.println("\nEl contenido NO ha cambiado. Integridad verificada.");
        } else {
            System.out.println("\nATENCIÓN: El contenido HA CAMBIADO desde la última descarga.");
        }
    }
}