package org.iesch.psp.HTTPHashClaims;// --- ServidorHTTPJWT.java ---
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.*;

public class ServidorHTTPJWT {

    private static final int PUERTO = 8080;
    private static final Map<String, Credential> credentials = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor HTTP con JWT en puerto " + PUERTO);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> atenderPeticion(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void atenderPeticion(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream out = clientSocket.getOutputStream()
        ) {
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                clientSocket.close();
                return;
            }
            System.out.println("Petición: " + requestLine);

            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String path = parts[1];

            // Leemos cabeceras
            Map<String, String> headers = new HashMap<>();
            String headerLine;
            int contentLength = 0;
            while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
                String[] hp = headerLine.split(": ", 2);
                if (hp.length == 2) headers.put(hp[0], hp[1]);
                if (headerLine.startsWith("Content-Length:"))
                    contentLength = Integer.parseInt(headerLine.split(": ")[1].trim());
            }

            String body = "";
            if (contentLength > 0) {
                char[] bc = new char[contentLength];
                in.read(bc, 0, contentLength);
                body = new String(bc);
            }

            switch (method + " " + path) {
                case "GET /":
                    enviarRespuesta(out, 200, "text/html",
                            "<html><body>"
                                    + "<h1>Servidor HTTP con JWT</h1>"
                                    + "<p>POST /register - Registrar usuario (user, password, role)</p>"
                                    + "<p>POST /login - Login (devuelve JWT)</p>"
                                    + "<p>GET /admin - Solo Role: Administrator</p>"
                                    + "<p>GET /user - Cualquier usuario autenticado</p>"
                                    + "</body></html>");
                    break;

                case "POST /register":
                    procesarRegistro(out, body);
                    break;

                case "POST /login":
                    procesarLogin(out, body);
                    break;

                case "GET /admin":
                    procesarRutaProtegida(out, headers, "Role", "Administrator",
                            "<h1>Panel de Administración</h1><p>Bienvenido administrador.</p>");
                    break;

                case "GET /user":
                    procesarRutaAutenticada(out, headers,
                            "<h1>Zona de Usuario</h1>");
                    break;

                default:
                    enviarRespuesta(out, 404, "text/html",
                            "<html><body><h1>404 - No encontrado</h1></body></html>");
                    break;
            }

            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void procesarRegistro(OutputStream out, String body) throws IOException {
        Map<String, String> params = parseFormParams(body);
        String user = params.get("user");
        String password = params.get("password");

        if (user == null || password == null || user.isEmpty() || password.isEmpty()) {
            enviarRespuesta(out, 400, "text/html",
                    "<html><body><h1>Error: Faltan campos</h1></body></html>");
            return;
        }

        byte[] salt = PasswordUtil.generateSalt();
        byte[] hash = PasswordUtil.getHash(password, salt);

        byte[] saveHash = new byte[64];
        System.arraycopy(salt, 0, saveHash, 0, 32);
        System.arraycopy(hash, 0, saveHash, 32, 32);

        String passwordHash = Base64.getEncoder().encodeToString(saveHash);
        credentials.put(user, new Credential(user, passwordHash));

        // Guardamos el rol como parte del user (simplificado)
        String role = params.getOrDefault("role", "User");

        System.out.println("Registrado: " + user + " con rol: " + role);
        enviarRespuesta(out, 200, "text/html",
                "<html><body><h1>Usuario " + user + " registrado (rol: " + role + ")</h1></body></html>");
    }

    private static void procesarLogin(OutputStream out, String body) throws IOException {
        Map<String, String> params = parseFormParams(body);
        String user = params.get("user");
        String password = params.get("password");
        String role = params.getOrDefault("role", "User");

        if (user == null || password == null) {
            enviarRespuesta(out, 400, "text/html",
                    "<html><body><h1>Error: Faltan campos</h1></body></html>");
            return;
        }

        if (credentials.containsKey(user)) {
            Credential cred = credentials.get(user);
            byte[] savedHash = Base64.getDecoder().decode(cred.getPasswordHash());
            byte[] salt = new byte[32];
            byte[] hash = new byte[32];
            System.arraycopy(savedHash, 0, salt, 0, 32);
            System.arraycopy(savedHash, 32, hash, 0, 32);

            byte[] checkHash = PasswordUtil.getHash(password, salt);

            boolean match = true;
            for (int i = 0; i < hash.length; i++) {
                if (hash[i] != checkHash[i]) { match = false; break; }
            }

            if (match) {
                // Generamos JWT con claims
                List<Claim> claims = new ArrayList<>();
                claims.add(new Claim("Name", user));
                claims.add(new Claim("Role", role));

                String token = JWTUtil.generateToken(claims);
                System.out.println("Login correcto: " + user + " | JWT: " + token);

                enviarRespuesta(out, 200, "text/html",
                        "<html><body>"
                                + "<h1>Login correcto</h1>"
                                + "<p>JWT: " + token + "</p>"
                                + "<p>Úsalo: Authorization: Bearer " + token + "</p>"
                                + "</body></html>");
                return;
            }
        } else {
            byte[] salt = PasswordUtil.generateSalt();
            PasswordUtil.getHash(password, salt);
        }

        enviarRespuesta(out, 401, "text/html",
                "<html><body><h1>Credenciales no válidas</h1></body></html>");
    }

    private static void procesarRutaProtegida(OutputStream out, Map<String, String> headers,
                                              String claimType, String claimValue, String contenido) throws IOException {

        List<Claim> claims = verificarToken(out, headers);
        if (claims == null) return;

        // Verificamos el claim requerido
        if (JWTUtil.hasClaim(claims, claimType, claimValue)) {
            String user = JWTUtil.getClaimValue(claims, "Name");
            enviarRespuesta(out, 200, "text/html",
                    "<html><body>" + contenido
                            + "<p>Usuario: " + user + "</p></body></html>");
        } else {
            enviarRespuesta(out, 403, "text/html",
                    "<html><body><h1>403 - Acceso denegado. Se requiere "
                            + claimType + ": " + claimValue + "</h1></body></html>");
        }
    }

    private static void procesarRutaAutenticada(OutputStream out, Map<String, String> headers,
                                                String contenido) throws IOException {

        List<Claim> claims = verificarToken(out, headers);
        if (claims == null) return;

        String user = JWTUtil.getClaimValue(claims, "Name");
        String role = JWTUtil.getClaimValue(claims, "Role");

        enviarRespuesta(out, 200, "text/html",
                "<html><body>" + contenido
                        + "<p>Usuario: " + user + " | Rol: " + role + "</p>"
                        + "<p>Claims:</p><ul>");

        // Mostramos todos los claims
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>").append(contenido);
        sb.append("<p>Usuario: ").append(user).append(" | Rol: ").append(role).append("</p>");
        sb.append("<ul>");
        for (Claim c : claims) {
            sb.append("<li>").append(c.getType()).append(" = ").append(c.getValue()).append("</li>");
        }
        sb.append("</ul></body></html>");

        enviarRespuesta(out, 200, "text/html", sb.toString());
    }

    private static List<Claim> verificarToken(OutputStream out, Map<String, String> headers) throws IOException {
        String authHeader = headers.get("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            enviarRespuesta(out, 401, "text/html",
                    "<html><body><h1>401 - No autorizado. Falta token JWT.</h1></body></html>");
            return null;
        }

        String token = authHeader.substring("Bearer ".length());
        List<Claim> claims = JWTUtil.verifyToken(token);

        if (claims == null) {
            enviarRespuesta(out, 401, "text/html",
                    "<html><body><h1>401 - Token JWT no válido o manipulado</h1></body></html>");
            return null;
        }

        return claims;
    }

    private static Map<String, String> parseFormParams(String body) {
        Map<String, String> params = new HashMap<>();
        if (body == null || body.isEmpty()) return params;
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                try {
                    params.put(URLDecoder.decode(kv[0], "UTF-8"), URLDecoder.decode(kv[1], "UTF-8"));
                } catch (Exception e) { params.put(kv[0], kv[1]); }
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
            case 403: statusText = "Forbidden"; break;
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
    }
}