package org.iesch.psp.CifradoAESParaleloconForkJoin;// --- TareaCifradoForkJoin.java (RecursiveAction para Fork-Join) ---
import java.io.File;
import java.util.concurrent.RecursiveAction;

public class TareaCifradoForkJoin extends RecursiveAction {

    private static final int UMBRAL = 5; // Si hay más de 5 archivos, dividir
    private File[] archivos;
    private int inicio;
    private int fin;
    private String directorioSalida;

    public TareaCifradoForkJoin(File[] archivos, int inicio, int fin, String directorioSalida) {
        this.archivos = archivos;
        this.inicio = inicio;
        this.fin = fin;
        this.directorioSalida = directorioSalida;
    }

    @Override
    protected void compute() {
        int cantidad = fin - inicio;

        if (cantidad <= UMBRAL) {
            // Caso base: cifrar secuencialmente
            cifrarSecuencial();
        } else {
            // Caso recursivo: dividir en dos subtareas
            int medio = inicio + cantidad / 2;

            TareaCifradoForkJoin tarea1 = new TareaCifradoForkJoin(archivos, inicio, medio, directorioSalida);
            TareaCifradoForkJoin tarea2 = new TareaCifradoForkJoin(archivos, medio, fin, directorioSalida);

            // Fork: lanzar subtareas en paralelo
            tarea1.fork();
            tarea2.fork();

            // Join: esperar a que terminen
            tarea1.join();
            tarea2.join();
        }
    }

    private void cifrarSecuencial() {
        for (int i = inicio; i < fin; i++) {
            File archivo = archivos[i];
            String nombreSalida = directorioSalida + File.separator + archivo.getName() + ".enc";

            System.out.println("[" + Thread.currentThread().getName() + "] Cifrando: " + archivo.getName());
            CifradorAES.cifrarArchivo(archivo.getAbsolutePath(), nombreSalida);
        }
    }
}