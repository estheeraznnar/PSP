package Ejercicio01;

/*El cliente debe seleccionar entre tres opciones: Listar ficheros, Mostrar fichero y Salir.
Cuando el usuario elija la primera opción, el cliente conectará con el servidor para solicitar
el listado de ficheros disponibles. Al elegir la segunda opción el cliente solicitará al usuario
el nombre del fichero y hará la petición al servidor para mostrar después al usuario el contenido del fichero.
La tercera opción cerrará el cliente.*/

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente {

    private static final String HOST = "127.0.0.1";
    private static final int PUERTO = 5000;

    public static void main(String[] args) {

        //Obtengo la informacion del servidor de argumentos si se proporciona
        String host = HOST;
        int puerto = PUERTO;

        try {
            if (args.length>=2){
                host = args[0];
                puerto = Integer.parseInt(args[1]);
            }
        }catch (Exception e){
            System.out.println("Usando configuracion por defecto");
        }

        Scanner sc = new Scanner(System.in);
        boolean continuar = true;

        while (continuar){
            mostrarMenu();

            int opcion = leerOpcion(sc);

            switch (opcion){
                case 1:
                    listarArchivos(host, puerto);
                    break;
                case 2:
                    System.out.println("Ingrse el nombre del archivo: ");
                    String nombreArchivo = sc.nextLine();
                    // Debug - imprimir lo que se va a enviar
                    System.out.println("DEBUG - Enviando: MOSTRAR:" + nombreArchivo);
                    mostrarArchivo(host, puerto, nombreArchivo);
                    break;
                case 3:
                    System.out.println("Cerrando cliente...");
                    continuar = false;
                    break;
                default:
                    System.out.println("Opcion invalida. Intente de nuevo.");
            }

            System.out.println();
        }

        sc.close();
        System.out.println("Cliente terminado.");

    }

    private static void mostrarMenu() {
        System.out.println("1. Listar ficheros");
        System.out.println("2. Mostrar ficheros");
        System.out.println("3. Salir");
        System.out.println("Seleccione una opcion: ");
    }

    private static int leerOpcion(Scanner sc) {
        int opcion = -1;
        if (sc.hasNextInt()){
            opcion = sc.nextInt();
        }

        sc.nextLine();
        return opcion;
    }

    private static void listarArchivos(String host, int puerto) {
        try (
                Socket socket = new Socket(host, puerto);
                DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
                DataInputStream entrada = new DataInputStream(socket.getInputStream())
        ) {
            System.out.println("Conectado a " + socket.getInetAddress()
                    + ":" + socket.getPort());

            // Enviar solicitud
            salida.writeUTF("LISTAR");

            // Recibir respuesta
            String respuesta = entrada.readUTF();
            System.out.println("\n" + respuesta);

        } catch (UnknownHostException e) {
            System.err.println("Host desconocido: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }

    private static void mostrarArchivo(String host, int puerto, String nombreArchivo) {
        try (
                Socket socket = new Socket(host, puerto);
                DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
                DataInputStream entrada = new DataInputStream(socket.getInputStream())
        ) {
            System.out.println("Conectado a " + socket.getInetAddress()
                    + ":" + socket.getPort());

            // Enviar solicitud
            salida.writeUTF("MOSTRAR:" + nombreArchivo);

            // Recibir respuesta
            String respuesta = entrada.readUTF();

            System.out.println("\n═══════ Contenido del archivo ═══════");
            System.out.println(respuesta);
            System.out.println("═════════════════════════════════════");

        } catch (UnknownHostException e) {
            System.err.println("Host desconocido: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }

}
