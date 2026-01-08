
/*El servidor deberá atender las peticiones del cliente.
Las peticiones del cliente serán de 2 tipos: Listar contactos y Buscar contacto.
La primera opción enviará al cliente el listado completo de contactos
almacenados (nombre, teléfono, correo).
La segunda enviará al cliente la información de un contacto específico
cuyo nombre formará parte de la solicitud del cliente.
El servidor debe permitir la conexión de múltiples clientes.*/

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args) {

        /* Configuración de parámetros (Puerto y Archivo)*/
            //valores por defecto
        int port = 12345;
        File workDir = new File("contactos.txt");
            //Argumentos de linea de comandos
        if (args.length >=1) port = Integer.parseInt(args[0]);
        if (args.length >=2) workDir = new File(args[1]);

        /*Apertura del Socket del Servidor*/
            //creo un try-whith-resources para crear el serverSocket par agarantizar que el socket
            //se cierre automaticamente si ocurre un error o al finalizar el programa
        try (ServerSocket serverSocket = new ServerSocket(port)){
            /*Bucle infinito y aceptacion de clientes*/
            while (true){
                //.accept-> es un metodo bloqueante. el programa para hasta que el cliente intenta conectarse
                //Cuando alguien se conecta devuelve un socket que representa la conexion
                //con ese cliente especifico
                Socket cliente = serverSocket.accept();
                //pra que no se quede bloqueado atendiendo a un solo cliente crrea un nuevo hilo
                Thread t = new Thread(new SrvThread(cliente, workDir));
                t.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
