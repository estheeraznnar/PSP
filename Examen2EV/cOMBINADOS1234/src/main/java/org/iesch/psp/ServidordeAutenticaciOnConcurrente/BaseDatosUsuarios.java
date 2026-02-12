// --- ServidorDeAutenticacionConcurrente/BaseDatosUsuarios.java ---
package org.iesch.psp.ServidordeAutenticaciOnConcurrente;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Base de datos de usuarios thread-safe usando ConcurrentHashMap
 * Implementa control de intentos fallidos (máximo 3)
 * Basado en conceptos del Tema 1 (colecciones concurrentes)
 */
public class BaseDatosUsuarios {

    // ConcurrentHashMap para acceso thread-safe a los usuarios
    // Clave: nombre de usuario, Valor: hash de contraseña
    private ConcurrentHashMap<String, String> usuarios = new ConcurrentHashMap<>();

    // Control de intentos fallidos por usuario
    private ConcurrentHashMap<String, Integer> intentosFallidos = new ConcurrentHashMap<>();

    // Máximo de intentos permitidos antes de bloquear
    private static final int MAX_INTENTOS = 3;

    /**
     * Registra un nuevo usuario en el sistema
     * Método sincronizado para evitar registros duplicados
     * @param usuario Nombre de usuario
     * @param password Contraseña en texto plano
     * @return true si se registró correctamente, false si ya existe
     */
    public synchronized boolean registrar(String usuario, String password) {
        // Verificar si el usuario ya existe
        if (usuarios.containsKey(usuario)) {
            return false;
        }
        // Generar hash de la contraseña y almacenar
        String hashPassword = PasswordUtil.hashPassword(password);
        usuarios.put(usuario, hashPassword);
        return true;
    }

    /**
     * Intenta iniciar sesión con las credenciales proporcionadas
     * @param usuario Nombre de usuario
     * @param password Contraseña
     * @return Código de resultado:
     *         0 = éxito
     *         1 = credenciales incorrectas
     *         2 = cuenta bloqueada
     *         3 = usuario no existe
     */
    public synchronized int login(String usuario, String password) {
        // Verificar si el usuario existe
        if (!usuarios.containsKey(usuario)) {
            return 3; // Usuario no existe
        }

        // Verificar si está bloqueado por demasiados intentos
        int intentos = intentosFallidos.getOrDefault(usuario, 0);
        if (intentos >= MAX_INTENTOS) {
            return 2; // Cuenta bloqueada
        }

        // Obtener hash almacenado y verificar contraseña
        String hashAlmacenado = usuarios.get(usuario);
        if (PasswordUtil.checkPassword(password, hashAlmacenado)) {
            // Login exitoso - reiniciar contador de intentos
            intentosFallidos.put(usuario, 0);
            return 0; // Éxito
        } else {
            // Login fallido - incrementar contador
            intentosFallidos.put(usuario, intentos + 1);
            return 1; // Credenciales incorrectas
        }
    }

    /**
     * Obtiene los intentos restantes para un usuario
     * @param usuario Nombre de usuario
     * @return Número de intentos restantes
     */
    public int getIntentosRestantes(String usuario) {
        int intentos = intentosFallidos.getOrDefault(usuario, 0);
        return MAX_INTENTOS - intentos;
    }
}