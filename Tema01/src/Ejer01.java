import java.io.IOException;

/*Crea un archivo de texto con la carpeta Documentos de tu ordenador. Crea un
programa en java que lance un proceso para abrir este fichero con
Notepad++.*/
public class Ejer01 {
    public static void main(String[] args) {

        //Ruta completa del fichero de texto
        String rutaFichero = "C:\\Users\\dam2\\_ESTHER_\\PSP\\prueba.txt" ;
        //Ruta del ejecutable
        String rutaNotepad = "C:\\Program Files\\Notepad++\\notepad++.exe";

        try {
            //Creo un proceso con ProcessBuilder para lanzar el notePad con el fichero
            ProcessBuilder pb = new ProcessBuilder(rutaNotepad, rutaFichero);
            Process process = pb.start();
            System.out.println("NotePad++ se ha abierto correctamente con el archivo: " + rutaFichero);

            //Espero a que el usuario cierre el notepad
            process.waitFor();
            System.out.println("El proceso ha terminado");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}