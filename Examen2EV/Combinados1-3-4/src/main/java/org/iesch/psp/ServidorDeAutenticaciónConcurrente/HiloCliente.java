package org.iesch.psp.ServidorDeAutenticaciónConcurrente;// --- HiloCliente.java (Atiende cada cliente) ---
import java.io.*;
import java.net.*;

public class HiloCliente implements Runnable {

    private Socket socket;
    private BaseDatosUsuarios bd;

    public HiloCliente(Socket socket, BaseDatosUsuarios bd) {
        this.socket = socket;
        this.bd = bd;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println("=== SERVIDOR DE AUTENTICACIÓN ===");
            out.println("Comandos: REGISTER usuario password | LOGIN usuario password | LOGOUT");

            String linea;
            boolean conectado = true;
            String usuarioActual = null;

            while (conectado && (linea = in.readLine()) != null) {
                String[] partes = linea.split(" ");
                String comando = partes[0].toUpperCase();

                switch (comando) {
                    case "REGISTER":
                        if (partes.length < 3) {
                            out.println("ERROR: Uso: REGISTER usuario password");
                        } else {
                            String user = partes[1];
                            String pass = partes[2];
                            if (bd.registrar(user, pass)) {
                                out.println("OK: Usuario " + user + " registrado correctamente.");
                                System.out.println("[" + Thread.currentThread().getName()
                                        + "] Registrado: " + user);
                            } else {
                                out.println("ERROR: El usuario " + user + " ya existe.");
                            }
                        }
                        break;

                    case "LOGIN":
                        if (partes.length < 3) {
                            out.println("ERROR: Uso: LOGIN usuario password");
                        } else {
                            String user = partes[1];
                            String pass = partes[2];
                            int resultado = bd.login(user, pass);

                            switch (resultado) {
                                case 0:
                                    usuarioActual = user;
                                    out.println("OK: Bienvenido " + user);
                                    System.out.println("[" + Thread.currentThread().getName()
                                            + "] Login exitoso: " + user);
                                    break;
                                case 1:
                                    int restantes = bd.getIntentosRestantes(user);
                                    out.println("ERROR: Credenciales incorrectas. Intentos restantes: " + restantes);
                                    System.out.println("[" + Thread.currentThread().getName()
                                            + "] Login fallido: " + user);
                                    break;
                                case 2:
                                    out.println("ERROR: Cuenta bloqueada por demasiados intentos fallidos.");
                                    break;
                                case 3:
                                    out.println("ERROR: Usuario no existe.");
                                    break;
                            }
                        }
                        break;

                    case "LOGOUT":
                        if (usuarioActual != null) {
                            out.println("OK: Hasta luego " + usuarioActual);
                            System.out.println("[" + Thread.currentThread().getName()
                                    + "] Logout: " + usuarioActual);
                            usuarioActual = null;
                        }
                        conectado = false;
                        break;

                    default:
                        out.println("ERROR: Comando desconocido.");
                        break;
                }
            }

            socket.close();
            System.out.println("[" + Thread.currentThread().getName() + "] Cliente desconectado.");

        } catch (IOException e) {
            System.out.println("Error con cliente: " + e.getMessage());
        }
    }
}