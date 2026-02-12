package org.iesch.psp.SistemaDeBackupCifradoConcurrente;// --- TareaBackup.java (Callable que cifra y sube) ---
import org.apache.commons.net.ftp.FTPSClient;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

public class TareaBackup implements Callable<String> {

    private File archivo;
    private String servidor;
    private String usuario;
    private String password;
    private Semaphore semaforo;
    private LogSincronizado log;

    // Clave AES
    private static final byte[] key = {
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
            0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16
    };
    private static final byte[] iv = {
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
            0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16
    };

    public TareaBackup(File archivo, String servidor, String usuario,
                       String password, Semaphore semaforo, LogSincronizado log) {
        this.archivo = archivo;
        this.servidor = servidor;
        this.usuario = usuario;
        this.password = password;
        this.semaforo = semaforo;
        this.log = log;
    }

    @Override
    public String call() throws Exception {
        String nombreArchivo = archivo.getName();

        try {
            // 1. Cifrar archivo localmente
            log.escribir("[" + Thread.currentThread().getName() + "] Cifrando: " + nombreArchivo);

            byte[] datosCifrados = cifrarArchivo(archivo);

            // 2. Adquirir semáforo (máximo 2 conexiones FTP simultáneas)
            log.escribir("[" + Thread.currentThread().getName() + "] Esperando permiso FTP...");
            semaforo.acquire();

            try {
                // 3. Subir al servidor FTP
                log.escribir("[" + Thread.currentThread().getName() + "] Subiendo: " + nombreArchivo + ".enc");

                FTPSClient ftp = new FTPSClient("TLS", false);
                ftp.setTrustManager(new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
                });

                ftp.connect(servidor);
                if (!ftp.login(usuario, password)) {
                    return "ERROR: Login fallido para " + nombreArchivo;
                }

                ftp.enterLocalPassiveMode();
                ftp.execPBSZ(0);
                ftp.execPROT("P");

                ByteArrayInputStream bis = new ByteArrayInputStream(datosCifrados);
                boolean exito = ftp.storeFile(nombreArchivo + ".enc", bis);

                bis.close();
                ftp.logout();
                ftp.disconnect();

                if (exito) {
                    log.escribir("[" + Thread.currentThread().getName() + "] COMPLETADO: " + nombreArchivo);
                    return "OK: " + nombreArchivo;
                } else {
                    return "ERROR: No se pudo subir " + nombreArchivo;
                }

            } finally {
                // Liberar semáforo
                semaforo.release();
            }

        } catch (Exception e) {
            return "ERROR: " + nombreArchivo + " - " + e.getMessage();
        }
    }

    private byte[] cifrarArchivo(File archivo) throws Exception {
        // Leer archivo
        FileInputStream fis = new FileInputStream(archivo);
        byte[] datos = fis.readAllBytes();
        fis.close();

        // Cifrar con AES
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

        return cipher.doFinal(datos);
    }
}