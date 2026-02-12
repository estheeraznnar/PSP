package org.example.Gestion_Usu_SALT;

/*Ejercicio 5 – Gestión de usuarios con contraseñas con salt
Tenemos una aplicación que consta de las clases PasswordUtil, Credential, AccountManager y MainLogin. La idea es similar al ejemplo de UD4 pero adaptado a tipo examen.

PasswordUtil: Ya implementada según el tema (PBKDF2WithHmacSHA256 con sal de 32 bytes).
Credential: Clase que representa un usuario con:
    String username
    String saltedHash (Base64 con salt+hash)

AccountManager: Mantiene una lista de usuarios en memoria (Map<String, Credential>) y permite:
    crear usuarios nuevos (createUser)
    validar logins (login)

MainLogin: Programa con menú:
    Crear usuario
    Iniciar sesión
    Salir

Tareas:

    Implementa AccountManager.createUser(String user, String password) que:
        genere la sal
        calcule el hash
        guarde to do en un objeto Credential
    (3 puntos)

    Implementa AccountManager.login(String user, String password) que:
        busque el usuario
        recalculé el hash con la sal y compare
    (3 puntos)

    Implementa en MainLogin el menú de consola con 3 intentos máximos de login y mensajes adecuados. (2 puntos)
    Añade una pequeña protección: si el usuario falla el login 3 veces, no permitir más intentos para ese usuario durante la ejecución. (2 puntos)
*/

import java.util.Scanner;

/**
 * Programa principal SIN HILOS.
 *
 * Menú secuencial:
 * 1) Crear usuario
 * 2) Login (3 intentos máximo)
 * 3) Listar usuarios
 * 0) Salir
 */
public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AccountManager manager = new AccountManager();

        System.out.println("=== Sistema de Login con Salt (PBKDF2) ===");

        while (true) {
            showMenu();
            String option = sc.nextLine().trim();

            switch (option) {
                case "1":
                    System.out.print("Usuario: ");
                    String user = sc.nextLine().trim();
                    System.out.print("Contraseña: ");
                    String pass = sc.nextLine();
                    manager.createUser(user, pass);
                    break;

                case "2":
                    loginAttempt(sc, manager);
                    break;

                case "3":
                    manager.listUsers();
                    break;

                case "0":
                    System.out.println("¡Hasta luego!");
                    sc.close();
                    return;

                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private static void showMenu() {
        System.out.println("\n--- MENÚ ---");
        System.out.println("1) Crear usuario");
        System.out.println("2) Iniciar sesión");
        System.out.println("3) Listar usuarios");
        System.out.println("0) Salir");
        System.out.print("Opción: ");
    }

    /**
     * Intenta login con máximo 3 intentos.
     */
    private static void loginAttempt(Scanner sc, AccountManager manager) {
        System.out.print("Usuario: ");
        String user = sc.nextLine().trim();
        System.out.print("Contraseña: ");
        String pass = sc.nextLine();

        // Un único intento por sesión de login
        // El bloqueo de 3 fallos ya está en AccountManager
        boolean ok = manager.login(user, pass);
        if (ok) {
            System.out.println("¡Bienvenido, " + user + "!");
        }
    }
}

