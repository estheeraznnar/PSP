package org.iesch.psp.SMTPAES;// --- SendEncryptedMail.java ---
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.Scanner;

public class SendEncryptedMail {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Datos del emisor
        System.out.println("Introduce la dirección de correo emisor:");
        String fromEmail = sc.nextLine().trim();

        System.out.println("Introduce la contraseña del correo emisor:");
        String password = sc.nextLine().trim();

        // Destinatario
        System.out.println("Introduce la dirección de correo destinatario:");
        String toEmail = sc.nextLine().trim();

        // Asunto
        System.out.println("Introduce el asunto del mensaje:");
        String subject = sc.nextLine().trim();

        // Cuerpo del mensaje
        System.out.println("Introduce el cuerpo del mensaje (se cifrará con AES):");
        String bodyText = sc.nextLine();

        // Ciframos el cuerpo del mensaje
        String bodyCifrado = AESUtil.encrypt(bodyText);
        System.out.println("Mensaje cifrado: " + bodyCifrado);

        // Propiedades SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Creamos la sesión con autenticación
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            // Creamos el mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail, fromEmail));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toEmail));
            message.setSubject(subject);

            // El cuerpo es el texto cifrado
            message.setText(bodyCifrado);

            // Envío del mensaje
            Transport.send(message);
            System.out.println("Correo cifrado enviado correctamente.");

            // Simulamos que el receptor descifra el mensaje
            System.out.println("\n--- RECEPTOR ---");
            System.out.println("Mensaje cifrado recibido: " + bodyCifrado);
            String bodyDescifrado = AESUtil.decrypt(bodyCifrado);
            System.out.println("Mensaje descifrado: " + bodyDescifrado);

        } catch (Exception e) {
            System.out.println("Error al enviar el correo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}