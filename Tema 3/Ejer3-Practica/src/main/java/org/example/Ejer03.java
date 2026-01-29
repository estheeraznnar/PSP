package org.example;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.Scanner;

public class Ejer03 {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 1. Solicitar datos al usuario
        System.out.print("Correo emisor: ");
        String emisor = sc.nextLine();
        System.out.print("Contraseña (o token de aplicación): ");
        String password = sc.nextLine();
        System.out.print("Correo destinatario: ");
        String destinatario = sc.nextLine();
        System.out.print("Asunto: ");
        String asunto = sc.nextLine();
        System.out.print("Cuerpo del mensaje: ");
        String cuerpo = sc.nextLine();

        // 2. Configuración de propiedades para el servidor SMTP (ejemplo con Gmail)
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // 3. Crear la sesión con autenticación
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emisor, password);
            }
        });

        try {
            // 4. Construir el mensaje
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(emisor));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject(asunto);
            mensaje.setText(cuerpo);

            // 5. Enviar el correo
            Transport.send(mensaje);
            System.out.println("¡Éxito! El correo ha sido enviado correctamente.");

        } catch (MessagingException e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }
    }
}
