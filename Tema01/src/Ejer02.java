
/*Hemos visto en los ejemplos que podemos lanzar un proceso con
ProcessBuilder y ejecutar comandos de consola (cmd) en él. En el ejemplo
indicábamos exactamente el comando a ejecutar y mostrábamos por consola
el resultado de la ejecución. La clase ProcessBuilder permite redirigir la
entrada, salida y los errores en la ejecución de proceso a ficheros. Busca
información en la ayuda de Java sobre los métodos redirectInput,
redirectOutput y redirectError. Deberás desarrollar un programa que lance un
subproceso cmd con ProcessBuilder, el programa debe obtener los comandos
a ejecutar por la consola de un fichero .bat que habrás creado tú
previamente. El programa dejará el log de ejecución en un fichero de salida y
el log de errores en otro fichero. Deberás utilizar los métodos redirectInput,
redirectOutput y redirectError.
El fichero bat podría tener por ejemplo los siguientes comandos:
ping www.dam2chomon.org
ping www.google.es
pring www.iesch.org
De esa forma veremos que ocurre en cada situación:
• un comando correcto con una dirección que no existe
• un comando correcto con una dirección que existe
• un comando incorrecto*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Ejer02 {
    public static void main(String[] args) {
        File ficheroBat = new File("comandos.bat");
        File salidaLog = new File("salida.log");
        File errorLog = new File("error.log");

        //verifico si el archivo bat existe
        if(!ficheroBat.exists()){
            System.err.println("El archivo comandos.bat no existe");
            return;
        }

        //creo un ProcessBuilder con cmd.exe y el parametro /C para ejecutar el bat
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/C", "comandos.bat");

        //redirijo la entrada para la salida y errores
        pb.redirectOutput(salidaLog);
        pb.redirectError(errorLog);

        try {
            System.out.println("-----Iniciando proceso-----");
            System.out.println("Salida: " + salidaLog.getAbsolutePath());
            System.out.println("Errores: " + errorLog.getAbsolutePath());

            //Inicio el proceso
            Process process = pb.start();

            //Espero a que termine
            int codSalida = process.waitFor();

            System.out.println("\n--- Proceso finalizado ---");
            System.out.println("Codigo de salida: " + codSalida);

            //Muestro el contenido de los logs
            mostrarArchivo("salida.log", salidaLog);
            mostrarArchivo("error.log", errorLog);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void mostrarArchivo(String nombre, File archivo) {
        System.out.println("\n------" + nombre + " ------");
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))){

            String linea;
            int cont = 0;
            while ((linea = br.readLine()) != null){
                System.out.println(linea);
                cont++;
            }

            if (cont == 0){
                System.out.println("(archivo vacio)");
            }

        }catch (IOException e){
            System.err.println("Error al leer " + nombre + ": " + e.getMessage());
        }

        System.out.println("----------------------\n");
    }
}
