
/*El cliente debe seleccionar entre tres opciones: Listar contactos, Buscar
contacto y Salir.
Cuando el usuario elija la primera opción, el cliente conectará con el
servidor para solicitar el listado de contactos.

Al elegir la segunda opción el cliente solicitará al usuario el nombre del
contacto, hará la petición al servidor y mostrará los datos recibidos al
usuario.
La tercera opción cerrará el cliente.
 */

import modelo.Contacto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class Cliente {
    private static boolean stop = false;

    public static void main(String[] args) {
        /*Inicialización y Conexión*/

        
        String host;
        int port;

        try {
            host = args[0];
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }

        try (Socket cli = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(cli.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(cli.getInputStream());
             Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8)){

            System.out.println("Conectado a " + cli.getInetAddress() + ": " + cli.getPort());

            Request req = Request.enter();
            out.reset();
            out.writeObject(req);
            Response response = (Response) in.readObject();

            if (processResponse(response, sc)){
                while (!stop){
                    req = newRequest(sc);
                    out.reset();
                    out.writeObject(req);
                    response = (Response) in.readObject();
                    processResponse(response, sc);
                }
            }

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static Request newRequest(Scanner sc) {

        System.out.println("Menu:");
        System.out.println("1 - Listar contactos");
        System.out.println("2 - Buscar contactos");
        System.out.println("3 - Salir");
        System.out.println("ELIGE UNA OPCION:");
        int opt = sc.nextInt();

        switch (opt){
            case 1:
                return Request.list();
            case 2:
                System.out.println("Nombre:");
                String nombre = sc.next();
                return Request.buscar(nombre);
            case 3:
                return Request.quit();
            default:
                System.out.println("Opcion invalida");
                return newRequest(sc);
        }

    }

    private static boolean processResponse(Response response, Scanner sc) {

        if (response.data != null){
            List<Contacto> data = response.data;
            if (data.isEmpty()){
                System.out.println("Sin resultados");
            }else {
                for (Contacto line : data) System.out.println(line);
            }
        } else if (response.message != null) {
            System.out.println(response.message);
            if (response.message.equalsIgnoreCase("Adios")){
                stop = true;
                return false;
            }
        }

        return true;
    }
}
