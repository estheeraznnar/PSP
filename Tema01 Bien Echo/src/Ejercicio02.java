
/*Hemos visto en los ejemplos que podemos lanzar un proceso con ProcessBuilder
y ejecutar comandos de consola (cmd) en él. En el ejemplo indicábamos exactamente
el comando a ejecutar y mostrábamos por consola el resultado de la ejecución.
La clase ProcessBuilder permite redirigir la entrada, salida y los errores en
la ejecución de proceso a ficheros. Busca información en la ayuda de Java sobre
los métodos redirectInput, redirectOutput y redirectError. Deberás desarrollar
un programa que lance un subproceso cmd con ProcessBuilder, el programa debe obtener
los comandos a ejecutar por la consola de un fichero .bat que habrás creado tú previamente.
El programa dejará el log de ejecución en un fichero de salida y el log de errores en otro fichero.
Deberás utilizar los métodos redirectInput, redirectOutput y redirectError.

El fichero bat podría tener por ejemplo los siguientes comandos:
ping www.dam2chomon.org
ping www.google.es
pring www.iesch.org
De esa forma veremos que ocurre en cada situación:
• un comando correcto con una dirección que no existe
• un comando correcto con una dirección que existe
• un comando incorrecto*/

import java.io.File;
import java.io.IOException;

public class Ejercicio02 {

    public static void main(String[] args) {
        //Rutas de los archivos
        String rutaDocumentos = System.getProperty("user.home") + "\\Documents\\";
        File ficheroBat = new File(rutaDocumentos + "comandos.bat");
        File logSalida = new File(rutaDocumentos + "salida.log");
        File logErrores = new File(rutaDocumentos + "errores.log");

        //Creo el proces builder
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", ficheroBat.getAbsolutePath());

        //Redireccion de entrada salida y error
        pb.redirectInput(ficheroBat);
        pb.redirectOutput(logSalida);
        pb.redirectError(logErrores);

        try {
            System.out.println("Ejecutando el bat");
            Process process = pb.start();
            int exit = process.waitFor(); //Esepra a que termine
            System.out.println("Proceso terminado con codigo: " + exit);
            System.out.println("Revida los logs en: " + rutaDocumentos);
        }catch (InterruptedException | IOException e){
            e.printStackTrace();
        }
    }

}
