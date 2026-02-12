package org.iesch.psp.HTTPSaltedPasswordHashing;// --- ServidorHTTPAuth.java ---
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ServidorHTTPAuth {

    private static final int PUERTO = 8080;
    private static final Map<String, Credential> credentials = new HashMap<>();
    // Sesiones activas: token -> usuario
    private static final Map<String, String> sesiones = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor HTTP con autenticación escuchando en el puerto " + PUERTO);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());
                new Thread(() -> atenderPeticion(clientSocket)).start();
            }

        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void atenderPeticion(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream out = clientSocket.getOutputStream()
        ) {
            // Leemos la primera línea de la petición
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                clientSocket.close();
                return;
            }
            System.out.println("Petición recibida: " + requestLine);

            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String path = parts[1];

            // Leemos cabeceras
            Map<String, String> headers = new HashMap<>();
            String headerLine;
            int contentLength = 0;
            while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
                String[] headerParts = headerLine.split(": ", 2);
                if (headerParts.length == 2) {
                    headers.put(headerParts[0], headerParts[1]);
                }
                if (headerLine.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(headerLine.split(": ")[1].trim());
                }
            }

            // Leemos body si hay Content-Length
            String body = "";
            if (contentLength > 0) {
                char[] bodyChars = new char[contentLength];
                in.read(bodyChars, 0, contentLength);
                body = new String(bodyChars);
            }

            // Enrutamiento
            switch (method + " " + path) {
                case "GET /":
                    enviarRespuesta(out, 200, "text/html",
                            "<html><body>"
                                    + "<h1>Bienvenido al Servidor HTTP con Autenticación</h1>"
                                    + "<p>Rutas disponibles:</p>"
                                    + "<ul>"
                                    + "<li>POST /register - Registrar usuario</li>"
                                    + "<li>POST /login - Iniciar sesión</li>"
                                    + "<li>GET /secreto - Recurso protegido</li>"
                                    + "</ul>"
                                    + "</body></html>");
                    break;

                case "POST /register":
                    procesarRegistro(out, body);
                    break;

                case "POST /login":
                    procesarLogin(out, body);
                    break;

                case "GET /secreto":
                    procesarSecreto(out, headers);
                    break;

                default:
                    enviarRespuesta(out, 404, "text/html",
                            "<html><body><h1>404 - Recurso no encontrado</h1></body></html>");
                    break;
            }

            clientSocket.close();

        } catch (IOException e) {
            System.out.println("Error atendiendo petición: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void procesarRegistro(OutputStream out, String body) throws IOException {
        // Parseamos los campos: user=xxx&password=xxx
        Map<String, String> params = parseFormParams(body);
        String user = params.get("user");
        String password = params.get("password");

        if (user == null || password == null || user.isEmpty() || password.isEmpty()) {
            enviarRespuesta(out, 400, "text/html",
                    "<html><body><h1>Error: Faltan campos user y/o password</h1></body></html>");
            return;
        }

        if (credentials.containsKey(user)) {
            enviarRespuesta(out, 400, "text/html",
                    "<html><body><h1>Error: El usuario ya existe</h1></body></html>");
            return;
        }

        // Generar salt y hash
        byte[] salt = PasswordUtil.generateSalt();
        byte[] hash = PasswordUtil.getHash(password, salt);

        // Guardamos salt + hash en un solo array de 64 bytes
        byte[] saveHash = new byte[64];
        System.arraycopy(salt, 0, saveHash, 0, 32);
        System.arraycopy(hash, 0, saveHash, 32, 32);

        String passwordHash = Base64.getEncoder().encodeToString(saveHash);
        credentials.put(user, new Credential(user, passwordHash));

        System.out.println("Usuario registrado: " + user);
        enviarRespuesta(out, 200, "text/html",
                "<html><body><h1>Usuario " + user + " registrado correctamente</h1></body></html>");
    }

    private static void procesarLogin(OutputStream out, String body) throws IOException {
        Map<String, String> params = parseFormParams(body);
        String user = params.get("user");
        String password = params.get("password");

        if (user == null || password == null) {
            enviarRespuesta(out, 400, "text/html",
                    "<html><body><h1>Error: Faltan campos</h1></body></html>");
            return;
        }

        if (credentials.containsKey(user)) {
            Credential credential = credentials.get(user);
            byte[] savedHash = Base64.getDecoder().decode(credential.getPasswordHash());

            byte[] salt = new byte[32];
            byte[] hash = new byte[32];
            System.arraycopy(savedHash, 0, salt, 0, 32);
            System.arraycopy(savedHash, 32, hash, 0, 32);

            byte[] checkHash = PasswordUtil.getHash(password, salt);

            boolean match = true;
            for (int i = 0; i < hash.length; i++) {
                if (hash[i] != checkHash[i]) {
                    match = false;
                    break;
                }
            }

            if (match) {
                // Generamos un token simple
                String token = Base64.getEncoder().encodeToString(
                        PasswordUtil.generateSalt());
                sesiones.put(token, user);

                System.out.println("Login correcto: " + user + " | Token: " + token);
                enviarRespuesta(out, 200, "text/html",
                        "<html><body>"
                                + "<h1>Login correcto</h1>"
                                + "<p>Tu token de sesión: " + token + "</p>"
                                + "<p>Úsalo en la cabecera Authorization: Bearer " + token + "</p>"
                                + "</body></html>");
                return;
            }
        } else {
            // Simulamos validación para evitar retardo de timing
            byte[] salt = PasswordUtil.generateSalt();
            PasswordUtil.getHash(password, salt);
        }

        enviarRespuesta(out, 401, "text/html",
                "<html><body><h1>Credenciales no válidas</h1></body></html>");
    }

    private static void procesarSecreto(OutputStream out, Map<String, String> headers) throws IOException {
        String authHeader = headers.get("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            enviarRespuesta(out, 401, "text/html",
                    "<html><body><h1>401 - No autorizado. Falta token.</h1></body></html>");
            return;
        }

        String token = authHeader.substring("Bearer ".length());

        if (sesiones.containsKey(token)) {
            String user = sesiones.get(token);
            enviarRespuesta(out, 200, "text/html",
                    "<html><body>"
                            + "<h1>Recurso Secreto</h1>"
                            + "<p>Bienvenido " + user + "!</p>"
                            + "<p>Este contenido solo es accesible para usuarios autenticados.</p>"
                            + "</body></html>");
        } else {
            enviarRespuesta(out, 401, "text/html",
                    "<html><body><h1>401 - Token no válido</h1></body></html>");
        }
    }

    private static Map<String, String> parseFormParams(String body) {
        Map<String, String> params = new HashMap<>();
        if (body == null || body.isEmpty()) return params;

        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                try {
                    params.put(
                            URLDecoder.decode(kv[0], "UTF-8"),
                            URLDecoder.decode(kv[1], "UTF-8")
                    );
                } catch (Exception e) {
                    params.put(kv[0], kv[1]);
                }
            }
        }
        return params;
    }

    private static void enviarRespuesta(OutputStream out, int statusCode, String contentType, String body)
            throws IOException {
        String statusText;
        switch (statusCode) {
            case 200: statusText = "OK"; break;
            case 400: statusText = "Bad Request"; break;
            case 401: statusText = "Unauthorized"; break;
            case 404: statusText = "Not Found"; break;
            default: statusText = "Unknown"; break;
        }

        String response = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n"
                + "Content-Type: " + contentType + "; charset=UTF-8\r\n"
                + "Content-Length: " + body.getBytes("UTF-8").length + "\r\n"
                + "Connection: close\r\n"
                + "\r\n"
                + body;

        out.write(response.getBytes("UTF-8"));
        out.flush();

        System.out.println("Respuesta enviada: " + statusCode + " " + statusText);
    }
}