package org.iesch.psp.Ej02;

// --- Program.java ---
public class Program {
    public static void main(String[] args) {
        // Creación de cuenta
        Credential credential = AccountUtil.createAccount();
        Auth.registrar(credential);
        System.out.println();

        // Inicio de sesión
        LoginSesion[] login = new LoginSesion[1];
        int attempts = 3;
        boolean success = false;

        while (attempts > 0) {
            if (Auth.login(login)) {
                success = true;
                break;
            } else {
                System.out.println("Credenciales no válidas\n");
                attempts--;
            }
        }

        if (success) {
            System.out.println("Usuario " + login[0].getUser() + " conectado");
        } else {
            System.out.println("Superado número de intentos. No conectado");
        }
    }
}