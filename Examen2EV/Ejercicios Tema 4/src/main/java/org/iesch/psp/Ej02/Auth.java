package org.iesch.psp.Ej02;// --- Auth.java ---
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Auth {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, Credential> credentials = new HashMap<>();

    public static void registrar(Credential credential) {
        credentials.put(credential.getUser(), credential);
    }

    public static boolean login(LoginSesion[] userSession) {
        userSession[0] = null;
        System.out.println("Inicio de sesión");

        System.out.println("Introduce nombre de usuario:");
        String user = scanner.nextLine().trim();

        System.out.println("Introduce la contraseña:");
        String password = scanner.nextLine();

        if (credentials.containsKey(user)) {
            Credential credential = credentials.get(user);
            byte[] savedHash = Base64.getDecoder().decode(credential.getPasswordHash());

            byte[] salt = new byte[32];
            byte[] hash = new byte[32];
            System.arraycopy(savedHash, 0, salt, 0, 32);
            System.arraycopy(savedHash, 32, hash, 0, 32);

            byte[] checkHash = PasswordUtil.getHash(password, salt);

            for (int i = 0; i < hash.length; i++) {
                if (hash[i] != checkHash[i]) {
                    return false;
                }
            }
            userSession[0] = new LoginSesion(user);
            return true;
        } else {
            // Simulamos validación para evitar retardo de timing
            byte[] salt = PasswordUtil.generateSalt();
            PasswordUtil.getHash(password, salt);
            return false;
        }
    }
}