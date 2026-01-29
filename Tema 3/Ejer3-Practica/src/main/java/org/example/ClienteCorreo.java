package org.example;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.Scanner;

public class ClienteCorreo {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try {
            // Pedir datos
            System.out.print("Tu correo: ");
            String miCorreo = sc.nextLine();

            System.out.print("Tu contraseña: ");
            String password = sc.nextLine();

            System.out.print("Destinatario: ");
            String destinatario = sc.nextLine();

            System.out.print("Asunto: ");
            String asunto = sc.nextLine();

            System.out.print("Mensaje: ");
            String mensaje = sc.nextLine();

            // Configurar SMTP
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            // Crear sesión
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(miCorreo, password);
                }
            });

            // Crear y enviar mensaje
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(miCorreo));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            msg.setSubject(asunto);
            msg.setText(mensaje);

            System.out.println("\nEnviando...");
            Transport.send(msg);
            System.out.println(" Correo enviado");

        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        sc.close();
    }
}

