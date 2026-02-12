package org.example;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.Scanner;

/**
 * Cliente SMTP para enviar correos electrónicos usando JavaMail API.
 */
public class SMTP {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Solicitar datos de conexión y autenticación
        System.out.print("Servidor SMTP (ej: smtp.gmail.com): ");
        String host = "smtp.gmail.com";

        System.out.print("Puerto (587 para TLS / 465 para SSL): ");
        int puerto = Integer.parseInt(sc.nextLine().trim());

        System.out.print("Tu email (emisor): ");
        String emailEmisor = sc.nextLine().trim();

        System.out.print("Contraseña (o App Password): ");
        String password = sc.nextLine().trim();

        // Solicitar datos del correo
        System.out.print("Email destinatario: ");
        String destinatario = sc.nextLine().trim();

        System.out.print("Asunto: ");
        String asunto = sc.nextLine().trim();

        // Leer mensaje multilínea hasta línea vacía
        System.out.println("Escribe el mensaje (línea vacía para terminar):");
        StringBuilder cuerpo = new StringBuilder();
        String linea;
        while (!(linea = sc.nextLine()).isEmpty()) {
            cuerpo.append(linea).append("\n");
        }

        // Configurar propiedades del servidor SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", puerto);
        props.put("mail.smtp.auth", "true");

        // Configurar cifrado según el puerto
        if (puerto == 587) {
            props.put("mail.smtp.starttls.enable", "true"); // TLS explícito (STARTTLS)
        } else if (puerto == 465) {
            props.put("mail.smtp.ssl.enable", "true"); // SSL implícito
        }

        // Crear sesión con autenticación
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailEmisor, password);
            }
        });

        try {
            // Construir mensaje de correo
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(emailEmisor));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject(asunto);
            mensaje.setText(cuerpo.toString());

            // Enviar correo
            Transport.send(mensaje);
            System.out.println("Correo enviado correctamente ✓");

        } catch (MessagingException e) {
            System.out.println("Error al enviar correo: " + e.getMessage());
            e.printStackTrace();
        }

        sc.close();
    }
}
