// --- ServidorDeAutenticacionConcurrente/AuthServerThread.java ---
package org.iesch.psp.ServidordeAutenticaciOnConcurrente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Hilo que atiende a cada cliente conectado
 * Basado en CubeMultiSvrThread del PDF (página 37)
 * y CalcSvrThread (páginas 46-48)
 */
public class AuthServerThread implements Runnable {

    // Socket para comunicación con el cliente
    private Socket socket;

    // Referencia a la base de datos compartida (thread-safe)
    private BaseDatosUsuarios bd;

    // Flag para indicar parada del hilo
    private boolean stop = false;

    /**
     * Constructor que recibe el socket del cliente y la BD compartida
     */
    public AuthServerThread(Socket socket, BaseDatosUsuarios bd) {
        this.socket = socket;
        this.bd = bd;
    }

    @Override
    public void run() {
        // Usar try-with-resources para streams (se cierran automáticamente)
        // Como en el ejemplo de la calculadora (página 46)
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("[" + Thread.currentThread().getName()
                    + "] Cliente conectado desde " + socket.getInetAddress());

            // Bucle de atención al cliente mientras no pare
            while (!stop) {
                // Leer petición del cliente (objeto serializado)
                AuthRequest req = (AuthRequest) in.readObject();

                // Procesar la petición y generar respuesta
                AuthResponse resp = procesarPeticion(req);

                // Limpiar stream antes de enviar (recomendado en bucles)
                out.reset();

                // Enviar respuesta al cliente
                out.writeObject(resp);
            }

        } catch (IOException e) {
            System.out.println("[" + Thread.currentThread().getName()
                    + "] Cliente desconectado");
        } catch (ClassNotFoundException e) {
            System.out.println("[" + Thread.currentThread().getName()
                    + "] Error: clase no encontrada");
        }

        // Cerrar el socket del cliente
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Procesa una petición y genera la respuesta correspondiente
     * Similar al método processRequest de CalcSvrThread (página 47)
     */
    private AuthResponse procesarPeticion(AuthRequest req) {
        boolean ok;
        String mensaje;

        // Evaluar el tipo de petición
        switch (req.getType()) {

            case REGISTER:
                // Intentar registrar el usuario
                if (bd.registrar(req.getUsuario(), req.getPassword())) {
                    ok = true;
                    mensaje = "Usuario " + req.getUsuario() + " registrado correctamente";
                    System.out.println("[" + Thread.currentThread().getName()
                            + "] REGISTER OK: " + req.getUsuario());
                } else {
                    ok = false;
                    mensaje = "Error: El usuario " + req.getUsuario() + " ya existe";
                    System.out.println("[" + Thread.currentThread().getName()
                            + "] REGISTER FAIL: usuario ya existe");
                }
                break;

            case LOGIN:
                // Intentar iniciar sesión
                int resultado = bd.login(req.getUsuario(), req.getPassword());

                switch (resultado) {
                    case 0: // Éxito
                        ok = true;
                        mensaje = "Bienvenido " + req.getUsuario();
                        System.out.println("[" + Thread.currentThread().getName()
                                + "] LOGIN OK: " + req.getUsuario());
                        break;
                    case 1: // Credenciales incorrectas
                        ok = false;
                        int restantes = bd.getIntentosRestantes(req.getUsuario());
                        mensaje = "Credenciales incorrectas. Intentos restantes: " + restantes;
                        System.out.println("[" + Thread.currentThread().getName()
                                + "] LOGIN FAIL: credenciales incorrectas");
                        break;
                    case 2: // Cuenta bloqueada
                        ok = false;
                        mensaje = "Cuenta bloqueada por demasiados intentos fallidos";
                        System.out.println("[" + Thread.currentThread().getName()
                                + "] LOGIN FAIL: cuenta bloqueada");
                        break;
                    default: // Usuario no existe
                        ok = false;
                        mensaje = "El usuario no existe";
                        System.out.println("[" + Thread.currentThread().getName()
                                + "] LOGIN FAIL: usuario no existe");
                }
                break;

            case LOGOUT:
                // Cerrar sesión
                ok = true;
                mensaje = "Sesión cerrada. Hasta pronto!";
                stop = true; // Indicar fin del bucle
                System.out.println("[" + Thread.currentThread().getName()
                        + "] LOGOUT");
                break;

            default:
                ok = false;
                mensaje = "Operación no reconocida";
        }

        // Crear y devolver la respuesta
        return new AuthResponse(req, ok, mensaje);
    }
}