package org.iesch.psp.SistemaDeBackupCifradoConcurrente;// --- LogSincronizado.java ---
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogSincronizado {

    private PrintWriter writer;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public LogSincronizado(String archivoLog) throws IOException {
        writer = new PrintWriter(new FileWriter(archivoLog, true), true);
    }

    public synchronized void escribir(String mensaje) {
        String linea = "[" + sdf.format(new Date()) + "] " + mensaje;
        System.out.println(linea);
        writer.println(linea);
    }

    public void cerrar() {
        writer.close();
    }
}