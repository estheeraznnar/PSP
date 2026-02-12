package org.iesch.psp.DescargadorFTPConcurrente;// --- DescargadorConcurrente.java (Main con ThreadPoolExecutor) ---
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.ftp.FTPFile;
import java.util.*;
import java.util.concurrent.*;

public class DescargadorConcurrente {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Servidor FTP:");
        String servidor = sc.nextLine();
        System.out.println("Usuario:");
        String usuario = sc.nextLine();
        System.out.println("Contraseña:");
        String password = sc.nextLine();

        try {
            // Primero obtenemos la lista de archivos
            FTPSClient ftp = new FTPSClient("TLS", false);
            ftp.setTrustManager(new javax.net.ssl.X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
            });

            ftp.connect(servidor);
            ftp.login(usuario, password);
            ftp.enterLocalPassiveMode();
            ftp.execPBSZ(0);
            ftp.execPROT("P");

            FTPFile[] archivos = ftp.listFiles();
            List<String> nombresArchivos = new ArrayList<>();

            System.out.println("\nArchivos disponibles:");
            for (FTPFile archivo : archivos) {
                if (archivo.isFile()) {
                    System.out.println("  - " + archivo.getName());
                    nombresArchivos.add(archivo.getName());
                }
            }

            ftp.logout();
            ftp.disconnect();

            if (nombresArchivos.isEmpty()) {
                System.out.println("No hay archivos para descargar.");
                return;
            }

            // Crear lista de tareas Callable
            List<DescargaFTP> tareas = new ArrayList<>();
            for (String nombre : nombresArchivos) {
                tareas.add(new DescargaFTP(servidor, usuario, password, nombre, "descarga_" + nombre));
            }

            // DESCARGA CONCURRENTE con ThreadPoolExecutor
            System.out.println("\n=== DESCARGA CONCURRENTE (3 hilos) ===");
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

            long inicioConcurrente = System.currentTimeMillis();
            List<Future<String>> resultados = executor.invokeAll(tareas);
            executor.shutdown();
            long finConcurrente = System.currentTimeMillis();

            // Mostrar resultados
            for (Future<String> resultado : resultados) {
                System.out.println(resultado.get());
            }

            System.out.println("Tiempo concurrente: " + (finConcurrente - inicioConcurrente) + " ms");
            System.out.println("Tareas completadas: " + executor.getCompletedTaskCount());

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}