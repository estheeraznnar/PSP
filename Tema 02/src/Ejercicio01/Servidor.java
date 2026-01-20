package Ejercicio01;

/*El servidor deberá atender las peticiones del cliente.
Las peticiones del cliente serán de 2 tipos: Listar ficheros y Mostrar fichero.
La primera opción enviará al cliente el listado de ficheros existentes en un directorio
de trabajo definido en el servidor. La segunda enviará al cliente el contenido de un fichero
(el nombre del fichero formará parte de la solicitud del cliente),
supondremos que todos los ficheros disponibles serán de ficheros de texto.
El servidor debe permitir la conexión de múltiples clientes.*/

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Servidor {
    private static final int Puerto = 5000;
    public static final String directorio_trabajo = "./archivos_servidor";
    private static volatile boolean shutdown = false;

    public static void main(String[] args) {

        //obtengo el puerto de argumentos si se proporciona
        int puerto = Puerto;
        try {
            if (args.length>0){
                puerto=Integer.parseInt(args[0]);
            }
        }catch (Exception e){
            System.out.println("Puerto incorrecto usando puerto por defecto: " + Puerto);
        }

        //Arranco el servidor
        try (ServerSocket serverSocket = new ServerSocket(puerto)){
            serverSocket.setSoTimeout(3000); //Timeout para responder la orden de parada

            System.out.println("Servidor iniciado en " + LocalDateTime.now());
            System.out.println("Puerto del servidor: " + serverSocket.getLocalPort());
            System.out.println("Directorio de trabajo: " + directorio_trabajo);

            //Acepto peticiones en bucle
            while (!shutdown){
                try {
                    Socket clienteSocket = serverSocket.accept();
                    System.out.println("Cliente conectado: " + clienteSocket.getInetAddress() + ":" + clienteSocket.getPort());

                    //Creo un hilo para atender al cliente
                    new Thread(new ManejadorCliente(clienteSocket)).start();

                }catch (SocketTimeoutException e){
                    //Timeout alcanzado, verificar condicion de parada
                    continue;
                }
            }

            System.out.println("Servidor detenido en: " + LocalDateTime.now());
        }catch (IOException e) {
            System.err.println("Error en servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Clase interna que implementa Runnable para atender al cliente
    static class ManejadorCliente implements Runnable{

        private Socket socket;

        public ManejadorCliente(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            try(DataInputStream entrada = new DataInputStream(socket.getInputStream());
                DataOutputStream salida = new DataOutputStream(socket.getOutputStream())){

                //Leo el tipo de solicitud
                String solicitud = entrada.readUTF();
                System.out.println("Solicitud recibida: " + solicitud);

                if ("LISTAR".equals(solicitud)){
                    listarArchivos(salida);
                } else if (solicitud.startsWith("MOSTRAR: ")) {
                    String nombreArchivo = solicitud.substring(8);
                    mostrarArchivo(nombreArchivo, salida);
                }else {
                    salida.writeUTF("ERROR: Solicitud no reconocida");
                }
            }catch (IOException e){
                System.err.println("Error manejando cliente: " + e.getMessage());
                e.printStackTrace();
            }finally {
                try {
                    socket.close();
                    System.out.println("Conexion cerrada con: " + socket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void mostrarArchivo(String nombreArchivo, DataOutputStream salida) throws IOException {

            File archivo = new File(directorio_trabajo, nombreArchivo);

            if (!archivo.exists() || !archivo.isFile()){
                salida.writeUTF("Error: Archivo no encontrado: " + nombreArchivo);
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                StringBuilder contenido = new StringBuilder();
                String linea;

                while ((linea = br.readLine()) != null){
                    contenido.append(linea).append("\n");
                }
                salida.writeUTF(contenido.toString());
            }catch (IOException e){
                salida.writeUTF("Error: no se pudo leer el archivo - " + e.getMessage());
            }

        }

        private void listarArchivos(DataOutputStream salida) throws IOException{

            File directorio = new File(directorio_trabajo);

            if (!directorio.exists() || !directorio.isDirectory()){
                salida.writeUTF("Error: Directorio no disponible");
                return;
            }

            File[] archivos = directorio.listFiles();
            if (archivos == null || archivos.length == 0){
                salida.writeUTF("No hay archivos disponibles en el servidor");
            }else {
                StringBuilder listado = new StringBuilder("Archivos disponibles: \n");

                for (File archivo: archivos){
                    if (archivo.isFile()){
                        listado.append("-")
                                .append(archivo.getName())
                                .append("\n");
                    }
                }
                salida.writeUTF(listado.toString());
            }

        }


    }
}
