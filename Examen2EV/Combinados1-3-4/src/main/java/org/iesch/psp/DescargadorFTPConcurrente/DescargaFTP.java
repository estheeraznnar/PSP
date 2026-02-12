package org.iesch.psp.DescargadorFTPConcurrente;// --- DescargaFTP.java (Tarea Callable) ---
import org.apache.commons.net.ftp.FTPSClient;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.util.concurrent.Callable;

public class DescargaFTP implements Callable<String> {

    private String servidor;
    private String usuario;
    private String password;
    private String archivoRemoto;
    private String archivoLocal;

    public DescargaFTP(String servidor, String usuario, String password,
                       String archivoRemoto, String archivoLocal) {
        this.servidor = servidor;
        this.usuario = usuario;
        this.password = password;
        this.archivoRemoto = archivoRemoto;
        this.archivoLocal = archivoLocal;
    }

    @Override
    public String call() throws Exception {
        FTPSClient ftp = new FTPSClient("TLS", false);

        try {
            // Configurar TrustManager para certificados autofirmados
            ftp.setTrustManager(new javax.net.ssl.X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
            });

            ftp.connect(servidor);
            System.out.println("[" + Thread.currentThread().getName() + "] Conectado a " + servidor);

            if (!ftp.login(usuario, password)) {
                return "ERROR: Login fallido para " + archivoRemoto;
            }

            ftp.enterLocalPassiveMode();
            ftp.execPBSZ(0);
            ftp.execPROT("P");

            // Descargar archivo
            FileOutputStream fos = new FileOutputStream(archivoLocal);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            boolean exito = ftp.retrieveFile(archivoRemoto, bos);

            bos.close();
            fos.close();
            ftp.logout();
            ftp.disconnect();

            if (exito) {
                return "OK: " + archivoRemoto + " descargado por " + Thread.currentThread().getName();
            } else {
                return "ERROR: No se pudo descargar " + archivoRemoto;
            }

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}