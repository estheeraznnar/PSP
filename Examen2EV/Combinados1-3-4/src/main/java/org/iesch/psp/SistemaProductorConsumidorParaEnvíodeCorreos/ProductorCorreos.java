package org.iesch.psp.SistemaProductorConsumidorParaEnvíodeCorreos;// --- ProductorCorreos.java ---
import java.io.*;

public class ProductorCorreos implements Runnable {

    private ColaCorreos cola;
    private String archivoCorreos;

    public ProductorCorreos(ColaCorreos cola, String archivoCorreos) {
        this.cola = cola;
        this.archivoCorreos = archivoCorreos;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(archivoCorreos));
            String linea;

            while ((linea = reader.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    cola.poner(linea.trim());
                    Thread.sleep(100); // Simular tiempo de lectura
                }
            }

            reader.close();
            cola.terminarProduccion();
            System.out.println("[Productor] Lectura de archivo completada.");

        } catch (Exception e) {
            System.out.println("[Productor] Error: " + e.getMessage());
        }
    }
}