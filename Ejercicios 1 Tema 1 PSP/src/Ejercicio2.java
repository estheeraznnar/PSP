import java.io.File;
import java.io.IOException;

public class Ejercicio2 {
    public static void main(String[] args) {
        // Rutas de los archivos
        String rutaDocumentos = System.getProperty("user.home") + "\\Documents\\";
        File ficheroBat = new File(rutaDocumentos + "comandos.bat");
        File logSalida = new File(rutaDocumentos + "salida.log");
        File logErrores = new File(rutaDocumentos +"errores.log");

        // Crear el ProcessBuilder
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", ficheroBat.getAbsolutePath());

        // Redirecciones de entrada, salida y error
        pb.redirectInput(ficheroBat);      // El proceso leerá los comandos desde el .bat
        pb.redirectOutput(logSalida);      // Salida estándar -> salida.log
        pb.redirectError(logErrores);      // Errores -> errores.log

        try {
            System.out.println("Ejecutando el .bat...");
            Process proceso = pb.start();
            int exitCode = proceso.waitFor(); // Esperar a que termine
            System.out.println("Proceso terminado con código: " + exitCode);
            System.out.println("Revisa los logs en: " + rutaDocumentos);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

