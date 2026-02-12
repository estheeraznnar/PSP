package org.iesch.psp.EjercicioFTP;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.X509Certificate;

public class EjercicioFTP {

    public static void main(String[] args) {
        // 1. Crear cliente FTPS (TLS explícito, isImplicit = false)
        FTPSClient ftp = new FTPSClient("TLS", false);

        try {
            // 2. Configurar TrustManager para aceptar certificados autofirmados (Entorno de desarrollo)
            // Esto es CRÍTICO según el PDF para evitar excepciones de SSLHandshake
            ftp.setTrustManager(new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) {}
                public void checkServerTrusted(X509Certificate[] xcs, String string) {}
                public X509Certificate[] getAcceptedIssuers() { return null; }
            });

            // 3. Conexión al servidor
            System.out.println("Conectando a servidorifc.iesch.org...");
            ftp.connect("servidorifc.iesch.org");

            // Verificar respuesta del servidor tras conectar
            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                ftp.disconnect();
                System.out.println("No se pudo conectar al servidor. Código: " + ftp.getReplyCode());
                return;
            }

            // 4. Login
            boolean loginExitoso = ftp.login("user", "psw");
            if (!loginExitoso) {
                System.out.println("Error de autenticación.");
                ftp.disconnect();
                return;
            }
            System.out.println("Login correcto.");

            // 5. Configuración de seguridad del canal de datos (OBLIGATORIO en FTPS)
            ftp.execPBSZ(0); // Protection Buffer Size
            ftp.execPROT("P"); // Data Channel Protection = Private (Encriptado)

            // 6. Modo Pasivo (Para evitar problemas con Firewalls)
            ftp.enterLocalPassiveMode();

            // 7. Listar el directorio raíz
            System.out.println("Listando archivos del directorio raíz:");
            listDirectory(ftp);

            // 8. Desconexión limpia
            ftp.logout();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    // Ignorar error al desconectar
                }
            }
        }
    }

    // Método auxiliar para listar archivos (Tal cual aparece en la pág 5 del PDF)
    private static void listDirectory(FTPSClient ftp) throws IOException {
        FTPFile[] files = ftp.listFiles();

        if (files == null || files.length == 0) {
            System.out.println("No se encontraron archivos o el directorio está vacío.");
            return;
        }

        for (FTPFile file : files) {
            // Imprimimos solo el nombre, como en el ejemplo
            System.out.println(file.getName());
        }
    }
}