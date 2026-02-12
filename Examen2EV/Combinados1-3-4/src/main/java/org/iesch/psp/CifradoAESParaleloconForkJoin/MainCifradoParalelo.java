package org.iesch.psp.CifradoAESParaleloconForkJoin;// --- MainCifradoParalelo.java ---
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class MainCifradoParalelo {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Directorio con archivos a cifrar:");
        String dirEntrada = sc.nextLine();
        System.out.println("Directorio de salida:");
        String dirSalida = sc.nextLine();

        File carpeta = new File(dirEntrada);
        File carpetaSalida = new File(dirSalida);

        if (!carpeta.isDirectory()) {
            System.out.println("El directorio de entrada no existe.");
            return;
        }

        if (!carpetaSalida.exists()) {
            carpetaSalida.mkdirs();
        }

        // Obtener solo archivos .txt
        File[] todosArchivos = carpeta.listFiles();
        List<File> archivosTxt = new ArrayList<>();
        for (File f : todosArchivos) {
            if (f.isFile() && f.getName().endsWith(".txt")) {
                archivosTxt.add(f);
            }
        }

        File[] archivos = archivosTxt.toArray(new File[0]);
        System.out.println("Archivos encontrados: " + archivos.length);

        if (archivos.length == 0) {
            System.out.println("No hay archivos .txt para cifrar.");
            return;
        }

        // === CIFRADO SECUENCIAL ===
        System.out.println("\n=== CIFRADO SECUENCIAL ===");
        long inicioSec = System.currentTimeMillis();

        for (File archivo : archivos) {
            String nombreSalida = dirSalida + File.separator + "sec_" + archivo.getName() + ".enc";
            CifradorAES.cifrarArchivo(archivo.getAbsolutePath(), nombreSalida);
            System.out.println("Cifrado: " + archivo.getName());
        }

        long finSec = System.currentTimeMillis();
        System.out.println("Tiempo secuencial: " + (finSec - inicioSec) + " ms");

        // === CIFRADO PARALELO CON FORK-JOIN ===
        System.out.println("\n=== CIFRADO PARALELO (Fork-Join) ===");
        ForkJoinPool pool = new ForkJoinPool();

        TareaCifradoForkJoin tarea = new TareaCifradoForkJoin(archivos, 0, archivos.length, dirSalida);

        long inicioPar = System.currentTimeMillis();
        pool.invoke(tarea);
        long finPar = System.currentTimeMillis();

        System.out.println("Tiempo paralelo: " + (finPar - inicioPar) + " ms");

        // Comparación
        System.out.println("\n=== COMPARACIÓN ===");
        System.out.println("Secuencial: " + (finSec - inicioSec) + " ms");
        System.out.println("Paralelo:   " + (finPar - inicioPar) + " ms");

        if ((finPar - inicioPar) < (finSec - inicioSec)) {
            System.out.println("El cifrado paralelo fue más rápido.");
        } else {
            System.out.println("El cifrado secuencial fue más rápido (pocos archivos).");
        }
    }
}