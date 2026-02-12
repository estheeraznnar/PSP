package org.example.EJ2Repaso;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Clase que almacena usuarios y contraseñas hasheadas.
 * Se usa un mapa estático en memoria para simplificar el ejercicio.
 *
 * Formato: user -> saltedHash (Base64 de salt+hash)
 */
public class UserCredentials {

    // Mapa thread-safe para poder acceder desde varios hilos
    private static final Map<String, String> users = new ConcurrentHashMap<>();

    // Bloque estático: se ejecuta una vez al cargar la clase
    static {
        // Creamos 3 usuarios de prueba con contraseñas seguras
        addUser("admin", "admin123");
        addUser("user1", "password1");
        addUser("user2", "password2");
    }

    /**
     * Añade un usuario con su contraseña en formato salted hash.
     */
    public static void addUser(String user, String password) {
        String saltedHash = PasswordUtil.createSaltedHash(password);
        users.put(user, saltedHash);
    }

    /**
     * Valida si user/password son correctos usando el hash almacenado.
     *
     * @return true si la contraseña coincide, false en caso contrario.
     */
    public static boolean validate(String user, String password) {
        if (!users.containsKey(user)) return false;
        String stored = users.get(user);
        return PasswordUtil.checkPassword(password, stored);
    }
}
