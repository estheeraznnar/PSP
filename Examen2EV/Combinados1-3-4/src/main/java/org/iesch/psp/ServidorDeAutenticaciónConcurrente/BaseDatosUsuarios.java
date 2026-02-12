package org.iesch.psp.ServidorDeAutenticaciónConcurrente;// --- BaseDatosUsuarios.java (ConcurrentHashMap thread-safe) ---
import java.util.concurrent.ConcurrentHashMap;

public class BaseDatosUsuarios {

    // ConcurrentHashMap para acceso thread-safe
    private ConcurrentHashMap<String, String> usuarios = new ConcurrentHashMap<>();

    // Control de intentos fallidos (también thread-safe)
    private ConcurrentHashMap<String, Integer> intentosFallidos = new ConcurrentHashMap<>();

    private static final int MAX_INTENTOS = 3;

    public synchronized boolean registrar(String usuario, String password) {
        if (usuarios.containsKey(usuario)) {
            return false; // Usuario ya existe
        }
        String hashPassword = PasswordUtil.hashPassword(password);
        usuarios.put(usuario, hashPassword);
        return true;
    }

    public synchronized int login(String usuario, String password) {
        // 0 = éxito, 1 = credenciales incorrectas, 2 = cuenta bloqueada, 3 = usuario no existe

        if (!usuarios.containsKey(usuario)) {
            return 3;
        }

        // Verificar si está bloqueado
        int intentos = intentosFallidos.getOrDefault(usuario, 0);
        if (intentos >= MAX_INTENTOS) {
            return 2; // Bloqueado
        }

        String hashAlmacenado = usuarios.get(usuario);
        if (PasswordUtil.checkPassword(password, hashAlmacenado)) {
            intentosFallidos.put(usuario, 0); // Reiniciar contador
            return 0; // Éxito
        } else {
            intentosFallidos.put(usuario, intentos + 1);
            return 1; // Credenciales incorrectas
        }
    }

    public int getIntentosRestantes(String usuario) {
        int intentos = intentosFallidos.getOrDefault(usuario, 0);
        return MAX_INTENTOS - intentos;
    }
}