package org.example.Cifrado_Simetrico_AES;

import java.io.File;
import java.util.Scanner;

/**
 * Menú principal para cifrar/descifrar ficheros.
 *
 * 1) Cifrar fichero
 * 2) Descifrar fichero
 * 0) Salir
 */
public class MainCrypto {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Cifrado AES de Ficheros ===");

        while (true) {
            System.out.println("\n--- MENÚ ---");
            System.out.println("1) Cifrar fichero");
            System.out.println("2) Descifrar fichero");
            System.out.println("0) Salir");
            System.out.print("Opción: ");

            String option = sc.nextLine().trim();

            switch (option) {
                case "1":
                    cifrar(sc);
                    break;
                case "2":
                    descifrar(sc);
                    break;
                case "0":
                    System.out.println("¡Hasta luego!");
                    sc.close();
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private static void cifrar(Scanner sc) {
        System.out.print("Fichero de entrada: ");
        String inputName = sc.nextLine().trim();
        File input = new File(inputName);
        if (!input.exists()) {
            System.out.println(" Fichero no encontrado.");
            return;
        }

        System.out.print("Fichero cifrado (salida): ");
        String outputName = sc.nextLine().trim();
        File output = new File(outputName);

        try {
            FileCrypto.encryptFile(input, output);
        } catch (Exception e) {
            System.err.println("Error cifrando: " + e.getMessage());
        }
    }

    private static void descifrar(Scanner sc) {
        System.out.print("Fichero cifrado (entrada): ");
        String inputName = sc.nextLine().trim();
        File input = new File(inputName);
        if (!input.exists()) {
            System.out.println(" Fichero no encontrado.");
            return;
        }

        System.out.print("Fichero descifrado (salida): ");
        String outputName = sc.nextLine().trim();
        File output = new File(outputName);

        try {
            FileCrypto.decryptFile(input, output);
        } catch (Exception e) {
            System.err.println("Error descifrando: " + e.getMessage());
        }
    }
}
