package org.example.Gestion_Usu_SALT;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestiona usuarios y contraseñas en memoria (SIN HILOS).
 *
 * createUser(): genera sal + hash PBKDF2.
 * login(): recalcula hash y compara.
 * failedAttempts: protección contra 3 fallos consecutivos.
 */
public class AccountManager {

    // Mapa simple de usuarios (sin hilos → HashMap normal)
    private final Map<String, Credential> users = new HashMap<>();

    // Contador de fallos por usuario
    private final Map<String, Integer> failedAttempts = new HashMap<>();

    /**
     * Crea usuario nuevo:
     * 1) Genera sal aleatoria.
     * 2) Calcula hash PBKDF2(password + sal).
     * 3) Guarda Credential con saltedHash.
     */
    public boolean createUser(String username, String password) {
        if (users.containsKey(username)) {
            System.out.println(" Usuario ya existe: " + username);
            return false;
        }

        // 1. Generar salted hash
        String saltedHash = PasswordUtil.createSaltedHash(password);

        // 2. Crear y guardar credential
        Credential cred = new Credential(username, saltedHash);
        users.put(username, cred);

        // 3. Resetear intentos fallidos
        failedAttempts.remove(username);

        System.out.println(" Usuario creado: " + username);
        return true;
    }

    /**
     * Login:
     * 1) Busca usuario.
     * 2) Recalcula hash con sal guardada.
     * 3) Compara con saltedHash.
     * 4) Bloquea tras 3 fallos.
     */
    public boolean login(String username, String password) {
        Credential cred = users.get(username);
        if (cred == null) {
            System.out.println(" Usuario no encontrado: " + username);
            return false;
        }

        // ¿Está bloqueado?
        int attempts = failedAttempts.getOrDefault(username, 0);
        if (attempts >= 3) {
            System.out.println(" Usuario bloqueado (3 fallos consecutivos)");
            return false;
        }

        // Verificar contraseña
        boolean ok = PasswordUtil.checkPassword(password, cred.getSaltedHash());

        if (ok) {
            failedAttempts.remove(username);
            System.out.println(" Login correcto: " + username);
            return true;
        } else {
            failedAttempts.put(username, attempts + 1);
            System.out.println(" Contraseña incorrecta. Intentos: " +
                    (attempts + 1) + "/3");
            return false;
        }
    }

    /**
     * Lista usuarios registrados.
     */
    public void listUsers() {
        if (users.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
        } else {
            System.out.println("Usuarios registrados:");
            for (Credential cred : users.values()) {
                System.out.println("- " + cred.getUsername());
            }
        }
    }
}

