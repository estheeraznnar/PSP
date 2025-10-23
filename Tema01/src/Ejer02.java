
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

import java.io.File;

public class Ejer02 {
    public static void main(String[] args) {
        File ficheroBat = new File("C:\\Users\\dam2\\_ESTHER_\\PSP\\comandos.bat");
        File salidaLog = new File("C:\\Users\\dam2\\_ESTHER_\\PSP\\salida.log");
        File errorLog = new File("C:\\Users\\dam2\\_ESTHER_\\PSP\\error.log");

        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/C");

        //redirijo la entrada para que el cmd lea ek
    }
}
