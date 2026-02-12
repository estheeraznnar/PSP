package org.iesch.psp.Ej02;// --- AccountUtil.java ---
import java.util.Base64;
import java.util.Scanner;

public class AccountUtil {
    private static final Scanner scanner = new Scanner(System.in);

    public static Credential createAccount() {
        System.out.println("Creación de una nueva cuenta");

        String user = "";
        while (user.isEmpty()) {
            System.out.println("Introduce nombre de usuario:");
            user = scanner.nextLine().trim();
        }

        String password = "";
        boolean match = false;
        while (!match) {
            while (password.isEmpty()) {
                System.out.println("Introduce contraseña:");
                password = scanner.nextLine();
            }
            String confirm = "";
            while (confirm.isEmpty()) {
                System.out.println("Confirma la contraseña:");
                confirm = scanner.nextLine();
            }
            match = password.equals(confirm);
            if (!match) {
                System.out.println("Las contraseñas no coinciden. Vuelve a intentarlo.");
                password = "";
            }
        }

        byte[] salt = PasswordUtil.generateSalt();
        byte[] hash = PasswordUtil.getHash(password, salt);

        byte[] saveHash = new byte[64];
        System.arraycopy(salt, 0, saveHash, 0, 32);
        System.arraycopy(hash, 0, saveHash, 32, 32);

        String passwordHash = Base64.getEncoder().encodeToString(saveHash);
        return new Credential(user, passwordHash);
    }
}