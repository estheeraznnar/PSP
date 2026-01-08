import modelo.Contacto;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;





public class SrvThread implements Runnable {

    private Socket socket;
    private File workDir;

    public SrvThread(Socket socket, File workDir) {
        this.socket = socket;
        this.workDir = workDir;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()))
        {

            boolean running = true;

            while (running) {
                try {
                    Object obj = in.readObject();

                    if (!(obj instanceof Request)) {
                        out.writeObject(Response.error("Objeto invalido"));
                        out.flush();
                        continue;
                    }

                    Request req = (Request) obj;

                    switch (req.type) {
                        case LIST -> listarContactos(out);
                        case BUSCAR -> buscarContactos(out, req.argument);
                        case ENTER -> {
                            out.writeObject(Response.okMessage("Bienvenido!"));
                            out.flush();
                        }
                        case QUIT -> {
                            out.writeObject(Response.okMessage("Adios"));
                            out.flush();
                            running = false;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    out.writeObject(Response.error("Error interno"));
                    out.flush();
                }
            }

        } catch (Exception e) {
            System.out.println("Conexión terminada: " + e);
        }
    }

    private void listarContactos(ObjectOutputStream out) {
        try (BufferedReader br = new BufferedReader(new FileReader(workDir))) {
            String line;
            List<Contacto> contactos = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] datos = line.split(",");
                contactos.add(new Contacto(datos[0], Integer.parseInt(datos[1]), datos[2]));
            }
            out.writeObject(Response.okWithData(contactos));
            out.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("Archivo no encontrado");
            throw new RuntimeException(e);
        }
    }

    private void buscarContactos(ObjectOutputStream out, String nombre) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(workDir))) {
            String line;
            List<Contacto> contactos = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] datos = line.split(",");
                contactos.add(new Contacto(datos[0], Integer.parseInt(datos[1]), datos[2]));
            }
            for (Contacto contacto : contactos) {
                if (contacto.getNombre().equals(nombre)) {
                    out.reset();
                    out.writeObject(Response.okWithData(List.of(contacto)));
                    out.flush();
                    return;
                }
            }
            out.reset();
            out.writeObject(Response.error("No encontrado"));
            out.flush();
        }
    }
}
