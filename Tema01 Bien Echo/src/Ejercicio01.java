
/*Crea un archivo de texto con la carpeta Documentos de tu ordenador.
Crea un programa en java que lance un proceso para abrir este fichero con Notepad++.*/

import java.io.File;
import java.io.IOException;

public class Ejercicio01 {

    public static void main(String[] args) {

        //Ruta del archivo
        //Me aseguro que este notepad++ instalado
        String rutaArchivo = System.getProperty("user.home") + "\\Documents\\Ejercicio01.txt";

        //Creo el archivo si no existe
        File archivo = new File(rutaArchivo);

        try {

            if (archivo.createNewFile()){
                System.out.println("Archivo creado correctamente" + archivo.getAbsolutePath());
            }else {
                System.out.println("El archivo ya existe " + archivo.getAbsolutePath());
            }

            //Ruta del ejecutable del archivo
            String rutaEjecutable = "C:\\Program Files\\Notepad++\\notepad++.exe";

            //Creo el proceso para abrir el archo
            ProcessBuilder pb = new ProcessBuilder(rutaEjecutable, archivo.getAbsolutePath());
            pb.start();
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }

}
