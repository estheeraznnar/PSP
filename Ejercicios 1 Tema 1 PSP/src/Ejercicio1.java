import java.io.File;
import java.io.IOException;

public class Ejercicio1 {
    public static void main(String[] args) {
        // Ruta del archivo (ajústala a tu sistema)
        // Asegúrate de que Notepad++ esté instalado en C:\Program Files\Notepad++\notepad++.exe.
        String rutaArchivo = System.getProperty("user.home") + "\\Documents\\ejemplo.txt";

        // Crear el archivo si no existe
        File archivo = new File(rutaArchivo);
        try {
            if (archivo.createNewFile()) {
                System.out.println("Archivo creado: " + archivo.getAbsolutePath());
            } else {
                System.out.println("El archivo ya existe: " + archivo.getAbsolutePath());
            }

            // Ruta al ejecutable de Notepad++
            String rutaNotepadPP = "C:\\Program Files\\Notepad++\\notepad++.exe";

            // Crear el proceso para abrir el archivo con Notepad++
            ProcessBuilder pb = new ProcessBuilder(rutaNotepadPP, archivo.getAbsolutePath());
            pb.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
